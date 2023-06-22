package net.replaceitem.symbolchat.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.SymbolStorage;
import net.replaceitem.symbolchat.gui.widget.ScrollableGridWidget;
import net.replaceitem.symbolchat.gui.widget.SymbolSearchBar;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class SearchTab extends SymbolTab {
    
    public static final Text NO_RESULTS = Text.translatable("symbolchat.no_search_results");
    private final SymbolSearchBar searchBar;

    private static final int SEARCH_BAR_HEIGHT = 10;

    public SearchTab(Consumer<String> symbolConsumer, SymbolCategory symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y, int height) {
        super(symbolConsumer, symbols, symbolSelectionPanel, x, y, height);

        this.searchBar = new SymbolSearchBar(this.x + 2, this.y + 1, getWidth() - 4, SEARCH_BAR_HEIGHT);
        this.searchBar.setChangedListener(s -> refresh(SymbolStorage.all));
        this.children.add(this.searchBar);
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        if(this.searchBar.isFocused()) return;
        super.setFocused(focused);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
        this.searchBar.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    protected void addSymbols(SymbolCategory symbolCategory) {
        String search = searchBar == null ? "" : searchBar.getText();
        List<String> searchResults = SymbolStorage.performSearch(symbolCategory, search).toList();
        searchResults.stream().map(s -> new PasteSymbolButtonWidget(0, 0, this.symbolConsumer, s)).forEachOrdered(scrollableGridWidget::add);
        this.isEmpty = searchResults.isEmpty();
    }

    @Override
    protected ScrollableGridWidget createScrollableGridWidget() {
        int offset = SEARCH_BAR_HEIGHT + 2;
        return new ScrollableGridWidget(x, y + offset, this.getWidth(), this.getHeight()-offset, COLUMNS);
    }

    @Override
    public Text getNoSymbolsText() {
        return NO_RESULTS;
    }
}
