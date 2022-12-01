package net.replaceitem.symbolchat.gui;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.replaceitem.symbolchat.SymbolList;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteKaomojiButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;

public class KaomojiTab extends SymbolTab implements Drawable, Element {


    public KaomojiTab(Screen screen, SymbolList symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y) {
        super(screen, symbols, symbolSelectionPanel, x, y);
    }

    @Override
    protected PasteSymbolButtonWidget createButton(int x, int y, String symbol) {
        return new PasteKaomojiButtonWidget(screen, x, y, this, symbol);
    }

    @Override
    protected int getColumns() {
        return 1;
    }
}
