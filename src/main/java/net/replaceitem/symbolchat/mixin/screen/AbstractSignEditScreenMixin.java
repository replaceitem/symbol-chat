package net.replaceitem.symbolchat.mixin.screen;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.replaceitem.symbolchat.extensions.ScreenAccess;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.resource.FontProcessor;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin extends Screen implements SymbolInsertable, SymbolSuggestable.SelectionManagerSymbolSuggestable {
    @Shadow private TextFieldHelper signField;
    @Shadow private int line;
    @Shadow @Final private String[] messages;
    @Shadow @Final protected SignBlockEntity sign;

    @Shadow protected abstract Vector3f getSignTextScale();

    protected AbstractSignEditScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "HEAD"))
    private void onInit(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handleKeyPressed(input)) cir.setReturnValue(true);
    }
    
    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    public void charTyped(CharacterEvent input, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handlePanelCharTyped(input)) cir.setReturnValue(true);
    }
    
    @Inject(method = "charTyped", at = @At("RETURN"))
    private void updateSuggestions(CharacterEvent input, CallbackInfoReturnable<Boolean> cir) {
        ((ScreenAccess) this).refreshSuggestions();
    }
    
    @WrapOperation(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/TextFieldHelper;charTyped(Lnet/minecraft/client/input/CharacterEvent;)Z"))
    private boolean processFont(TextFieldHelper instance, CharacterEvent input, Operation<Boolean> original) {
        FontProcessor fontProcessor = SymbolChat.fontManager.getCurrentScreenFontProcessor();
        String string = fontProcessor.convertString(Character.toString(input.codepoint()));
        instance.insertText(string);
        if(fontProcessor.isReverseDirection()) {
            int pos = instance.getCursorPos()-string.length();
            instance.setSelectionRange(pos, pos);
        }
        return true;
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private boolean doNotRenderSuperBefore(Screen instance, GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        return false;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderSuperAfter(GuiGraphics context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        super.render(context, mouseX, mouseY, deltaTicks);
    }
    
    
    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void updateSuggestions(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        ((ScreenAccess) this).refreshSuggestions();
    }
    
    @Override
    public void insertSymbol(String symbol) {
        this.signField.insertText(symbol);
    }

    @Override
    public ScreenPosition getCursorPosition() {
        Vector3f textScale = this.getSignTextScale();
        String string = this.getText();
        int cursor = this.getTextFieldHelper().getCursorPos();
        if (string == null || cursor < 0 || minecraft == null) return new ScreenPosition(0,0);
        int halfY = 4 * this.sign.getTextLineHeight() / 2;
        int y = this.line * this.sign.getTextLineHeight() - halfY;
        int cx = this.minecraft.font.width(string.substring(0, Math.min(cursor, string.length())));
        int x = cx - this.minecraft.font.width(string) / 2;
        x += this.width/2; // see translateForRender()
        y += 90;
        if(((Object) this) instanceof HangingSignEditScreen) y += 35;
        return new ScreenPosition((int) (x * textScale.x), (int) (y * textScale.y));
    }

    @Override
    public String getText() {
        return this.messages[line];
    }

    @Override
    public TextFieldHelper getTextFieldHelper() {
        return this.signField;
    }
}
