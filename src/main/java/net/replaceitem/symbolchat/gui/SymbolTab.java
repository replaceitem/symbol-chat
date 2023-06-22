package net.replaceitem.symbolchat.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolStorage;
import net.replaceitem.symbolchat.gui.widget.ScrollableGridWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SymbolTab extends AbstractParentElement implements Widget, Drawable, Element {

    protected static int COLUMNS = 9;
    
    public static final Text NO_FAVORITE_SYMBOLS = Text.translatable("symbolchat.no_favorite_symbols");
    public static final Text NO_CLOTHCONFIG = Text.translatable("symbolchat.no_clothconfig");

    public SymbolSelectionPanel symbolSelectionPanel;

    protected List<Element> children;
    protected int x, y;
    private final int width;
    private final int height;
    
    protected boolean isEmpty = false;

    protected Consumer<String> symbolConsumer;

    protected final ScrollableGridWidget scrollableGridWidget;
    
    public static SymbolTab fromCategory(Consumer<String> symbolInsertable, SymbolCategory symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y, int height) {
        if(symbols == SymbolStorage.kaomojis) {
            return new KaomojiTab(symbolInsertable, symbols, symbolSelectionPanel, x, y, height);
        } else if(symbols == SymbolStorage.all) {
            return new SearchTab(symbolInsertable, symbols, symbolSelectionPanel, x, y, height);
        } else {
            return new SymbolTab(symbolInsertable, symbols, symbolSelectionPanel, x, y, height);
        }
    }

    public SymbolTab(Consumer<String> symbolConsumer, SymbolCategory symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y, int height) {
        this.x = x;
        this.y = y;
        this.width = SymbolSelectionPanel.WIDTH;
        this.height = height;
        this.symbolConsumer = symbolConsumer;
        this.children = new ArrayList<>();
        this.symbolSelectionPanel = symbolSelectionPanel;
        this.scrollableGridWidget = createScrollableGridWidget();

        this.children.add(scrollableGridWidget);

        this.refresh(symbols);
    }

    public void refresh(SymbolCategory symbolCategory) {
        scrollableGridWidget.clearElements();
        addSymbols(symbolCategory);
        this.scrollableGridWidget.refreshPositions();
    }

    protected void addSymbols(SymbolCategory symbolCategory) {
        for (String symbol : symbolCategory.getSymbols()) {
            this.scrollableGridWidget.add(createButton(symbol));
        }
        this.isEmpty = symbolCategory.getSymbols().isEmpty();
    }
    
    protected ScrollableGridWidget createScrollableGridWidget() {
        return new ScrollableGridWidget(this.x, this.y, this.width, this.height, COLUMNS);
    }

    protected PasteSymbolButtonWidget createButton(String symbol) {
        return new PasteSymbolButtonWidget(x, y, this.symbolConsumer, symbol);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if(this.isEmpty) {
            Text text = this.getNoSymbolsText();
            if(text == null) return;
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            List<OrderedText> orderedTexts = textRenderer.wrapLines(text, SymbolSelectionPanel.WIDTH - 4);
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0,0,200);
            for(int i = 0; i < orderedTexts.size(); i++) {
                int dy = this.y + 2 + (i * textRenderer.fontHeight);
                drawContext.drawText(textRenderer, orderedTexts.get(i), this.x + 2, dy, 0x66FFFFFF, false);
            }
            drawContext.getMatrices().pop();
            return;
        }

        this.scrollableGridWidget.render(drawContext, mouseX, mouseY, delta);
    }
    
    public Text getNoSymbolsText() {
        return SymbolChat.clothConfigEnabled ? NO_FAVORITE_SYMBOLS : NO_CLOTHCONFIG;
    }

    @Override
    public List<? extends Element> children() {
        return this.children;
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
