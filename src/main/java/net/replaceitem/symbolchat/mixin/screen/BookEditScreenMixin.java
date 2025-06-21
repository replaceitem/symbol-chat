package net.replaceitem.symbolchat.mixin.screen;

import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.*;
import net.replaceitem.symbolchat.extensions.EditBoxWidgetExtension;
import net.replaceitem.symbolchat.extensions.ScreenAccess;
import net.replaceitem.symbolchat.extensions.SymbolEditableWidget;
import net.replaceitem.symbolchat.mixin.widget.EditBoxWidgetAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin extends Screen implements SymbolInsertable, SymbolSuggestable.EditBoxSymbolSuggestable {

    @Shadow private EditBoxWidget editBox;

    protected BookEditScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "HEAD"))
    private void onInit(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void afterInit(CallbackInfo ci) {
        ((SymbolEditableWidget) this.editBox).setFontProcessorSupplier(() -> ((ScreenAccess) this).getFontProcessor());
        ((SymbolEditableWidget) this.editBox).setRefreshSuggestions(() -> ((ScreenAccess) this).refreshSuggestions());
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handleKeyPressed(keyCode, scanCode, modifiers)) cir.setReturnValue(true);
    }

    @Override
    public void insertSymbol(String symbol) {
        ((EditBoxWidgetExtension) this.editBox).insert(symbol);
    }

    @Override
    public ScreenPos getCursorPosition() {
        return ((EditBoxWidgetExtension) this.editBox).getCursorPosition();
    }

    @Override
    public EditBox getEditBox() {
        return ((EditBoxWidgetAccessor) this.editBox).getEditBox();
    }
}