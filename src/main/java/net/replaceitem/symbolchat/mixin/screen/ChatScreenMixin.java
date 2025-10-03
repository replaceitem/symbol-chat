package net.replaceitem.symbolchat.mixin.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.extensions.ScreenAccess;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.config.Config;
import net.replaceitem.symbolchat.extensions.SymbolEditableWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
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
    @Shadow protected TextFieldWidget chatField;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
        ((SymbolEditableWidget) this.chatField).setRefreshSuggestions(() -> ((ScreenAccess) this).refreshSuggestions());
        ((SymbolEditableWidget) this.chatField).setFontProcessorSupplier(() -> ((ScreenAccess) this).getFontProcessor());

        ((SymbolEditableWidget) this.chatField).setConvertFontsPredicate((text, insert) -> {
            // Never convert slashes at the start for commands
            if(text.isEmpty() && Objects.equals(insert, "/")) return false;
            return SymbolChat.config.fontConversionPattern.get().matcher(text).matches();
        });
    }
    
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handleSuggestorKeyPressed(input)) cir.setReturnValue(true);
    }
    
    @WrapOperation(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(Lnet/minecraft/client/input/KeyInput;)Z"))
    public boolean skipArrowKeys(ChatScreen instance, KeyInput input, Operation<Boolean> original) {
        // Prevent arrow keys changing focus when they should move through chat history 
        return ((!input.isUp() && !input.isDown()) || !this.chatField.isFocused()) && original.call(instance, input);
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
    
    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    private void fillBackgroundAtTextBox(Args args) {
        args.set(0, this.chatField.getX() - 2);
        args.set(1, this.chatField.getY() - 2);
        args.set(2, this.chatField.getRight() + TEXT_BOX_CURSOR_WIDTH);
        args.set(3, this.chatField.getBottom() - 2);
    }

    @Inject(method = "mouseScrolled", at = @At(value = "HEAD"), cancellable = true)
    public void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if(super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) cir.setReturnValue(true);
    }
    
    @Override
    public void insertSymbol(String symbol) {
        this.chatField.write(symbol);
        if(client != null) this.client.send(() -> {
            if(client.currentScreen == this) this.setFocused(this.chatField);
        });
    }

    @Override
    public void focusTextbox() {
        if(client != null) this.client.send(() -> {
            if(client.currentScreen == this) this.setFocused(this.chatField);
        });
    }

    @Override
    public boolean disabled() {
        String text = this.chatField.getText();
        return !SymbolChat.config.chatSuggestionPattern.get().matcher(text).matches();
    }

    @Override
    public TextFieldWidget getTextField() {
        return chatField;
    }
}
