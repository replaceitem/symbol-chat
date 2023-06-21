package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.DrawContext;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;

public class OpenSymbolPanelButtonWidget extends SymbolButtonWidget {

    protected SymbolSelectionPanel symbolSelectionPanel;

    public OpenSymbolPanelButtonWidget(int x, int y, SymbolSelectionPanel symbolSelectionPanel) {
        super(x, y, "☺");
        this.symbolSelectionPanel = symbolSelectionPanel;
    }

    @Override
    public boolean onClick(int button) {
        symbolSelectionPanel.visible = !symbolSelectionPanel.visible;
        return true;
    }

    @Override
    public boolean isSelected() {
        return super.isSelected() || symbolSelectionPanel.visible;
    }

    @Override
    protected void drawOutline(DrawContext drawContext) {
        drawContext.drawHorizontalLine(this.getX()-1, this.getX()+width, this.getY()-1, 0xFFFFFFFF);
        drawContext.drawVerticalLine(this.getX()-1, this.getY()-1, this.getY()+height, 0xFFFFFFFF);
        drawContext.drawHorizontalLine(this.getX()-1, this.getX()+width, this.getY()+height, 0xFFFFFFFF);
        drawContext.drawVerticalLine(this.getX()+width, this.getY()-1, this.getY()+height, 0xFFFFFFFF);
    }
}
