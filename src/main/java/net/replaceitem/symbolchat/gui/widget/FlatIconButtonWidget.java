package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.replaceitem.symbolchat.SymbolChat;
import org.jetbrains.annotations.Nullable;

public class FlatIconButtonWidget extends TextIconButtonWidget.IconOnly {
    private boolean outlined = false;

    public FlatIconButtonWidget(int width, int height, Text message, int textureWidth, int textureHeight, ButtonTextures buttonTextures, PressAction pressAction, @Nullable Text tooltipText, @Nullable ButtonWidget.NarrationSupplier narrationSupplier) {
        super(width, height, message, textureWidth, textureHeight, buttonTextures, pressAction, tooltipText, narrationSupplier);
    }

    protected int getBackgroundColor() {
        return this.isHovered() ? SymbolChat.config.buttonActiveColor.get() : SymbolChat.config.buttonColor.get();
    }

    public void setOutlined(boolean outlined) {
        this.outlined = outlined;
    }

    public boolean isOutlined() {
        return outlined;
    }

    @Override
    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
        if(getMessage() != null && getMessage() != ScreenTexts.EMPTY) this.drawScrollableText(context, textRenderer, 0, color);
    }

    public void drawOutline(DrawContext drawContext, int color) {
        int alphaColor = ColorHelper.withAlpha((int) (this.alpha*255), color);
        drawContext.drawHorizontalLine(this.getX()-1, this.getX()+width, this.getY()-1, alphaColor);
        drawContext.drawVerticalLine(this.getX()-1, this.getY()-1, this.getY()+height, alphaColor);
        drawContext.drawHorizontalLine(this.getX()-1, this.getX()+width, this.getY()+height, alphaColor);
        drawContext.drawVerticalLine(this.getX()+width, this.getY()-1, this.getY()+height, alphaColor);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), ColorHelper.mix(getBackgroundColor(), ColorHelper.getWhite(alpha)));
        if(outlined) drawOutline(context, 0xFFFFFFFF);
        int textureX = this.getX() + this.getWidth() / 2 - this.textureWidth / 2;
        int textureY = this.getY() + this.getHeight() / 2 - this.textureHeight / 2;
        int textColor = this.isHovered() ? SymbolChat.config.buttonTextHoverColor.get() : SymbolChat.config.buttonTextColor.get();
        if(texture != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture.enabled(), textureX, textureY, this.textureWidth, this.textureHeight, ColorHelper.withAlpha((int) (this.alpha*255), textColor));
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        this.drawMessage(context, minecraftClient.textRenderer, textColor);
    }

    public interface PressAction extends ButtonWidget.PressAction {
        void onPress(FlatIconButtonWidget button);
        @Override
        default void onPress(ButtonWidget button) {
            if(button instanceof FlatIconButtonWidget flatIconButtonWidget) this.onPress(flatIconButtonWidget);
        }
    }
}
