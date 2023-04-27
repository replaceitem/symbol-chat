package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.gui.SymbolTab;

import java.util.Locale;
import java.util.stream.Collectors;

public class PasteSymbolButtonWidget extends SymbolButtonWidget {
    protected SymbolInsertable symbolInsertable;

    protected String symbol;

    public PasteSymbolButtonWidget(int x, int y, SymbolInsertable symbolInsertable, String symbol) {
        super(x, y, symbol);
        this.symbolInsertable = symbolInsertable;
        this.symbol = symbol;
        this.setTooltip(Tooltip.of(Text.of(getTooltipText())));
        this.setTooltipDelay(SymbolChat.config.getSymbolTooltipMode().delay);
    }

    protected String getTooltipText() {
        return symbol.codePoints().mapToObj(operand -> generateCapitalization(Character.getName(operand))).collect(Collectors.joining(", "));
    }

    public void placeInTabGrid(SymbolTab tab, int gx, int gy) {
        this.setX(tab.getX()+1+(gx * (SymbolButtonWidget.SYMBOL_SIZE +1)));
        this.setY(tab.getY()+1+(gy * (SymbolButtonWidget.SYMBOL_SIZE +1)));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.symbolInsertable.insertSymbol(this.symbol);
    }

    @Override
    public boolean isSelected() {
        return this.isFocused();
    }

    /**
     * @param string input string to be converted
     * @return The provided string, with capitalization applied to the first character of each word.
     */
    private static String generateCapitalization(String string) {
        if(string == null) string = "Unknown";
        StringBuilder newString = new StringBuilder();
        String lower = string.toLowerCase(Locale.ROOT);
        newString.append(string.charAt(0));
        for (int i = 1; i < string.length(); i++) {
            newString.append(Character.isAlphabetic(string.charAt(i-1)) ? lower.charAt(i) : string.charAt(i));
        }
        return newString.toString();
    }
}
