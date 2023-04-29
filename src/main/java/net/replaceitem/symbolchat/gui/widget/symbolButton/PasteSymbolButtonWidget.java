package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.Util;

public class PasteSymbolButtonWidget extends SymbolButtonWidget {
    protected SymbolInsertable symbolInsertable;

    protected String symbol;

    public PasteSymbolButtonWidget(int x, int y, SymbolInsertable symbolInsertable, String symbol) {
        this(x, y, symbolInsertable, symbol, Tooltip.of(Text.of(Util.getCapitalizedSymbolName(symbol))));
    }
    
    public PasteSymbolButtonWidget(int x, int y, SymbolInsertable symbolInsertable, String symbol, Tooltip tooltip) {
        super(x, y, symbol);
        this.symbolInsertable = symbolInsertable;
        this.symbol = symbol;
        this.setTooltip(tooltip);
        this.setTooltipDelay(SymbolChat.config.getSymbolTooltipMode().delay);
    }
    
    @Override
    public boolean onClick(int button) {
        this.symbolInsertable.insertSymbol(this.symbol);
        return true;
    }
}
