package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.Util;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class PasteSymbolButtonWidget extends SymbolButtonWidget {
    @Nullable
    protected final Context context;
    protected final String symbol;
    private boolean isFavorite;

    public PasteSymbolButtonWidget(int x, int y, @Nullable Context context, String symbol) {
        this(x, y, context, symbol, Tooltip.create(Component.nullToEmpty(Util.getCapitalizedSymbolName(symbol))));
    }

    public PasteSymbolButtonWidget(int x, int y, @Nullable Context context, String symbol, Tooltip tooltip) {
        super(x, y, symbol);
        this.symbol = symbol;
        this.context = context;
        this.setTooltip(tooltip);
        this.setTooltipDelay(SymbolChat.getConfig().symbolTooltipMode.get().getDelay());
        this.isFavorite = SymbolChat.getSymbolManager().isFavorite(symbol);
    }

    @Override
    protected void renderOverlay(GuiGraphicsExtractor graphics) {
        super.renderOverlay(graphics);
        if(isFavorite) this.drawCorners(graphics, SymbolChat.getConfig().favoriteColor.get());
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        if(click.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
            this.onSymbolClicked();
            return;
        }
        if(click.button() == GLFW.GLFW_MOUSE_BUTTON_2) {
            onRightClick();
            if(this.context != null) this.context.refresh();
        }
    }

    public void onSymbolClicked() {
        if(this.context != null) {
            this.context.onSymbolClicked(this.getSymbol());
        }
    }

    protected void onRightClick() {
        if(symbol.codePoints().count() > 1) return; // With current config implementation, favoriting more than once codepoint isn't possible
        boolean currentlyFavorite = SymbolChat.getSymbolManager().isFavorite(this.symbol);
        String currentFavorites = SymbolChat.getConfig().favoriteSymbols.get();

        if(currentlyFavorite) {
            SymbolChat.getConfig().favoriteSymbols.set(currentFavorites.replace(this.getSymbol(), ""));
        } else {
            SymbolChat.getConfig().favoriteSymbols.set(currentFavorites + getSymbol());
        }
        SymbolChat.getConfig().scheduleSave();
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
