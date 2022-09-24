package symbolchat.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import symbolchat.SymbolChat;
import symbolchat.SymbolList;
import symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import symbolchat.gui.widget.symbolButton.SymbolButtonWidget;

import java.util.ArrayList;
import java.util.List;

public class SymbolTab extends AbstractParentElement implements Drawable, Element {

    protected static int columns = 8;
    protected static int rows = 16;

    public static int width = columns * (SymbolButtonWidget.symbolSize+1) + 1;
    public static int height = rows * (SymbolButtonWidget.symbolSize+1) + 1;
    
    public static final Text NO_CUSTOM_SYMBOLS = Text.translatable("symbolchat.no_custom_symbols");
    public static final Text NO_CLOTHCONFIG = Text.translatable("symbolchat.no_clothconfig");

    public SymbolSelectionPanel symbolSelectionPanel;
    
    protected SymbolList symbols;
    
    protected int scroll;
    protected final int maxScroll;

    protected List<SymbolButtonWidget> symbolButtons;
    protected int x,y;

    protected Screen screen;
    
    public static SymbolTab fromList(Screen screen, SymbolList symbols, SymbolSelectionPanel symbolSelectionPanel) {
        if(symbols.id.equals("kaomojis")) {
            return new KaomojiTab(screen, symbols, symbolSelectionPanel);
        } else {
            return new SymbolTab(screen, symbols, symbolSelectionPanel);
        }
    }

    public SymbolTab(Screen screen, SymbolList symbols, SymbolSelectionPanel symbolSelectionPanel) {
        this.x = symbolSelectionPanel.x;
        this.y = symbolSelectionPanel.y;
        this.screen = screen;
        this.symbolButtons = new ArrayList<>();
        this.symbolSelectionPanel = symbolSelectionPanel;
        this.symbols = symbols;
        this.scroll = 0;
        loadSymbols();
        int count = this.symbolButtons.size();
        int totalRows = (count / getColumns()) + Math.min(count % getColumns(),1);
        this.maxScroll = Math.max(totalRows - rows, 0);
    }
    
    protected int getColumns() {
        return columns;
    }

    public void loadSymbols() {
        symbolButtons.clear();
        for(int i = 0; i < this.symbols.items.size(); i++) {
            int widgetX = this.x+1+(i % columns *(SymbolButtonWidget.symbolSize+1));
            int widgetY = this.y+1+(i / columns *(SymbolButtonWidget.symbolSize+1));
            SymbolButtonWidget widget = new PasteSymbolButtonWidget(screen, widgetX, widgetY, this, symbols.items.get(i));
            symbolButtons.add(widget);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(symbolButtons.size() == 0) {
            if(this.symbols.id.equals("custom")) {
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                Text text = SymbolChat.clothConfigEnabled ? NO_CUSTOM_SYMBOLS : NO_CLOTHCONFIG;
                List<OrderedText> orderedTexts = textRenderer.wrapLines(text, width - 4);
                for(int i = 0; i < orderedTexts.size(); i++) {
                    int dy = this.y + 2 + (i * textRenderer.fontHeight);
                    textRenderer.draw(matrices, orderedTexts.get(i), this.x + 2, dy, 0x66FFFFFF);
                }
            }
            return;
        }
        for (int i = 0; i < symbolButtons.size(); i++) {
            int row = (i/ getColumns() - scroll);
            if(row >= 0 && row < rows) {
                symbolButtons.get(i).render(matrices, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public List<? extends Element> children() {
        return this.symbolButtons;
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
        this.scroll = Math.max(Math.min(this.scroll - ((int) amount),maxScroll),0);
        for(int i = 0; i < this.symbolButtons.size(); i++) {
            this.symbolButtons.get(i).y = this.y+1+((i / getColumns() - scroll)*(SymbolButtonWidget.symbolSize+1));
        }
        return true;
    }

    public void pasteSymbol(String symbol) {
        symbolSelectionPanel.onSymbolPasted(symbol);
    }
}
