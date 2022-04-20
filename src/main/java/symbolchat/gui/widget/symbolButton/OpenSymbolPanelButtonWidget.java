package symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.screen.Screen;
import symbolchat.gui.SymbolSelectionPanel;

public class OpenSymbolPanelButtonWidget extends SymbolButtonWidget {

    protected SymbolSelectionPanel symbolSelectionPanel;

    public OpenSymbolPanelButtonWidget(Screen screen, int x, int y, SymbolSelectionPanel symbolSelectionPanel) {
        super(screen, x, y, "\u263a");
        this.symbolSelectionPanel = symbolSelectionPanel;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        symbolSelectionPanel.visible = !symbolSelectionPanel.visible;
    }

    @Override
    protected boolean isSelected() {
        return symbolSelectionPanel.visible;
    }
    
    @Override
    public boolean isHovered() {
        return super.isHovered() || this.isSelected();
    }
}
