package net.replaceitem.symbolchat.gui;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.gui.widget.ScrollableGridWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;

import java.util.function.Consumer;

public class KaomojiTab extends SymbolTab implements Drawable, Element {


    public KaomojiTab(Consumer<String> symbolConsumer, SymbolCategory symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y, int height) {
        super(symbolConsumer, symbols, symbolSelectionPanel, x, y, height);
    }

    @Override
    protected PasteSymbolButtonWidget createButton(String symbol) {
        PasteSymbolButtonWidget pasteSymbolButtonWidget = new PasteSymbolButtonWidget(x, y, this.symbolConsumer, symbol);
        pasteSymbolButtonWidget.setWidth(this.getWidth() - 2);
        pasteSymbolButtonWidget.setTooltip(null);
        return pasteSymbolButtonWidget;
    }

    @Override
    protected ScrollableGridWidget createScrollableGridWidget() {
        return new ScrollableGridWidget(this.x, this.y, this.getWidth(), this.getHeight(), 1);
    }
}
