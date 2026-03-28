package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.replaceitem.symbolchat.SymbolChat;
import org.lwjgl.glfw.GLFW;

public abstract class SymbolButtonWidget extends AbstractWidget implements Renderable, GuiEventListener, NarrationSupplier {
    public static final int SYMBOL_SIZE = 12;
    public static final int GRID_SPCAING = SYMBOL_SIZE + 1;

    public SymbolButtonWidget(int x, int y, String symbol) {
        this(x, y, SYMBOL_SIZE, SYMBOL_SIZE, symbol);
    }

    public SymbolButtonWidget(int x, int y, int w, int h, String symbol) {
        super(x, y, w, h, Component.literal(symbol));
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        return false; // default behaviour consumes any release event which is problematic when trying to un-set ScrollableWidget.scrollbarDragged
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo input) {
        return super.isValidClickButton(input) || input.button() == GLFW.GLFW_MOUSE_BUTTON_2;
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return Component.literal("Add Symbol");
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if(shouldRenderBackground()) {
            this.extractBackgroundRenderState(graphics);
        }
        Font textRenderer = Minecraft.getInstance().font;
        int textColor = this.isHighlighted() ? SymbolChat.config.buttonTextHoverColor.get() : SymbolChat.config.buttonTextColor.get();
        drawSymbol(graphics, textRenderer, this.getMessage(), textColor);
        this.renderOverlay(graphics);
    }

    protected void extractBackgroundRenderState(GuiGraphicsExtractor drawContext) {
            int bg = getBackgroundColor();
            drawContext.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bg);
    }

    protected boolean isHighlighted() {
        return this.isHovered();
    }

    protected int getBackgroundColor() {
        return this.isHighlighted() ? SymbolChat.config.buttonActiveColor.get() : SymbolChat.config.buttonColor.get();
    }
    
    protected boolean shouldRenderBackground() {
        return true;
    }
    
    protected boolean shouldRenderTextWithShadow() {
        return true;
    }

    protected void renderOverlay(GuiGraphicsExtractor graphics) {
        if(this.shouldDrawOutline()) {
            this.extractOutlineRenderState(graphics);
        }
    }

    protected boolean shouldDrawOutline() {
        return false;
    }

    protected void extractOutlineRenderState(GuiGraphicsExtractor drawContext) {
        drawContext.horizontalLine(this.getX()-1, this.getX()+width, this.getY()-1, 0xFFFFFFFF);
        drawContext.verticalLine(this.getX()-1, this.getY()-1, this.getY()+height, 0xFFFFFFFF);
        drawContext.horizontalLine(this.getX()-1, this.getX()+width, this.getY()+height, 0xFFFFFFFF);
        drawContext.verticalLine(this.getX()+width, this.getY()-1, this.getY()+height, 0xFFFFFFFF);
    }
    
    protected void drawCorners(GuiGraphicsExtractor graphics, int color) {
        int lastX = this.getX()+SYMBOL_SIZE-1;
        int lastY = this.getY()+SYMBOL_SIZE-1;
        for(int i = 0; i < 2; i++) {
            int offset = i*(SYMBOL_SIZE-1);
            int x = this.getX() + offset;
            int y = this.getY() + offset;
            graphics.horizontalLine(getX(), getX()+1, y, color);
            graphics.horizontalLine(lastX-1, lastX, y, color);
            // why does drawVertical work differently -_-
            graphics.verticalLine(x, getY()-1, getY()+2, color);
            graphics.verticalLine(x, lastY-2, lastY+1, color);
        }
    }

    protected void drawSymbol(GuiGraphicsExtractor graphics, Font textRenderer, Component text, int color) {
        FormattedCharSequence orderedText = text.getVisualOrderText();
        int centerX = this.getX() + this.width / 2;
        int y = this.getY() + (this.height - 8) / 2;
        graphics.text(textRenderer, orderedText, centerX - textRenderer.width(orderedText) / 2, y, color, shouldRenderTextWithShadow());
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }
}
