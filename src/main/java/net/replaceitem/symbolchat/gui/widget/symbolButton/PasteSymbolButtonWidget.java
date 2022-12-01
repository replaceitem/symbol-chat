package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.screen.Screen;
import net.replaceitem.symbolchat.gui.SymbolTab;

public class PasteSymbolButtonWidget extends SymbolButtonWidget {

    protected SymbolTab symbolTab;

    public String getSymbol() {
        return this.symbol;
    }

    protected String symbol;

    public PasteSymbolButtonWidget(Screen screen, int x, int y, SymbolTab symbolTab, String symbol) {
        super(screen, x, y, symbol);
        this.symbolTab = symbolTab;
        this.symbol = symbol;
    }

    public void placeInTabGrid(int gx, int gy) {
        this.x = symbolTab.getX()+1+(gx * (SymbolButtonWidget.symbolSize+1));
        this.y = symbolTab.getY()+1+(gy * (SymbolButtonWidget.symbolSize+1));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.symbolTab.pasteSymbol(this.symbol);
    }
}
