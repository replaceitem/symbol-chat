package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.ScreenAccess;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.regex.PatternSyntaxException;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements SymbolInsertable, SymbolSuggestable.TextFieldWidgetSymbolSuggestable {
    @Shadow protected TextFieldWidget chatField;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
    }
    
    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;mouseClicked(DDI)Z"), cancellable = true)
    private void callSuperMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        // Directly call super.mouseClicked instead of chatField.mouseClicked first.
        // Since chatField is also in this.children(), this means that clicking the chat box will actually change focus like any other element.
        cir.setReturnValue(super.mouseClicked(mouseX, mouseY, button));
    }
    
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handleSuggestorKeyPressed(keyCode, scanCode, modifiers)) cir.setReturnValue(true);
    }

    @ModifyConstant(method = "init",constant = @Constant(intValue = 4, ordinal = 1),require = 1)
    private int changeTextBoxWidth(int original) {
        return original + SymbolButtonWidget.SYMBOL_SIZE + 8;
    }
    @ModifyConstant(method = "render",constant = @Constant(intValue = 2, ordinal = 1),require = 1)
    private int changeChatBoxFillWidth(int original) {
        return original + SymbolButtonWidget.SYMBOL_SIZE + 2;
    }

    @Inject(method = "mouseScrolled", at = @At(value = "HEAD"), cancellable = true)
    public void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if(super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) cir.setReturnValue(true);
    }
    
    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"))
    private void onOnChatFieldUpdate(String chatText, CallbackInfo ci) {
        ((ScreenAccess) this).refreshSuggestions();
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
        try {
            return !text.matches(SymbolChat.config.getChatSuggestionRegex());
        } catch (PatternSyntaxException e) {
            SymbolChat.LOGGER.error("Invalid regex provided as the Chat Suggestion Regex", e);
            return false;
        }
    }

    @Override
    public TextFieldWidget getTextField() {
        return chatField;
    }
}
