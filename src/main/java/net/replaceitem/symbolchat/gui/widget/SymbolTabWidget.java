package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SearchUtil;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.container.NonScrollableContainerWidget;
import net.replaceitem.symbolchat.gui.container.ScrollableGridContainer;
import net.replaceitem.symbolchat.gui.container.SmoothScrollableContainerWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import net.replaceitem.symbolchat.resource.SymbolTab;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class SymbolTabWidget extends NonScrollableContainerWidget implements PasteSymbolButtonWidget.Context {
    
    private static final int SEARCH_BAR_HEIGHT = 10;
    public static final Text NO_RESULTS = Text.translatable("symbolchat.symbol_panel.no_search_results");
    public static final Text NO_FAVORITE_SYMBOLS = Text.translatable("symbolchat.symbol_panel.no_favorite_symbols");

    private final SymbolSelectionPanel symbolSelectionPanel;

    @Nullable
    private Text emptyText;
    protected final SymbolTab tab;

    @Nullable
    private SymbolSearchBar searchBar;
    protected final ScrollableGridContainer scrollableWidget;

    public SymbolTabWidget(int x, int y, int width, int height, SymbolTab symbolTab, SymbolSelectionPanel symbolSelectionPanel, int panelColumns) {
        super(x, y, width, height);
        this.tab = symbolTab;
        this.symbolSelectionPanel = symbolSelectionPanel;
        int columns = this.tab.getType().getColumns(panelColumns);
        int offset = this.tab.hasSearchBar() ? SEARCH_BAR_HEIGHT+2 : 0;
        this.scrollableWidget = new ScrollableGridContainer(getX()+1, getY() + offset, this.getWidth()-1, this.getHeight()-offset, columns);
        this.scrollableWidget.setSmoothScrolling(true);
        this.scrollableWidget.setScrollbarStyle(SmoothScrollableContainerWidget.ScrollbarStyle.SLIM);
        this.addChildren(scrollableWidget);
        if(tab.hasSearchBar()) {
            this.searchBar = new SymbolSearchBar(this.getX() + 2, this.getY() + 1, getWidth() - 4, SEARCH_BAR_HEIGHT);
            this.searchBar.setChangedListener(s -> refresh());
            this.addChildren(this.searchBar);
        }
        this.refresh();
    }

    @Override
    public void onSymbolClicked(String symbol) {
        this.symbolSelectionPanel.getSymbolInsertable().insertSymbol(symbol);
    }

    @Override
    public void refresh() {
        scrollableWidget.clearElements();
        addSymbols();
        this.scrollableWidget.refreshPositions();
    }

    protected void addSymbols() {
        Stream<String> stream = this.tab.streamSymbols();
        if(searchBar != null) {
            stream = SearchUtil.performSearch(stream, searchBar.getText());
        }
        List<PasteSymbolButtonWidget> buttons = stream.map(this::createButton).toList();
        buttons.forEach(scrollableWidget::add);
        this.emptyText = this.getEmptyText(buttons.isEmpty());
    }
    
    private Text getEmptyText(boolean noSymbols) {
        if(!noSymbols) return null;
        if(SymbolChat.symbolManager.isOnlyFavorites(tab)) {
            return NO_FAVORITE_SYMBOLS;
        }
        return NO_RESULTS;
    }

    protected PasteSymbolButtonWidget createButton(String symbol) {
        SymbolTab.Type type = tab.getType();
        PasteSymbolButtonWidget pasteSymbolButtonWidget = new PasteSymbolButtonWidget(getX(), getY(), this, symbol);
        if(type.hasFullWidthButtons()) pasteSymbolButtonWidget.setWidth(this.getWidth()-2-SmoothScrollableContainerWidget.ScrollbarStyle.SLIM.getWidth());
        if(!type.hasTooltip()) pasteSymbolButtonWidget.setTooltip(null);
        return pasteSymbolButtonWidget;
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.renderWidget(drawContext, mouseX, mouseY, delta);
        if(emptyText != null) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            List<OrderedText> orderedTexts = textRenderer.wrapLines(emptyText, width - 4);
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0, 0, 200);
            int centerX = this.getX() + this.getWidth() / 2;
            int startY = this.getY() + (this.getHeight() / 2) - (orderedTexts.size() * textRenderer.fontHeight / 2);
            for (int i = 0; i < orderedTexts.size(); i++) {
                OrderedText orderedText = orderedTexts.get(i);
                int dy = startY + (i * textRenderer.fontHeight);
                drawContext.drawText(textRenderer, orderedText, centerX - textRenderer.getWidth(orderedText) / 2, dy, 0x66FFFFFF, false);
            }
            drawContext.getMatrices().pop();
        }
    }
}
