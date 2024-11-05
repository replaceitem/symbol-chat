package net.replaceitem.symbolchat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.ScreenAccess;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.resource.FontProcessor;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin extends Screen implements SymbolInsertable, SymbolSuggestable.SelectionManagerSymbolSuggestable {
    @Shadow @Final private SelectionManager currentPageSelectionManager;
    @Shadow protected abstract String getCurrentPageContent();
    @Shadow protected abstract BookEditScreen.PageContent getPageContent();
    @Shadow protected abstract BookEditScreen.Position absolutePositionToScreenPosition(BookEditScreen.Position position);

    protected BookEditScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "HEAD"))
    private void onInit(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private void resetRenderColor(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // reset render color to prevent symbol chat gui from rendering glitchy due to cursor blinking shader color not being reset
        RenderSystem.setShaderColor(1,1,1,1);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handleKeyPressed(keyCode, scanCode, modifiers)) cir.setReturnValue(true);
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    public void charTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handlePanelCharTyped(chr, modifiers)) cir.setReturnValue(true);
    }
    
    @WrapOperation(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SelectionManager;insert(Ljava/lang/String;)V"))
    private void processFont(SelectionManager instance, String string, Operation<Void> original) {
        FontProcessor fontProcessor = SymbolChat.fontManager.getCurrentScreenFontProcessor();
        string = fontProcessor.convertString(string);
        original.call(instance, string);
        if(fontProcessor.isReverseDirection()) {
            int pos = instance.getSelectionStart()-string.length();
            instance.setSelection(pos, pos);
        }
    }
    
    @Inject(method = "charTyped", at = @At("RETURN"))
    private void updateSuggestions(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        ((ScreenAccess) this).refreshSuggestions();
    }
    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void updateSuggestions(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        ((ScreenAccess) this).refreshSuggestions();
    }
    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void updateSuggestions(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        ((ScreenAccess) this).refreshSuggestions();
    }

    @Override
    public void insertSymbol(String symbol) {
        this.currentPageSelectionManager.insert(symbol);
    }

    @Override
    public Vector2i getCursorPosition() {
        BookEditScreen.Position position = ((PageContentAccessor) this.getPageContent()).getPosition();
        position = this.absolutePositionToScreenPosition(position);
        return new Vector2i(position.x, position.y);
    }

    @Override
    public String getText() {
        return getCurrentPageContent();
    }

    @Override
    public SelectionManager getSelectionManager() {
        return this.currentPageSelectionManager;
    }
}