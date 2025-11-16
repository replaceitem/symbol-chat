package net.replaceitem.symbolchat.mixin.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.replaceitem.symbolchat.extensions.ScreenAccess;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.config.Config;
import net.replaceitem.symbolchat.extensions.SymbolEditableWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements SymbolInsertable, SymbolSuggestable.TextFieldWidgetSymbolSuggestable {
    @Shadow protected EditBox input;

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
        ((SymbolEditableWidget) this.input).setRefreshSuggestions(() -> ((ScreenAccess) this).refreshSuggestions());
        ((SymbolEditableWidget) this.input).setFontProcessorSupplier(() -> ((ScreenAccess) this).getFontProcessor());

        ((SymbolEditableWidget) this.input).setConvertFontsPredicate(this::isInMessage);
    }
    
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handleSuggestorKeyPressed(input)) cir.setReturnValue(true);
    }
    
    @WrapOperation(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z"))
    public boolean skipArrowKeys(ChatScreen instance, KeyEvent input, Operation<Boolean> original) {
        // Prevent arrow keys changing focus when they should move through chat history 
        return ((!input.isUp() && !input.isDown()) || !this.input.isFocused()) && original.call(instance, input);
    }
    
    @Unique
    private static final int TEXT_BOX_CURSOR_WIDTH = 8;

    @ModifyConstant(method = "init",constant = @Constant(intValue = 4, ordinal = 1),require = 1)
    private int changeTextBoxWidth(int original) {
        int symbolButtonWidth = switch (SymbolChat.config.symbolButtonPosition.get().getVertical()) {
            case TOP -> 0;
            case BOTTOM -> SymbolButtonWidget.SYMBOL_SIZE + 2;
        };
        return original + TEXT_BOX_CURSOR_WIDTH + symbolButtonWidth + 2;
    }
    @ModifyConstant(method = "init",constant = @Constant(intValue = 4, ordinal = 0),require = 1)
    private int changeTextBoxX(int original) {
        Config.HudCorner symbolButtonPosition = SymbolChat.config.symbolButtonPosition.get();
        if(symbolButtonPosition.getVertical() == Config.HudVerticalSide.TOP) return original;
        return switch(symbolButtonPosition.getHorizontal()) {
            case LEFT -> original + SymbolButtonWidget.SYMBOL_SIZE + 2;
            case RIGHT -> original;
        };
    }
    
    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"))
    private void fillBackgroundAtTextBox(Args args) {
        args.set(0, this.input.getX() - 2);
        args.set(1, this.input.getY() - 2);
        args.set(2, this.input.getRight() + TEXT_BOX_CURSOR_WIDTH);
        args.set(3, this.input.getBottom() - 2);
    }

    @Inject(method = "mouseScrolled", at = @At(value = "HEAD"), cancellable = true)
    public void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if(super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) cir.setReturnValue(true);
    }
    
    @Override
    public void insertSymbol(String symbol) {
        this.input.insertText(symbol);
        if(minecraft != null) this.minecraft.schedule(() -> {
            if(minecraft.screen == this) this.setFocused(this.input);
        });
    }

    @Override
    public void focusTextbox() {
        if(minecraft != null) this.minecraft.schedule(() -> {
            if(minecraft.screen == this) this.setFocused(this.input);
        });
    }

    @Override
    public boolean suggestionsDisabled() {
        String text = this.input.getValue();
        return !isInMessage(text, null);
    }

    @Override
    public EditBox getTextField() {
        return input;
    }

    private boolean isInMessage(String text, @Nullable String insert) {
        // Never convert slashes at the start for commands
        if(text.isEmpty() && Objects.equals(insert, "/")) return false;

        StringReader stringReader = new StringReader(this.input.getValue());
        boolean startsWithSlash = stringReader.canRead() && stringReader.peek() == '/';
        if (startsWithSlash) {
            stringReader.skip();
        }

        if (startsWithSlash && this.input.getCursorPosition() > 0 && this.minecraft != null && this.minecraft.player != null) {
            try {
                CommandDispatcher<ClientSuggestionProvider> commandDispatcher = this.minecraft.player.connection.getCommands();
                ParseResults<ClientSuggestionProvider> parse = commandDispatcher.parse(stringReader, this.minecraft.player.connection.getSuggestionsProvider());
                CommandNode<ClientSuggestionProvider> parent = parse.getContext().findSuggestionContext(this.input.getCursorPosition()).parent;
                return parent.getChildren().stream().anyMatch(node -> {
                    if (!(node instanceof ArgumentCommandNode<?, ?> argumentCommandNode)) return false;
                    ArgumentType<?> type = argumentCommandNode.getType();
                    if(type instanceof StringArgumentType stringArgument && stringArgument.getType() == StringArgumentType.StringType.GREEDY_PHRASE) return true;
                    return type instanceof MessageArgument;
                });
            } catch (Exception e) {
                SymbolChat.LOGGER.error(e);
            }
        }
        return true;
    }
}
