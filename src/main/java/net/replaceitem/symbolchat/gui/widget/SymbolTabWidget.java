package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SearchUtil;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolTab;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SymbolTabWidget extends AbstractParentElement implements Widget, Drawable, Element, PasteSymbolButtonWidget.Context {
    
    private static final int SEARCH_BAR_HEIGHT = 10;
    public static final Text NO_RESULTS = Text.translatable("symbolchat.no_search_results");
    public static final Text NO_FAVORITE_SYMBOLS = Text.translatable("symbolchat.no_favorite_symbols");
    public static final Text NO_CLOTHCONFIG = Text.translatable("symbolchat.no_clothconfig");
    private final int columns;

    public SymbolSelectionPanel symbolSelectionPanel;

    protected List<Element> children;
    @Nullable
    private Text emptyText;
    protected int x, y;
    private final int width;
    private final int height;
    
    protected final SymbolTab tab;

    protected Consumer<String> symbolConsumer;

    @Nullable
    private SymbolSearchBar searchBar;
    protected final ScrollableGridWidget scrollableGridWidget;

    public SymbolTabWidget(Consumer<String> symbolConsumer, SymbolTab symbolTab, SymbolSelectionPanel symbolSelectionPanel, int x, int y, int width, int height, int panelColumns) {
        this.tab = symbolTab;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.columns = this.tab.getType().getColumns(panelColumns);
        this.symbolConsumer = symbolConsumer;
        this.children = new ArrayList<>();
        this.symbolSelectionPanel = symbolSelectionPanel;
        this.scrollableGridWidget = createScrollableGridWidget();
        this.children.add(scrollableGridWidget);
        if(tab.hasSearchBar()) {
            this.searchBar = new SymbolSearchBar(this.x + 2, this.y + 1, getWidth() - 4, SEARCH_BAR_HEIGHT);
            this.searchBar.setChangedListener(s -> refresh());
            this.children.add(this.searchBar);
        }
        this.refresh();
    }

    @Override
    public void onSymbolClicked(String symbol) {
        symbolConsumer.accept(symbol);
    }

    @Override
    public void refresh() {
        scrollableGridWidget.clearElements();
        addSymbols();
        this.scrollableGridWidget.refreshPositions();
    }

    protected void addSymbols() {
        Stream<String> stream = this.tab.streamSymbols();
        if(searchBar != null) {
            stream = SearchUtil.performSearch(stream, searchBar.getText());
        }
        List<PasteSymbolButtonWidget> buttons = stream.map(this::createButton).toList();
        buttons.forEach(scrollableGridWidget::add);
        this.emptyText = this.getEmptyText(buttons.isEmpty());
    }
    
    private Text getEmptyText(boolean noSymbols) {
        if(!noSymbols) return null;
        if(SymbolChat.symbolManager.isOnlyFavorites(tab)) {
            return SymbolChat.clothConfigEnabled ? NO_FAVORITE_SYMBOLS : NO_CLOTHCONFIG;
        }
        return NO_RESULTS;
    }
    
    protected ScrollableGridWidget createScrollableGridWidget() {
        int offset = this.tab.hasSearchBar() ? SEARCH_BAR_HEIGHT+2 : 0;
        return new ScrollableGridWidget(x, y + offset, this.getWidth(), this.getHeight()-offset, columns);
    }

    protected PasteSymbolButtonWidget createButton(String symbol) {
        SymbolTab.Type type = tab.getType();
        PasteSymbolButtonWidget pasteSymbolButtonWidget = new PasteSymbolButtonWidget(x, y, this, symbol);
        if(type.hasFullWidthButtons()) pasteSymbolButtonWidget.setWidth(this.getWidth() - 2);
        if(!type.hasTooltip()) pasteSymbolButtonWidget.setTooltip(null);
        return pasteSymbolButtonWidget;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.scrollableGridWidget.render(drawContext, mouseX, mouseY, delta);
        if(emptyText != null) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            List<OrderedText> orderedTexts = textRenderer.wrapLines(emptyText, width - 4);
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0, 0, 200);
            int startY = this.y + (this.getHeight() / 2) - (orderedTexts.size() * textRenderer.fontHeight / 2);
            int centerX = this.x + this.getWidth() / 2;
            for (int i = 0; i < orderedTexts.size(); i++) {
                OrderedText orderedText = orderedTexts.get(i);
                int dy = startY + (i * textRenderer.fontHeight);
                drawContext.drawText(textRenderer, orderedText, centerX - textRenderer.getWidth(orderedText) / 2, dy, 0x66FFFFFF, false);
            }
            drawContext.getMatrices().pop();
        }
        if(this.searchBar != null) this.searchBar.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Element> children() {
        return this.children;
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        if(this.searchBar != null && this.searchBar.isFocused()) return;
        super.setFocused(focused);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + width && mouseY < this.y + height;
    }

    @Override
    public void setX(int x) {
        this.x = x;
        scrollableGridWidget.refreshPositions();
    }

    @Override
    public void setY(int y) {
        this.y = y;
        scrollableGridWidget.refreshPositions();
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        this.scrollableGridWidget.forEachChild(consumer);
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return new ScreenRect(new ScreenPos(x, y), width, height);
    }
}
