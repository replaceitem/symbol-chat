package net.replaceitem.symbolchat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.ScreenAccess;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.config.Config;
import net.replaceitem.symbolchat.SymbolEditableWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Debug(export = true)
@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements SymbolInsertable, SymbolSuggestable.TextFieldWidgetSymbolSuggestable {
    @Shadow protected TextFieldWidget chatField;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
        ((SymbolEditableWidget) this.chatField).setFontProcessorSupplier(() -> ((ScreenAccess) this).getFontProcessor());
        ((SymbolEditableWidget) this.chatField).setRefreshSuggestions(() -> ((ScreenAccess) this).refreshSuggestions());
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;mouseClicked(DDI)Z"), cancellable = true)
    private void callSuperMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
    
    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;mouseClicked(DDI)Z"))
    private boolean clickScreenAfterInput(ChatInputSuggestor instance, double mouseX, double mouseY, int button, Operation<Boolean> original) {
        boolean result = original.call(instance, mouseX, mouseY, button);
        return result || super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handleSuggestorKeyPressed(keyCode, scanCode, modifiers)) cir.setReturnValue(true);
    }
    
    @WrapOperation(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(III)Z"))
    public boolean skipArrowKeys(ChatScreen instance, int keyCode, int scanCode, int modifiers, Operation<Boolean> original) {
        // Prevent arrow keys changing focus when they should move through chat history 
        return ((keyCode != GLFW.GLFW_KEY_UP && keyCode != GLFW.GLFW_KEY_DOWN) || !this.chatField.isFocused()) && original.call(this, keyCode, scanCode, modifiers);
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
    public boolean disabled() {
        String text = this.chatField.getText();
        return !SymbolChat.config.chatSuggestionPattern.get().matcher(text).matches();
    }

    @Override
    public TextFieldWidget getTextField() {
        return chatField;
    }
}
