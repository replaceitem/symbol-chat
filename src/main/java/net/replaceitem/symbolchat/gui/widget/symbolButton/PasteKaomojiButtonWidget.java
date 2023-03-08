package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.gui.SymbolTab;

public class PasteKaomojiButtonWidget extends PasteSymbolButtonWidget {

    public PasteKaomojiButtonWidget(int x, int y, SymbolInsertable symbolInsertable, String symbol) {
        super(x, y, symbolInsertable, symbol);
        this.width = SymbolTab.width - 2;
    }
}
