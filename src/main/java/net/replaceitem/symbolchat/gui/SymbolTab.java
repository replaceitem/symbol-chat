package net.replaceitem.symbolchat.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;

import java.util.ArrayList;
import java.util.List;

public class SymbolTab extends AbstractParentElement implements Drawable, Element {

    protected static int columns = 8;
    protected static int rows = 16;

    public static int width = columns * (SymbolButtonWidget.SYMBOL_SIZE +1) + 1;
    public static int height = rows * (SymbolButtonWidget.SYMBOL_SIZE +1);
    
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

    protected SymbolInsertable symbolInsertable;
    
    public static SymbolTab fromCategory(SymbolInsertable symbolInsertable, SymbolCategory symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y) {
        if(symbols.id.equals("kaomojis")) {
            return new KaomojiTab(symbolInsertable, symbols, symbolSelectionPanel, x, y);
        } else {
            return new SymbolTab(symbolInsertable, symbols, symbolSelectionPanel, x, y);
        }
    }

    public SymbolTab(SymbolInsertable symbolInsertable, SymbolCategory symbols, SymbolSelectionPanel symbolSelectionPanel, int x, int y) {
        this.x = x;
        this.y = y;
        this.symbolInsertable = symbolInsertable;
        this.symbolButtons = new ArrayList<>();
        this.symbolSelectionPanel = symbolSelectionPanel;
        this.scroll = 0;
        init(symbols);
    }

    protected void init(SymbolCategory symbols) {
        this.loadSymbols(symbols);
        this.arrangeButtons();
    }
    
    protected int getColumns() {
        return columns;
    }

    protected PasteSymbolButtonWidget createButton(int x, int y, String symbol) {
        return new PasteSymbolButtonWidget(x, y, this.symbolInsertable, symbol);
    }

    public void loadSymbols(SymbolCategory symbols) {
        symbolButtons.clear();
        for(int i = 0; i < symbols.symbols.size(); i++) {
            int widgetX = 0;
            int widgetY = 0;
            symbolButtons.add(createButton(widgetX, widgetY, symbols.symbols.get(i)));
        }
    }

    protected void arrangeButtons() {
        int count = this.buttons().size();
        int totalRows = (count / getColumns()) + Math.min(count % getColumns(),1);
        this.maxScroll = Math.max(totalRows - rows, 0);
        this.scroll = MathHelper.clamp(this.scroll, 0, maxScroll);
        for (int i = 0; i < buttons().size(); i++) {
            PasteSymbolButtonWidget button = buttons().get(i);
            int row = i / getColumns() - scroll;
            int col = i % getColumns();
            button.placeInTabGrid(this, col, row);
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
        arrangeButtons();
        return true;
    }
}
