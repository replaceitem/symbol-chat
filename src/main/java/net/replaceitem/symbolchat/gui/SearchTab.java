package net.replaceitem.symbolchat.gui;

import net.minecraft.client.gui.screen.Screen;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchTab extends SymbolTab {


    protected List<PasteSymbolButtonWidget> visibleSymbolButtons;

    public SearchTab(Screen screen, SymbolCategory symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y) {
        super(screen, symbols, symbolSelectionPanel, x, y);
    }

    @Override
    protected void init(SymbolCategory symbols) {
        visibleSymbolButtons = new ArrayList<>();
        super.init(symbols);
    }

    @Override
    protected void rearrangeSymbols() {
        visibleSymbolButtons.clear();
        visibleSymbolButtons.addAll(symbolButtons.stream()
                .map(pasteSymbolButtonWidget -> new Pair<>(
                        pasteSymbolButtonWidget,
                        symbolSelectionPanel.getSearchOrder(pasteSymbolButtonWidget)
                ))
                .filter(pasteSymbolButtonWidgetIntegerPair -> pasteSymbolButtonWidgetIntegerPair.getB() >= 0)
                .sorted(Comparator.comparingInt(Pair::getB))
                .map(Pair::getA)
                .toList()
        );
        super.rearrangeSymbols();
    }

    @Override
    protected List<? extends PasteSymbolButtonWidget> buttons() {
        return visibleSymbolButtons;
    }
}
