package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SearchUtil;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.container.ContainerWidgetImpl;
import net.replaceitem.symbolchat.gui.container.GridLayoutContainer;
import net.replaceitem.symbolchat.gui.container.ScrollableContainer;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import net.replaceitem.symbolchat.resource.SymbolTab;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class SymbolTabWidget extends ContainerWidgetImpl implements PasteSymbolButtonWidget.Context {
    
    private static final int SEARCH_BAR_HEIGHT = 10;
    public static final Text NO_RESULTS = Text.translatable("symbolchat.symbol_panel.no_search_results");
    public static final Text NO_FAVORITE_SYMBOLS = Text.translatable("symbolchat.symbol_panel.no_favorite_symbols");
    public static final Text NO_CLOTHCONFIG = Text.translatable("symbolchat.symbol_panel.no_clothconfig");

    private final SymbolSelectionPanel symbolSelectionPanel;

    @Nullable
    private Text emptyText;
    protected final SymbolTab tab;

    @Nullable
    private SymbolSearchBar searchBar;
    protected final ScrollableContainer scrollableWidget;
    protected final GridLayoutContainer symbolContainer;

    public SymbolTabWidget(int x, int y, int width, int height, SymbolTab symbolTab, SymbolSelectionPanel symbolSelectionPanel, int panelColumns) {
        super(x, y, width, height);
        this.tab = symbolTab;
        this.symbolSelectionPanel = symbolSelectionPanel;
        int columns = this.tab.getType().getColumns(panelColumns);
        this.symbolContainer = new GridLayoutContainer(0, 0, 0, 0, columns);
        this.symbolContainer.setSpacing(1);
        int offset = this.tab.hasSearchBar() ? SEARCH_BAR_HEIGHT+2 : 0;
        this.scrollableWidget = new ScrollableContainer(getX()+1, getY() + offset, this.getWidth()-1, this.getHeight()-offset, symbolContainer);
        this.scrollableWidget.setSmoothScrolling(true);
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
        symbolContainer.clearElements();
        addSymbols();
        this.scrollableWidget.refreshPositions();
    }

    protected void addSymbols() {
        Stream<String> stream = this.tab.streamSymbols();
        if(searchBar != null) {
            stream = SearchUtil.performSearch(stream, searchBar.getText());
        }
        List<PasteSymbolButtonWidget> buttons = stream.map(this::createButton).toList();
        buttons.forEach(symbolContainer::addChildren);
        this.emptyText = this.getEmptyText(buttons.isEmpty());
    }
    
    private Text getEmptyText(boolean noSymbols) {
        if(!noSymbols) return null;
        if(SymbolChat.symbolManager.isOnlyFavorites(tab)) {
            return SymbolChat.clothConfigEnabled ? NO_FAVORITE_SYMBOLS : NO_CLOTHCONFIG;
        }
        return NO_RESULTS;
    }

    protected PasteSymbolButtonWidget createButton(String symbol) {
        SymbolTab.Type type = tab.getType();
        PasteSymbolButtonWidget pasteSymbolButtonWidget = new PasteSymbolButtonWidget(getX(), getY(), this, symbol);
        if(type.hasFullWidthButtons()) pasteSymbolButtonWidget.setWidth(this.getWidth()-2-ScrollableContainer.SCROLLBAR_WIDTH);
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
