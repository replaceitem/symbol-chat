package net.replaceitem.symbolchat.mixin.screen;

import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.replaceitem.symbolchat.*;
import net.replaceitem.symbolchat.extensions.MultilineEditBoxExtension;
import net.replaceitem.symbolchat.extensions.ScreenAccess;
import net.replaceitem.symbolchat.extensions.SymbolEditableWidget;
import net.replaceitem.symbolchat.mixin.widget.MultiLinedEditBoxAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin extends Screen implements SymbolInsertable, SymbolSuggestable.EditBoxSymbolSuggestable {

    @Shadow private MultiLineEditBox page;

    protected BookEditScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "HEAD"))
    private void onInit(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void afterInit(CallbackInfo ci) {
        ((SymbolEditableWidget) this.page).setFontProcessorSupplier(() -> ((ScreenAccess) this).getFontProcessor());
        ((SymbolEditableWidget) this.page).setRefreshSuggestions(() -> ((ScreenAccess) this).refreshSuggestions());
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handleKeyPressed(input)) cir.setReturnValue(true);
    }

    @Override
    public void insertSymbol(String symbol) {
        ((MultilineEditBoxExtension) this.page).insert(symbol);
    }

    @Override
    public void focusTextbox() {
        if(minecraft != null) this.minecraft.schedule(() -> {
            if(minecraft.screen == this) this.setFocused(this.page);
        });
    }

    @Override
    public ScreenPosition getCursorPosition() {
        return ((MultilineEditBoxExtension) this.page).getCursorPosition();
    }

    @Override
    public MultilineTextField getMultilineTextField() {
        return ((MultiLinedEditBoxAccessor) this.page).getMultilineTextField();
    }
}