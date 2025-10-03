package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.MouseInput;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolChat;
import org.lwjgl.glfw.GLFW;

public abstract class SymbolButtonWidget extends ClickableWidget implements Drawable, Element, Narratable {
    public static final int SYMBOL_SIZE = 12;
    public static final int GRID_SPCAING = SYMBOL_SIZE + 1;

    public SymbolButtonWidget(int x, int y, String symbol) {
        this(x, y, SYMBOL_SIZE, SYMBOL_SIZE, symbol);
    }

    public SymbolButtonWidget(int x, int y, int w, int h, String symbol) {
        super(x, y, w, h, Text.literal(symbol));
    }

    @Override
    public boolean mouseReleased(Click click) {
        return false; // default behaviour consumes any release event which is problematic when trying to un-set ScrollableWidget.scrollbarDragged
    }

    @Override
    protected boolean isValidClickButton(MouseInput input) {
        return super.isValidClickButton(input) || input.button() == GLFW.GLFW_MOUSE_BUTTON_2;
    }

    @Override
    protected MutableText getNarrationMessage() {
        return Text.literal("Add Symbol");
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if(shouldRenderBackground()) {
            this.renderBackground(drawContext);
        }
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textColor = this.isHighlighted() ? SymbolChat.config.buttonTextHoverColor.get() : SymbolChat.config.buttonTextColor.get();
        drawSymbol(drawContext, textRenderer, this.getMessage(), textColor);
        this.renderOverlay(drawContext);
    }
    
    

    protected void renderBackground(DrawContext drawContext) {
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

    protected void renderOverlay(DrawContext drawContext) {
        if(this.shouldDrawOutline()) {
            this.drawOutline(drawContext);
        }
    }

    protected boolean shouldDrawOutline() {
        return false;
    }

    protected void drawOutline(DrawContext drawContext) {
        drawContext.drawHorizontalLine(this.getX()-1, this.getX()+width, this.getY()-1, 0xFFFFFFFF);
        drawContext.drawVerticalLine(this.getX()-1, this.getY()-1, this.getY()+height, 0xFFFFFFFF);
        drawContext.drawHorizontalLine(this.getX()-1, this.getX()+width, this.getY()+height, 0xFFFFFFFF);
        drawContext.drawVerticalLine(this.getX()+width, this.getY()-1, this.getY()+height, 0xFFFFFFFF);
    }
    
    protected void drawCorners(DrawContext drawContext, int color) {
        int lastX = this.getX()+SYMBOL_SIZE-1;
        int lastY = this.getY()+SYMBOL_SIZE-1;
        for(int i = 0; i < 2; i++) {
            int offset = i*(SYMBOL_SIZE-1);
            int x = this.getX() + offset;
            int y = this.getY() + offset;
            drawContext.drawHorizontalLine(getX(), getX()+1, y, color);
            drawContext.drawHorizontalLine(lastX-1, lastX, y, color);
            // why does drawVertical work differently -_-
            drawContext.drawVerticalLine(x, getY()-1, getY()+2, color);
            drawContext.drawVerticalLine(x, lastY-2, lastY+1, color);
        }
    }

    protected void drawSymbol(DrawContext drawContext, TextRenderer textRenderer, Text text, int color) {
        OrderedText orderedText = text.asOrderedText();
        int centerX = this.getX() + this.width / 2;
        int y = this.getY() + (this.height - 8) / 2;
        drawContext.drawText(textRenderer, orderedText, centerX - textRenderer.getWidth(orderedText) / 2, y, color, shouldRenderTextWithShadow());
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}
