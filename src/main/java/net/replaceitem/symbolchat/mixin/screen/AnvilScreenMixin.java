package net.replaceitem.symbolchat.mixin.screen;

import net.replaceitem.symbolchat.extensions.ScreenAccess;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
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
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> implements SymbolInsertable, SymbolSuggestable.TextFieldWidgetSymbolSuggestable {
    @Shadow private EditBox name;

    public AnvilScreenMixin(AnvilMenu handler, Inventory playerInventory, Component title, ResourceLocation texture) {
        super(handler, playerInventory, title, texture);
    }

    @Inject(method = "subInit", at = @At("RETURN"))
    private void onSubInit(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
        ((SymbolEditableWidget) this.name).setFontProcessorSupplier(() -> ((ScreenAccess) this).getFontProcessor());
        ((SymbolEditableWidget) this.name).setRefreshSuggestions(() -> ((ScreenAccess) this).refreshSuggestions());
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).handleKeyPressed(input)) cir.setReturnValue(true);
    }

    @Override
    public void insertSymbol(String symbol) {
        if(name == null) return;
        if(this.name.canConsumeInput()) {
            this.name.insertText(symbol);
            if(minecraft != null) this.minecraft.schedule(() -> {
                if(minecraft.screen == this) this.setFocused(this.name);
            });
        }
    }

    @Override
    public void focusTextbox() {
        if(minecraft != null) this.minecraft.schedule(() -> {
            if(minecraft.screen == this) this.setFocused(this.name);
        });
    }

    @Override
    public EditBox getTextField() {
        return this.name;
    }
}
