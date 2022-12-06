package net.replaceitem.symbolchat.mixin;


import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.widget.symbolButton.OpenSymbolPanelButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.replaceitem.symbolchat.SymbolInsertable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignEditScreen.class)
public class SignEditScreenMixin extends Screen implements SymbolInsertable {
    @Shadow private SelectionManager selectionManager;
    private SymbolSelectionPanel symbolSelectionPanel;

    protected SignEditScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "HEAD"))
    private void addSymbolButton(CallbackInfo ci) {
        int symbolButtonX = this.width- SymbolButtonWidget.symbolSize;
        int symbolButtonY = this.height-2-SymbolButtonWidget.symbolSize;
        this.symbolSelectionPanel = new SymbolSelectionPanel(this,this.width-SymbolSelectionPanel.WIDTH -2,symbolButtonY-2-SymbolSelectionPanel.HEIGHT);
        SymbolButtonWidget symbolButtonWidget = new OpenSymbolPanelButtonWidget(this, symbolButtonX, symbolButtonY, this.symbolSelectionPanel);
        this.addDrawableChild(symbolSelectionPanel);
        this.addDrawableChild(symbolButtonWidget);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(symbolSelectionPanel.mouseScrolled(mouseX,mouseY,amount)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(this.symbolSelectionPanel.keyPressed(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    public void charTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(this.symbolSelectionPanel.charTyped(chr, modifiers)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Override
    public void insertSymbol(String symbol) {
        this.selectionManager.insert(symbol);
    }
}
