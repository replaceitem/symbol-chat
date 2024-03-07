package net.replaceitem.symbolchat.gui.widget.symbolButton;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;
import org.lwjgl.glfw.GLFW;

public abstract class SymbolButtonWidget extends ClickableWidget implements Drawable, Element, Narratable {
    public static final int SYMBOL_SIZE = 12;
    public static final int GRID_SPCAING = SYMBOL_SIZE + 1;
    
    private int backgroundColor;
    private int hoverBackgroundColor;

    public SymbolButtonWidget(int x, int y, String symbol) {
        this(x, y, SYMBOL_SIZE, SYMBOL_SIZE, symbol);
    }

    public SymbolButtonWidget(int x, int y, int w, int h, String symbol) {
        super(x, y, w, h, Text.literal(symbol));
        hoverBackgroundColor = SymbolChat.config.getButtonHoverColor();
        backgroundColor = SymbolChat.config.getButtonColor();
    }

    public void setBackgroundColors(int hoverColor) {
        this.hoverBackgroundColor = hoverColor;
        int alpha = hoverColor & 0xFF000000;
        int color = (
                ((((hoverColor >> 16) & 0xFF) / 2) << 16) |
                ((((hoverColor >> 8 ) & 0xFF) / 2) << 8 ) |
                ((((hoverColor      ) & 0xFF) / 2)      )
        );
        this.backgroundColor = alpha | color;
    }
    
    public void setBackgroundColors(int backgroundColor, int hoverBackgroundColor) {
        this.backgroundColor = backgroundColor;
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    public abstract boolean onClick(int button);

    // final, so the above is used instead
    @Override
    public final void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible) return false;
        if (this.isValidClickButton(button) && this.clicked(mouseX, mouseY)) {
            if(this.onClick(button)) {
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return super.isValidClickButton(button) || button == GLFW.GLFW_MOUSE_BUTTON_2;
    }

    @Override
    protected MutableText getNarrationMessage() {
        return Text.literal("Add Symbol");
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            RenderSystem.disableDepthTest();
            int bg = this.isSelected() ? hoverBackgroundColor : backgroundColor;
            drawContext.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bg);
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            int rgb = this.isSelected() ? 0xFFFFFF : 0xA0A0A0;
            int argb = rgb | MathHelper.ceil(this.alpha * 255.0F) << 24;

            drawContext.drawCenteredTextWithShadow(textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, argb);
            this.renderOverlay(drawContext);
        }
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

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}
