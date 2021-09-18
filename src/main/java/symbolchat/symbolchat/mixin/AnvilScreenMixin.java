package symbolchat.symbolchat.mixin;

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
import symbolchat.symbolchat.FontProcessor;
import symbolchat.symbolchat.FontProcessorAccessor;
import symbolchat.symbolchat.SymbolChat;
import symbolchat.symbolchat.widget.DropDownWidget;
import symbolchat.symbolchat.widget.FontSelectionDropDownWidget;
import symbolchat.symbolchat.widget.symbolButton.OpenSymbolPanelButtonWidget;
import symbolchat.symbolchat.widget.symbolButton.SymbolButtonWidget;
import symbolchat.symbolchat.SymbolInsertable;
import symbolchat.symbolchat.SymbolSelectionPanel;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin extends Screen implements SymbolInsertable {
    @Shadow private TextFieldWidget nameField;

    private SymbolButtonWidget symbolButtonWidget;
    private SymbolSelectionPanel symbolSelectionPanel;

    protected AnvilScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "setup", at = @At(value = "RETURN"))
    private void addSymbolChatComponents(CallbackInfo ci) {
        int symbolButtonX = this.width-2-SymbolButtonWidget.symbolSize;
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

    @Inject(method = "renderForeground", at = @At(value = "RETURN"), cancellable = true)
    private void renderSymbolButton(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        symbolButtonWidget.render(matrices,mouseX,mouseY,delta);
        symbolSelectionPanel.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void insertSymbol(String symbol) {
        if(this.nameField.isActive())
            this.nameField.write(symbol);
    }
}
