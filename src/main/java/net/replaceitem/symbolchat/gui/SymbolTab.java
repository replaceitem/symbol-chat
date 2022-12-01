package net.replaceitem.symbolchat.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolList;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import net.replaceitem.symbolchat.SymbolChat;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SymbolTab extends AbstractParentElement implements Drawable, Element {

    protected static int columns = 8;
    protected static int rows = 16;

    public static int width = columns * (SymbolButtonWidget.symbolSize+1) + 1;
    public static int height = rows * (SymbolButtonWidget.symbolSize+1);
    
    public static final Text NO_CUSTOM_SYMBOLS = Text.translatable("symbolchat.no_custom_symbols");
    public static final Text NO_CLOTHCONFIG = Text.translatable("symbolchat.no_clothconfig");

    public SymbolSelectionPanel symbolSelectionPanel;
    
    protected int scroll;
    protected int maxScroll;

    protected List<PasteSymbolButtonWidget> symbolButtons;
    protected int x;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    protected int y;

    protected Screen screen;
    
    public static SymbolTab fromList(Screen screen, SymbolList symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y) {
        if(symbols.id.equals("kaomojis")) {
            return new KaomojiTab(screen, symbols, symbolSelectionPanel, x, y);
        } else {
            return new SymbolTab(screen, symbols, symbolSelectionPanel, x, y);
        }
    }

    public SymbolTab(Screen screen, SymbolList symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y) {
        this.x = x;
        this.y = y;
        this.screen = screen;
        this.symbolButtons = new ArrayList<>();
        this.symbolSelectionPanel = symbolSelectionPanel;
        this.scroll = 0;
        init(symbols);
    }

    protected void init(SymbolList symbols) {
        this.loadSymbols(symbols);
        this.rearrangeSymbols();
    }
    
    protected int getColumns() {
        return columns;
    }

    protected PasteSymbolButtonWidget createButton(int x, int y, String symbol) {
        return new PasteSymbolButtonWidget(screen, x, y, this, symbol);
    }

    public void loadSymbols(SymbolList symbols) {
        symbolButtons.clear();
        for(int i = 0; i < symbols.items.size(); i++) {
            int widgetX = 0;
            int widgetY = 0;
            symbolButtons.add(createButton(widgetX, widgetY, symbols.items.get(i)));
        }
    }

    protected void rearrangeSymbols() {
        int count = this.buttons().size();
        int totalRows = (count / getColumns()) + Math.min(count % getColumns(),1);
        this.maxScroll = Math.max(totalRows - rows, 0);
        this.scroll = MathHelper.clamp(this.scroll, 0, maxScroll);
        for (int i = 0; i < buttons().size(); i++) {
            PasteSymbolButtonWidget button = buttons().get(i);
            int row = i / getColumns() - scroll;
            int col = i % getColumns();
            button.placeInTabGrid(col, row);
            button.visible = row >= 0 && row < rows;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(symbolButtons.size() == 0) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            Text text = SymbolChat.clothConfigEnabled ? NO_CUSTOM_SYMBOLS : NO_CLOTHCONFIG;
            List<OrderedText> orderedTexts = textRenderer.wrapLines(text, width - 4);
            matrices.push();
            matrices.translate(0,0,200);
            for(int i = 0; i < orderedTexts.size(); i++) {
                int dy = this.y + 2 + (i * textRenderer.fontHeight);
                textRenderer.draw(matrices, orderedTexts.get(i), this.x + 2, dy, 0x66FFFFFF);
            }
            matrices.pop();
            return;
        }

        for (SymbolButtonWidget button : buttons()) {
            button.render(matrices, mouseX, mouseY, delta);
        }
        for (SymbolButtonWidget button : buttons()) {
            button.renderTooltip(matrices, mouseX, mouseY);
        }
    }

    protected List<? extends PasteSymbolButtonWidget> buttons() {
        return this.symbolButtons;
    }

    @Override
    public List<? extends Element> children() {
        return buttons();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.symbolSelectionPanel.getCurrentTab() != this || !this.isMouseOver(mouseX, mouseY)) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + width && mouseY < this.y + height;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(!this.isMouseOver(mouseX,mouseY)) return false;
        this.scroll -= amount;
        rearrangeSymbols();
        return true;
    }

    public void pasteSymbol(String symbol) {
        symbolSelectionPanel.onSymbolPasted(symbol);
    }
}
