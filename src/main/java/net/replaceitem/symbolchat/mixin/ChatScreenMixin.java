package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.ScreenAccess;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;
import java.util.regex.PatternSyntaxException;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements Consumer<String>, SymbolSuggestable.TextFieldWidgetSymbolSuggestable {
    @Shadow protected TextFieldWidget chatField;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
    }
    
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).onKeyPressed(keyCode, scanCode, modifiers)) cir.setReturnValue(true);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(((ScreenAccess) this).onCharTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        if (!((ScreenAccess) this).isSymbolChatWidget(focused)) super.setFocused(focused);
    }

    @Override
    protected void switchFocus(GuiNavigationPath path) {
        // we don't want focus to leave the chat box (breaks with the symbol chat widgets when using arrow keys)
        GuiNavigationPath tempPath = path;
        while(tempPath instanceof GuiNavigationPath.IntermediaryNode intermediaryNode && intermediaryNode != intermediaryNode.childPath()) {
            tempPath = intermediaryNode.childPath();
        }
        if(!((ScreenAccess) this).isSymbolChatWidget(this.chatField)) super.switchFocus(path);
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
    public void onMouseScrolled(double mouseX, double mouseY, double amount, CallbackInfoReturnable<Boolean> cir) {
        if(super.mouseScrolled(mouseX, mouseY, amount)) cir.setReturnValue(true);
    }
    
    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"))
    private void onOnChatFieldUpdate(String chatText, CallbackInfo ci) {
        ((ScreenAccess) this).refreshSuggestions();
    }
    
    @Override
    public void accept(String s) {
        this.chatField.write(s);
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
