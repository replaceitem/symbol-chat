package net.replaceitem.symbolchat.mixin;


import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.widget.SymbolSuggestor;
import net.replaceitem.symbolchat.gui.widget.symbolButton.OpenSymbolPanelButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(AbstractSignEditScreen.class)
public abstract class SignEditScreenMixin extends Screen implements Consumer<String>, SymbolSuggestable.SelectionManagerSymbolSuggestable {
    @Shadow private SelectionManager selectionManager;
    @Shadow private int currentRow;
    @Shadow @Final private String[] messages;
    @Shadow @Final private SignBlockEntity blockEntity;

    @Shadow protected abstract Vector3f getTextScale();

    private SymbolSelectionPanel symbolSelectionPanel;
    private SymbolSuggestor symbolSuggestor;

    protected SignEditScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "HEAD"))
    private void addSymbolButton(CallbackInfo ci) {
        int symbolButtonX = this.width - SymbolButtonWidget.SYMBOL_SIZE;
        int symbolButtonY = this.height - 2 - SymbolButtonWidget.SYMBOL_SIZE;
        this.symbolSelectionPanel = new SymbolSelectionPanel(this,this.width-SymbolSelectionPanel.WIDTH -2,symbolButtonY-2-SymbolSelectionPanel.HEIGHT);
        SymbolButtonWidget symbolButtonWidget = new OpenSymbolPanelButtonWidget(symbolButtonX, symbolButtonY, this.symbolSelectionPanel);
        this.addDrawableChild(symbolSelectionPanel);
        this.addDrawableChild(symbolButtonWidget);

        this.symbolSuggestor = new SymbolSuggestor(this, this::replaceSuggestion, this);
        this.addDrawableChild(symbolSuggestor);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(symbolSelectionPanel != null && symbolSelectionPanel.mouseScrolled(mouseX,mouseY,amount)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(symbolSuggestor != null && this.symbolSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
        }
        if(symbolSelectionPanel != null && this.symbolSelectionPanel.keyPressed(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    public void charTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(symbolSelectionPanel != null && this.symbolSelectionPanel.charTyped(chr, modifiers)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "charTyped", at = @At("RETURN"))
    private void updateSuggestions(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(symbolSuggestor != null) this.symbolSuggestor.refresh();
    }
    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void updateSuggestions(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(symbolSuggestor != null) this.symbolSuggestor.refresh();
    }
    

    @Override
    public void accept(String s) {
        this.selectionManager.insert(s);
    }

    @Override
    public Vector2i getCursorPosition() {
        Vector3f textScale = this.getTextScale();
        String string = this.getText();
        int cursor = this.getSelectionManager().getSelectionStart();
        if (string == null || cursor < 0) return new Vector2i(0,0);
        int halfY = 4 * this.blockEntity.getTextLineHeight() / 2;
        int y = this.currentRow * this.blockEntity.getTextLineHeight() - halfY;
        int cx = this.client.textRenderer.getWidth(string.substring(0, Math.max(Math.min(cursor, string.length()), 0)));
        int x = cx - this.client.textRenderer.getWidth(string) / 2;
        x += this.width/2; // see translateForRender()
        y += 90;
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
