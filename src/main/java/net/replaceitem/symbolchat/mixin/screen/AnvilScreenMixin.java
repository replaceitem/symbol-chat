package net.replaceitem.symbolchat.mixin.screen;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.replaceitem.symbolchat.extensions.ScreenAccess;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.extensions.SymbolEditableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> implements SymbolInsertable, SymbolSuggestable.TextFieldWidgetSymbolSuggestable {
    @Shadow private TextFieldWidget nameField;

    public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }

    @Inject(method = "setup", at = @At("RETURN"))
    private void onSetup(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
        ((SymbolEditableWidget) this.nameField).setFontProcessorSupplier(() -> ((ScreenAccess) this).getFontProcessor());
        ((SymbolEditableWidget) this.nameField).setRefreshSuggestions(() -> ((ScreenAccess) this).refreshSuggestions());
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handleKeyPressed(keyCode, scanCode, modifiers)) cir.setReturnValue(true);
    }
    
    @Override
    public void insertSymbol(String symbol) {
        if(nameField == null) return;
        if(this.nameField.isActive()) {
            this.nameField.write(symbol);
            if(client != null) this.client.send(() -> {
                if(client.currentScreen == this) this.setFocused(this.nameField);
            });
        }
    }

    @Override
    public TextFieldWidget getTextField() {
        return this.nameField;
    }
}
