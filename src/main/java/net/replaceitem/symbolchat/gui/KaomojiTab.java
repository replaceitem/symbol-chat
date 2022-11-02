package net.replaceitem.symbolchat.gui;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.replaceitem.symbolchat.SymbolList;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteKaomojiButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;

public class KaomojiTab extends SymbolTab implements Drawable, Element {


    public KaomojiTab(Screen screen, SymbolList symbols, SymbolSelectionPanel symbolSelectionPanel) {
        super(screen, symbols, symbolSelectionPanel);
    }

    @Override
    public void loadSymbols() {
        symbolButtons.clear();
        for(int i = 0; i < this.symbols.items.size(); i++) {
            int widgetX = this.x+1;
            int widgetY = this.y+1+(i / getColumns() *(SymbolButtonWidget.symbolSize+1));
            SymbolButtonWidget widget = new PasteKaomojiButtonWidget(screen, widgetX, widgetY, this, symbols.items.get(i));
            symbolButtons.add(widget);
        }
    }

    @Override
    protected int getColumns() {
        return 1;
    }
}
