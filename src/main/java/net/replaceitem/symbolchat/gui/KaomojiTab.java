package net.replaceitem.symbolchat.gui;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteKaomojiButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;

public class KaomojiTab extends SymbolTab implements Drawable, Element {


    public KaomojiTab(SymbolInsertable symbolInsertable, SymbolCategory symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y) {
        super(symbolInsertable, symbols, symbolSelectionPanel, x, y);
    }

    @Override
    protected PasteSymbolButtonWidget createButton(int x, int y, String symbol) {
        return new PasteKaomojiButtonWidget(x, y, this.symbolInsertable, symbol);
    }

    @Override
    protected int getColumns() {
        return 1;
    }
}
