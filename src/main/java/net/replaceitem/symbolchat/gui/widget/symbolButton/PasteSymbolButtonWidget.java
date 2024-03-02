package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.Util;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class PasteSymbolButtonWidget extends SymbolButtonWidget {
    protected final Consumer<String> symbolConsumer;
    protected final String symbol;
    private boolean isFavorite;

    public PasteSymbolButtonWidget(int x, int y, Consumer<String> symbolConsumer, String symbol) {
        this(x, y, symbolConsumer, symbol, Tooltip.of(Text.of(Util.getCapitalizedSymbolName(symbol))));
    }

    public PasteSymbolButtonWidget(int x, int y, Consumer<String> symbolConsumer, String symbol, Tooltip tooltip) {
        super(x, y, symbol);
        this.symbol = symbol;
        this.symbolConsumer = symbolConsumer;
        this.setTooltip(tooltip);
        this.setTooltipDelay(SymbolChat.config.getSymbolTooltipMode().delay);
        this.isFavorite = SymbolChat.symbolManager.isFavorite(symbol);
    }

    @Override
    protected void renderOverlay(DrawContext drawContext) {
        super.renderOverlay(drawContext);
        if(isFavorite) this.drawCorners(drawContext);
    }

    protected void drawCorners(DrawContext drawContext) {
        int cornerColor = SymbolChat.config.getFavoriteColor();
        int lastX = this.getX()+SYMBOL_SIZE-1;
        int lastY = this.getY()+SYMBOL_SIZE-1;
        for(int i = 0; i < 2; i++) {
            int offset = i*(SYMBOL_SIZE-1);
            int x = this.getX() + offset;
            int y = this.getY() + offset;
            drawContext.drawHorizontalLine(getX(), getX()+1, y, cornerColor);
            drawContext.drawHorizontalLine(lastX-1, lastX, y, cornerColor);
            // why does drawVertical work differently -_-
            drawContext.drawVerticalLine(x, getY()-1, getY()+2, cornerColor);
            drawContext.drawVerticalLine(x, lastY-2, lastY+1, cornerColor);
        }
    }

    @Override
    public boolean onClick(int button) {
        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && this.symbolConsumer != null) this.symbolConsumer.accept(this.getSymbol());
        if(button == GLFW.GLFW_MOUSE_BUTTON_2) onRightClick();
        return true;
    }
    
    protected void onRightClick() {
        boolean currentlyFavorite = SymbolChat.symbolManager.isFavorite(this.symbol);
        if(currentlyFavorite) SymbolChat.config.removeFavorite(this.getSymbol());
        else SymbolChat.config.addFavorite(this.getSymbol());
        this.isFavorite = !currentlyFavorite;
        // TODO refresh tab
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getSymbol() {
        return symbol;
    }
}
