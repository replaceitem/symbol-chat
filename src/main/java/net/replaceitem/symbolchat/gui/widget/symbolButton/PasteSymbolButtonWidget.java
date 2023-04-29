package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.Util;

import java.util.function.Consumer;

public class PasteSymbolButtonWidget extends SymbolButtonWidget {
    protected Consumer<String> symbolConsumer;

    protected String symbol;

    public PasteSymbolButtonWidget(int x, int y, Consumer<String> symbolConsumer, String symbol) {
        this(x, y, symbolConsumer, symbol, Tooltip.of(Text.of(Util.getCapitalizedSymbolName(symbol))));
    }
    
    public PasteSymbolButtonWidget(int x, int y, Consumer<String> symbolConsumer, String symbol, Tooltip tooltip) {
        super(x, y, symbol);
        this.symbolConsumer = symbolConsumer;
        this.symbol = symbol;
        this.setTooltip(tooltip);
        this.setTooltipDelay(SymbolChat.config.getSymbolTooltipMode().delay);
    }
    
    @Override
    public boolean onClick(int button) {
        this.symbolConsumer.accept(this.symbol);
        return true;
    }
}
