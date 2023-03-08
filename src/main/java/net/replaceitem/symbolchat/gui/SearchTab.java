package net.replaceitem.symbolchat.gui;

import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolStorage;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;

public class SearchTab extends SymbolTab {
    public SearchTab(SymbolInsertable symbolInsertable, SymbolCategory symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y) {
        super(symbolInsertable, symbols, symbolSelectionPanel, x, y);
    }

    @Override
    protected void arrangeButtons() {
        this.symbolButtons = SymbolStorage.performSearch(symbolSelectionPanel.getSearchTerm()).map(s -> new PasteSymbolButtonWidget(0, 0, this.symbolInsertable, s)).toList();
        super.arrangeButtons();
    }
}
