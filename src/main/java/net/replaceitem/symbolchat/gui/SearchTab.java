package net.replaceitem.symbolchat.gui;

import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.SymbolStorage;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;

import java.util.function.Consumer;

public class SearchTab extends SymbolTab {
    
    public static final Text NO_RESULTS = Text.translatable("symbolchat.no_search_results");
    
    public SearchTab(Consumer<String> symbolConsumer, SymbolCategory symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y) {
        super(symbolConsumer, symbols, symbolSelectionPanel, x, y);
    }

    @Override
    protected void arrangeButtons() {
        this.symbolButtons = SymbolStorage.performSearch(symbolSelectionPanel.getSearchTerm()).map(s -> new PasteSymbolButtonWidget(0, 0, this.symbolConsumer, s)).toList();
        super.arrangeButtons();
    }

    @Override
    public Text getNoSymbolsText() {
        return NO_RESULTS;
    }
}
