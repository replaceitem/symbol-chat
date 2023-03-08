package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.widget.SymbolSuggestor;
import net.replaceitem.symbolchat.gui.widget.symbolButton.OpenSymbolPanelButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> implements SymbolInsertable, SymbolSuggestable.TextFieldWidgetSymbolSuggestable {
    @Shadow private TextFieldWidget nameField;

    private static final int ANVIL_SYMBOL_BUTTON_SIZE = SymbolButtonWidget.SYMBOL_SIZE + 2;

    private SymbolSelectionPanel symbolSelectionPanel;
    private SymbolButtonWidget symbolButtonWidget;
    private SymbolSuggestor symbolSuggestor;

    public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }


    @ModifyConstant(method = "setup", constant = @Constant(intValue = 103))
    private int adjustWidth(int original) {
        return original-ANVIL_SYMBOL_BUTTON_SIZE-2;
    }

    @Inject(method = "setup", at = @At("RETURN"))
    private void addSymbolChatComponents(CallbackInfo ci) {
        int symbolButtonX = this.nameField.getX() + this.nameField.getWidth() + 2 + 3;
        int symbolButtonY = this.nameField.getY() - 3;
        this.symbolSelectionPanel = new SymbolSelectionPanel(this,this.width-SymbolSelectionPanel.WIDTH -2,this.height-2-SymbolSelectionPanel.HEIGHT);
        this.symbolButtonWidget = new OpenSymbolPanelButtonWidget(symbolButtonX, symbolButtonY, ANVIL_SYMBOL_BUTTON_SIZE, ANVIL_SYMBOL_BUTTON_SIZE, this.symbolSelectionPanel);

        this.symbolSuggestor = new SymbolSuggestor(this, this::replaceSuggestion, this);
        this.addDrawableChild(symbolSuggestor);
    }

    @Inject(method = "renderForeground", at = @At("RETURN"))
    protected void renderForeground(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        symbolSelectionPanel.render(matrices, mouseX, mouseY, delta);
        symbolButtonWidget.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(symbolSelectionPanel.mouseScrolled(mouseX,mouseY,amount)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(symbolSelectionPanel.mouseClicked(mouseX,mouseY,button)) return true;
        if(symbolButtonWidget.mouseClicked(mouseX,mouseY,button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(this.symbolSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
        }
        if(this.symbolSelectionPanel.keyPressed(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(this.symbolSelectionPanel.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Inject(method = "onRenamed", at = @At("HEAD"))
    private void updateSuggestions(String chatText, CallbackInfo ci) {
        if(this.symbolSuggestor != null) this.symbolSuggestor.refresh();
    }

    @Override
    public void insertSymbol(String symbol) {
        if(this.nameField.isActive())
            this.nameField.write(symbol);
    }

    @Override
    public TextFieldWidget getTextField() {
        return this.nameField;
    }
}
