package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.config.ConfigProvider;
import net.replaceitem.symbolchat.gui.SymbolTab;

import java.util.Locale;
import java.util.stream.Collectors;

public class PasteSymbolButtonWidget extends SymbolButtonWidget {

    protected static long startHover = Long.MAX_VALUE;
    protected static SymbolButtonWidget hoveredButton = null;

    protected SymbolTab symbolTab;

    public String getSymbol() {
        return this.symbol;
    }

    protected String symbol;

    public PasteSymbolButtonWidget(Screen screen, int x, int y, SymbolTab symbolTab, String symbol) {
        super(screen, x, y, symbol);
        this.symbolTab = symbolTab;
        this.symbol = symbol;
    }

    public void placeInTabGrid(int gx, int gy) {
        this.x = symbolTab.getX()+1+(gx * (SymbolButtonWidget.symbolSize+1));
        this.y = symbolTab.getY()+1+(gy * (SymbolButtonWidget.symbolSize+1));
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        super.renderTooltip(matrices, mouseX, mouseY);
        ConfigProvider.SymbolTooltipMode tooltipMode = SymbolChat.config.getSymbolTooltipMode();
        if(tooltipMode.equals(ConfigProvider.SymbolTooltipMode.OFF)) return;
        if(this.isHovered()) {
            if(hoveredButton != this) {
                hoveredButton = this;
                startHover = System.currentTimeMillis();
            }
            if(tooltipMode.equals(ConfigProvider.SymbolTooltipMode.ON) || System.currentTimeMillis() - startHover > 500) {
                screen.renderTooltip(matrices, Text.of(symbol.codePoints().mapToObj(operand -> generateCapitalization(Character.getName(operand))).collect(Collectors.joining(", "))), mouseX, mouseY);
            }
        }
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
