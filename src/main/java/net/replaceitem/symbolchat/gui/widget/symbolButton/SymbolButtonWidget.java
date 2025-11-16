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
    public void renderWidget(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        if(shouldRenderBackground()) {
            this.renderBackground(drawContext);
        }
        Font textRenderer = Minecraft.getInstance().font;
        int textColor = this.isHighlighted() ? SymbolChat.config.buttonTextHoverColor.get() : SymbolChat.config.buttonTextColor.get();
        drawSymbol(drawContext, textRenderer, this.getMessage(), textColor);
        this.renderOverlay(drawContext);
    }
    
    

    protected void renderBackground(GuiGraphics drawContext) {
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

    protected void renderOverlay(GuiGraphics drawContext) {
        if(this.shouldDrawOutline()) {
            this.drawOutline(drawContext);
        }
    }

    protected boolean shouldDrawOutline() {
        return false;
    }

    protected void drawOutline(GuiGraphics drawContext) {
        drawContext.hLine(this.getX()-1, this.getX()+width, this.getY()-1, 0xFFFFFFFF);
        drawContext.vLine(this.getX()-1, this.getY()-1, this.getY()+height, 0xFFFFFFFF);
        drawContext.hLine(this.getX()-1, this.getX()+width, this.getY()+height, 0xFFFFFFFF);
        drawContext.vLine(this.getX()+width, this.getY()-1, this.getY()+height, 0xFFFFFFFF);
    }
    
    protected void drawCorners(GuiGraphics drawContext, int color) {
        int lastX = this.getX()+SYMBOL_SIZE-1;
        int lastY = this.getY()+SYMBOL_SIZE-1;
        for(int i = 0; i < 2; i++) {
            int offset = i*(SYMBOL_SIZE-1);
            int x = this.getX() + offset;
            int y = this.getY() + offset;
            drawContext.hLine(getX(), getX()+1, y, color);
            drawContext.hLine(lastX-1, lastX, y, color);
            // why does drawVertical work differently -_-
            drawContext.vLine(x, getY()-1, getY()+2, color);
            drawContext.vLine(x, lastY-2, lastY+1, color);
        }
    }

    protected void drawSymbol(GuiGraphics drawContext, Font textRenderer, Component text, int color) {
        FormattedCharSequence orderedText = text.getVisualOrderText();
        int centerX = this.getX() + this.width / 2;
        int y = this.getY() + (this.height - 8) / 2;
        drawContext.drawString(textRenderer, orderedText, centerX - textRenderer.width(orderedText) / 2, y, color, shouldRenderTextWithShadow());
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }
}
