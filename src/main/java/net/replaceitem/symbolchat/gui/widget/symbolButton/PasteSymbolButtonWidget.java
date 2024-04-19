package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.Util;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class PasteSymbolButtonWidget extends SymbolButtonWidget {
    @Nullable
    protected final Context context;
    protected final String symbol;
    private boolean isFavorite;

    public PasteSymbolButtonWidget(int x, int y, @Nullable Context context, String symbol) {
        this(x, y, context, symbol, Tooltip.of(Text.of(Util.getCapitalizedSymbolName(symbol))));
    }

    public PasteSymbolButtonWidget(int x, int y, @Nullable Context context, String symbol, Tooltip tooltip) {
        super(x, y, symbol);
        this.symbol = symbol;
        this.context = context;
        this.setTooltip(tooltip);
        this.setTooltipDelay(SymbolChat.config.getSymbolTooltipMode().getDelay());
        this.isFavorite = SymbolChat.symbolManager.isFavorite(symbol);
    }

    @Override
    protected void renderOverlay(DrawContext drawContext) {
        super.renderOverlay(drawContext);
        if(isFavorite) this.drawCorners(drawContext, SymbolChat.config.getFavoriteColor());
    }

    @Override
    public boolean onClick(int button) {
        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && this.context != null) {
            this.context.onSymbolClicked(this.getSymbol());
            return true;
        }
        if(button == GLFW.GLFW_MOUSE_BUTTON_2) {
            onRightClick();
            if(this.context != null) this.context.refresh();
        }
        return true;
    }
    
    protected void onRightClick() {
        if(symbol.codePoints().count() > 1) return; // With current config implementation, favoriting more than once codepoint isn't possible
        boolean currentlyFavorite = SymbolChat.symbolManager.isFavorite(this.symbol);
        if(currentlyFavorite) SymbolChat.config.removeFavorite(this.getSymbol());
        else SymbolChat.config.addFavorite(this.getSymbol());
        this.isFavorite = !currentlyFavorite;
    }

    public String getSymbol() {
        return symbol;
    }
    
    public interface Context {
        void onSymbolClicked(String symbol);
        void refresh();
    }
}
