package net.replaceitem.symbolchat.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.ScreenAccess;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.resource.FontProcessor;
import org.joml.Vector2i;
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
    @Shadow private SelectionManager selectionManager;
    @Shadow private int currentRow;
    @Shadow @Final private String[] messages;
    @Shadow @Final protected SignBlockEntity blockEntity;

    @Shadow protected abstract Vector3f getTextScale();

    protected AbstractSignEditScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "HEAD"))
    private void onInit(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handleKeyPressed(keyCode, scanCode, modifiers)) cir.setReturnValue(true);
    }
    
    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    public void charTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handlePanelCharTyped(chr, modifiers)) cir.setReturnValue(true);
    }
    
    @Inject(method = "charTyped", at = @At("RETURN"))
    private void updateSuggestions(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        ((ScreenAccess) this).refreshSuggestions();
    }
    
    @WrapOperation(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SelectionManager;insert(C)Z"))
    private boolean processFont(SelectionManager instance, char c, Operation<Boolean> original) {
        FontProcessor fontProcessor = SymbolChat.fontManager.getCurrentScreenFontProcessor();
        String string = fontProcessor.convertString(Character.toString(c));
        instance.insert(string);
        if(fontProcessor.isReverseDirection()) {
            int pos = instance.getSelectionStart()-string.length();
            instance.setSelection(pos, pos);
        }
        return true;
    }
    
    
    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void updateSuggestions(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        ((ScreenAccess) this).refreshSuggestions();
    }
    
    @Override
    public void insertSymbol(String symbol) {
        this.selectionManager.insert(symbol);
    }

    @Override
    public Vector2i getCursorPosition() {
        Vector3f textScale = this.getTextScale();
        String string = this.getText();
        int cursor = this.getSelectionManager().getSelectionStart();
        if (string == null || cursor < 0 || client == null) return new Vector2i(0,0);
        int halfY = 4 * this.blockEntity.getTextLineHeight() / 2;
        int y = this.currentRow * this.blockEntity.getTextLineHeight() - halfY;
        int cx = this.client.textRenderer.getWidth(string.substring(0, Math.min(cursor, string.length())));
        int x = cx - this.client.textRenderer.getWidth(string) / 2;
        x += this.width/2; // see translateForRender()
        y += 90;
        if(((Object) this) instanceof HangingSignEditScreen) y += 35;
        return new Vector2i((int) (x * textScale.x), (int) (y * textScale.y));
    }

    @Override
    public String getText() {
        return this.messages[currentRow];
    }

    @Override
    public SelectionManager getSelectionManager() {
        return this.selectionManager;
    }
}
