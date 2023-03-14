package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;

public class OpenSymbolPanelButtonWidget extends SymbolButtonWidget {

    protected SymbolSelectionPanel symbolSelectionPanel;

    public OpenSymbolPanelButtonWidget(int x, int y, SymbolSelectionPanel symbolSelectionPanel) {
        super(x, y, "☺");
        this.symbolSelectionPanel = symbolSelectionPanel;
    }

    public OpenSymbolPanelButtonWidget(int x, int y, int w, int h, SymbolSelectionPanel symbolSelectionPanel) {
        super(x, y, w, h,"☺");
        this.symbolSelectionPanel = symbolSelectionPanel;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        symbolSelectionPanel.visible = !symbolSelectionPanel.visible;
    }

    @Override
    public boolean isSelected() {
        return symbolSelectionPanel.visible;
    }
    
    @Override
    public boolean isHovered() {
        return super.isHovered() || this.isSelected();
    }
}
