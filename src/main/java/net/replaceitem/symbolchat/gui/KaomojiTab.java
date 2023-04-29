package net.replaceitem.symbolchat.gui;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;

import java.util.function.Consumer;

public class KaomojiTab extends SymbolTab implements Drawable, Element {


    public KaomojiTab(Consumer<String> symbolConsumer, SymbolCategory symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y) {
        super(symbolConsumer, symbols, symbolSelectionPanel, x, y);
    }

    @Override
    protected PasteSymbolButtonWidget createButton(int x, int y, String symbol) {
        PasteSymbolButtonWidget pasteSymbolButtonWidget = new PasteSymbolButtonWidget(x, y, this.symbolConsumer, symbol);
        pasteSymbolButtonWidget.setWidth(SymbolTab.width - 2);
        pasteSymbolButtonWidget.setTooltip(null);
        return pasteSymbolButtonWidget;
    }

    @Override
    protected int getColumns() {
        return 1;
    }
}
