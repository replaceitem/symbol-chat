package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;

public class OpenSymbolPanelButtonWidget extends SymbolButtonWidget {

    protected SymbolSelectionPanel symbolSelectionPanel;

    public OpenSymbolPanelButtonWidget(int x, int y, SymbolSelectionPanel symbolSelectionPanel) {
        super(x, y, "☺");
        this.symbolSelectionPanel = symbolSelectionPanel;
    }

    @Override
    public boolean onClick(int button) {
        symbolSelectionPanel.toggleVisible();
        return true;
    }

    @Override
    public boolean isSelected() {
        return super.isSelected() || symbolSelectionPanel.isVisible();
    }
}
