package symbolchat.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolchat.gui.widget.symbolButton.OpenSymbolPanelButtonWidget;
import symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import symbolchat.SymbolInsertable;
import symbolchat.gui.SymbolSelectionPanel;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin extends Screen implements SymbolInsertable {
    @Shadow private TextFieldWidget nameField;

    private SymbolSelectionPanel symbolSelectionPanel;

    protected AnvilScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "setup", at = @At(value = "RETURN"))
    private void addSymbolChatComponents(CallbackInfo ci) {
        int symbolButtonX = this.width-2-SymbolButtonWidget.symbolSize;
        int symbolButtonY = this.height-2-SymbolButtonWidget.symbolSize;
        this.symbolSelectionPanel = new SymbolSelectionPanel(this,this.width-SymbolSelectionPanel.width-2,symbolButtonY-2-SymbolSelectionPanel.height);
        SymbolButtonWidget symbolButtonWidget = new OpenSymbolPanelButtonWidget(this, symbolButtonX, symbolButtonY, this.symbolSelectionPanel);
        this.addDrawableChild(symbolSelectionPanel);
        this.addDrawableChild(symbolButtonWidget);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(symbolSelectionPanel.mouseScrolled(mouseX,mouseY,amount)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void insertSymbol(String symbol) {
        if(this.nameField.isActive())
            this.nameField.write(symbol);
    }
}
