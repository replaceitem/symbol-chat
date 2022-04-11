package symbolchat.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import symbolchat.SymbolChat;
import symbolchat.SymbolList;
import symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import symbolchat.gui.widget.symbolButton.SymbolButtonWidget;

import java.util.ArrayList;
import java.util.List;

public class SymbolTab extends DrawableHelper implements Drawable, Element {

    protected static final int symbolsWidth = 8;
    protected static final int symbolsHeight = 16;

    public static final int width,height;
    
    public static final Text NO_CUSTOM_SYMBOLS = new TranslatableText("symbolchat.no_custom_symbols");
    public static final Text NO_CLOTHCONFIG = new TranslatableText("symbolchat.no_clothconfig");

    public SymbolSelectionPanel symbolSelectionPanel;
    
    protected SymbolList symbols;
    
    private int scroll;
    private final int maxScroll;

    static {
        //size of tabs for number of symbols plus margin
        width = symbolsWidth * SymbolButtonWidget.symbolSize + (SymbolTab.symbolsWidth+1);
        height = symbolsHeight * SymbolButtonWidget.symbolSize + (SymbolTab.symbolsHeight+1);
    }

    protected List<SymbolButtonWidget> symbolButtons;
    protected int x,y;

    protected Screen screen;

    public SymbolTab(Screen screen, SymbolList symbols, int x, int y, SymbolSelectionPanel symbolSelectionPanel) {
        this.x = x;
        this.y = y;
        this.screen = screen;
        this.symbolButtons = new ArrayList<>();
        this.symbolSelectionPanel = symbolSelectionPanel;
        this.symbols = symbols;
        this.scroll = 0;
        loadSymbols();
        int count = this.symbolButtons.size();
        int rows = (count/symbolsWidth) + Math.min(count%symbolsWidth,1);
        this.maxScroll = Math.max(rows-symbolsHeight, 0);
    }

    public void loadSymbols() {
        symbolButtons.clear();
        for(int i = 0; i < this.symbols.items.size(); i++) {
            int widgetX = this.x+1+(i%symbolsWidth*(SymbolButtonWidget.symbolSize+1));
            int widgetY = this.y+1+(i/symbolsWidth*(SymbolButtonWidget.symbolSize+1));
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
            int row = (i/symbolsWidth-scroll);
            if(row >= 0 && row < symbolsHeight) {
                symbolButtons.get(i).render(matrices, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(SymbolButtonWidget symbolButtonWidget : symbolButtons) {
            if(symbolButtonWidget.mouseClicked(mouseX,mouseY,button)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.scroll = Math.max(Math.min(this.scroll - ((int) amount),maxScroll),0);
        System.out.println(scroll);
        for(int i = 0; i < this.symbolButtons.size(); i++) {
            this.symbolButtons.get(i).y = this.y+1+((i/symbolsWidth-scroll)*(SymbolButtonWidget.symbolSize+1));
        }
        return true;
    }

    public void pasteSymbol(String symbol) {
        symbolSelectionPanel.onSymbolPasted(symbol);
    }
}
