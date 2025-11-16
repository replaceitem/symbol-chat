package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
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
        this(x, y, context, symbol, Tooltip.create(Component.nullToEmpty(Util.getCapitalizedSymbolName(symbol))));
    }

    public PasteSymbolButtonWidget(int x, int y, @Nullable Context context, String symbol, Tooltip tooltip) {
        super(x, y, symbol);
        this.symbol = symbol;
        this.context = context;
        this.setTooltip(tooltip);
        this.setTooltipDelay(SymbolChat.config.symbolTooltipMode.get().getDelay());
        this.isFavorite = SymbolChat.symbolManager.isFavorite(symbol);
    }

    @Override
    protected void renderOverlay(GuiGraphics drawContext) {
        super.renderOverlay(drawContext);
        if(isFavorite) this.drawCorners(drawContext, SymbolChat.config.favoriteColor.get());
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
        boolean currentlyFavorite = SymbolChat.symbolManager.isFavorite(this.symbol);
        String currentFavorites = SymbolChat.config.favoriteSymbols.get();

        if(currentlyFavorite) {
            SymbolChat.config.favoriteSymbols.set(currentFavorites.replace(this.getSymbol(), ""));
        } else {
            SymbolChat.config.favoriteSymbols.set(currentFavorites + getSymbol());
        }
        SymbolChat.config.scheduleSave();
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
