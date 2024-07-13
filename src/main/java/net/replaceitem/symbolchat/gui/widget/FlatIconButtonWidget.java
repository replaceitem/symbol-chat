package net.replaceitem.symbolchat.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.replaceitem.symbolchat.SymbolChat;
import org.jetbrains.annotations.Nullable;

public class FlatIconButtonWidget extends TextIconButtonWidget.IconOnly {
    private boolean outlined = false;
    
    public FlatIconButtonWidget(int width, int height, Text message, int textureWidth, int textureHeight, Identifier texture, PressAction pressAction, @Nullable ButtonWidget.NarrationSupplier narrationSupplier) {
        super(width, height, message, textureWidth, textureHeight, texture, pressAction, narrationSupplier);
    }

    protected int getBackgroundColor() {
        return this.isHovered() ? SymbolChat.config.getButtonHoverColor() : SymbolChat.config.getButtonColor();
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
        drawContext.drawHorizontalLine(this.getX()-1, this.getX()+width, this.getY()-1, color);
        drawContext.drawVerticalLine(this.getX()-1, this.getY()-1, this.getY()+height, color);
        drawContext.drawHorizontalLine(this.getX()-1, this.getX()+width, this.getY()+height, color);
        drawContext.drawVerticalLine(this.getX()+width, this.getY()-1, this.getY()+height, color);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), getBackgroundColor());
        if(outlined) drawOutline(context, 0xFFFFFFFF);
        int textureX = this.getX() + this.getWidth() / 2 - this.textureWidth / 2;
        int textureY = this.getY() + this.getHeight() / 2 - this.textureHeight / 2;
        int textColor = this.isHovered() ? SymbolChat.config.getButtonTextHoverColor() : SymbolChat.config.getButtonTextColor();
        if(texture != null) {
            context.setShaderColor(
                    (float) ColorHelper.Argb.getRed(textColor) / 255F,
                    (float) ColorHelper.Argb.getGreen(textColor) / 255F,
                    (float) ColorHelper.Argb.getBlue(textColor) / 255F,
                    (float) ColorHelper.Argb.getAlpha(textColor) / 255F
            );
            context.drawGuiTexture(this.texture, textureX, textureY, this.textureWidth, this.textureHeight);
            context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
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
