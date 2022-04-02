package symbolchat.mixin;


import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolchat.SymbolInsertable;
import symbolchat.gui.SymbolSelectionPanel;
import symbolchat.gui.widget.symbolButton.OpenSymbolPanelButtonWidget;
import symbolchat.gui.widget.symbolButton.SymbolButtonWidget;

@Mixin(SignEditScreen.class)
public class SignEditScreenMixin extends Screen implements SymbolInsertable {
    @Shadow private SelectionManager selectionManager;
    private SymbolButtonWidget symbolButtonWidget;
    private SymbolSelectionPanel symbolSelectionPanel;

    protected SignEditScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void addSymbolButton(CallbackInfo ci) {
        int symbolButtonX = this.width-SymbolButtonWidget.symbolSize;
        int symbolButtonY = this.height-2-SymbolButtonWidget.symbolSize;
        this.symbolSelectionPanel = new SymbolSelectionPanel(this,this.width-SymbolSelectionPanel.width-2,symbolButtonY-2-SymbolSelectionPanel.height);
        this.symbolButtonWidget = new OpenSymbolPanelButtonWidget(this, symbolButtonX, symbolButtonY, this.symbolSelectionPanel);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(symbolSelectionPanel.mouseClicked(mouseX,mouseY,button)) return true;
        if(symbolButtonWidget.mouseClicked(mouseX,mouseY,button)) return true;
        return super.mouseClicked(mouseX,mouseY,button);
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void renderSymbolButton(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        symbolButtonWidget.render(matrices,mouseX,mouseY,delta);
        symbolSelectionPanel.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void insertSymbol(String symbol) {
        this.selectionManager.insert(symbol);
    }
}
