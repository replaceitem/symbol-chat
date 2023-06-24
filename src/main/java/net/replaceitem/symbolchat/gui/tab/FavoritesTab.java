package net.replaceitem.symbolchat.gui.tab;

import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;

import java.util.function.Consumer;

public class FavoritesTab extends SymbolTab {


    public static final Text NO_FAVORITE_SYMBOLS = Text.translatable("symbolchat.no_favorite_symbols");
    public static final Text NO_CLOTHCONFIG = Text.translatable("symbolchat.no_clothconfig");


    protected boolean isEmpty = false;
    
    public FavoritesTab(Consumer<String> symbolConsumer, SymbolCategory symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y, int height) {
        super(symbolConsumer, symbols, symbolSelectionPanel, x, y, height);
    }

    @Override
    protected void addSymbols() {
        super.addSymbols();
        this.isEmpty = this.category.getSymbols().isEmpty();
    }

    @Override
    protected PasteSymbolButtonWidget createButton(String symbol) {
        PasteSymbolButtonWidget button = new PasteSymbolButtonWidget(x, y, this.symbolConsumer, symbol) {
            @Override
            protected void onRightClick() {
                SymbolChat.config.removeFavorite(this.getSymbol());
                refresh();
            }
        };
        button.setFavorite(false);
        return button;
    }

    @Override
    public Text getNoSymbolsText() {
        if(!isEmpty) return null;
        return SymbolChat.clothConfigEnabled ? NO_FAVORITE_SYMBOLS : NO_CLOTHCONFIG;
    }
}
