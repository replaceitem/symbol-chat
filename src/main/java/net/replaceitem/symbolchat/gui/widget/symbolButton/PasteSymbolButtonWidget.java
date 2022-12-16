package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.config.ConfigProvider;
import net.replaceitem.symbolchat.gui.SymbolTab;

import java.util.Locale;
import java.util.stream.Collectors;

public class PasteSymbolButtonWidget extends SymbolButtonWidget {
    protected SymbolTab symbolTab;

    public String getSymbol() {
        return this.symbol;
    }

    protected String symbol;

    public PasteSymbolButtonWidget(Screen screen, int x, int y, SymbolTab symbolTab, String symbol) {
        super(screen, x, y, symbol);
        this.symbolTab = symbolTab;
        this.symbol = symbol;
        String tooltipText = symbol.codePoints().mapToObj(operand -> generateCapitalization(Character.getName(operand))).collect(Collectors.joining(", "));
        this.setTooltip(Tooltip.of(Text.of(tooltipText)));
        this.setTooltipDelay(SymbolChat.config.getSymbolTooltipMode().delay);
    }

    public void placeInTabGrid(int gx, int gy) {
        this.setX(symbolTab.getX()+1+(gx * (SymbolButtonWidget.symbolSize+1)));
        this.setY(symbolTab.getY()+1+(gy * (SymbolButtonWidget.symbolSize+1)));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.symbolTab.pasteSymbol(this.symbol);
    }

    private static String generateCapitalization(String upper) {
        StringBuilder newString = new StringBuilder();
        String lower = upper.toLowerCase(Locale.ROOT);
        newString.append(upper.charAt(0));
        for (int i = 1; i < upper.length(); i++) {
            newString.append(Character.isAlphabetic(upper.charAt(i-1)) ? lower.charAt(i) : upper.charAt(i));
        }
        return newString.toString();
    }
}
