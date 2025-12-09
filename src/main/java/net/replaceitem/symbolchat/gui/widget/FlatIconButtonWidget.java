package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.replaceitem.symbolchat.SymbolChat;
import org.jetbrains.annotations.Nullable;

public class FlatIconButtonWidget extends SpriteIconButton.CenteredIcon {
    private boolean outlined = false;

    public FlatIconButtonWidget(int width, int height, Component message, int textureWidth, int textureHeight, WidgetSprites buttonTextures, PressAction pressAction, @Nullable Component tooltipText, @Nullable Button.CreateNarration narrationSupplier) {
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

    public void drawOutline(GuiGraphics drawContext, int color) {
        int alphaColor = ARGB.color((int) (this.alpha*255), color);
        drawContext.hLine(this.getX()-1, this.getX()+width, this.getY()-1, alphaColor);
        drawContext.vLine(this.getX()-1, this.getY()-1, this.getY()+height, alphaColor);
        drawContext.hLine(this.getX()-1, this.getX()+width, this.getY()+height, alphaColor);
        drawContext.vLine(this.getX()+width, this.getY()-1, this.getY()+height, alphaColor);
    }

    @Override
    public void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), ARGB.multiply(getBackgroundColor(), ARGB.white(alpha)));
        if(outlined) drawOutline(guiGraphics, 0xFFFFFFFF);
        int textureX = this.getX() + this.getWidth() / 2 - this.spriteWidth / 2;
        int textureY = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
        int textColor = this.isHovered() ? SymbolChat.config.buttonTextHoverColor.get() : SymbolChat.config.buttonTextColor.get();
        if(sprite != null) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite.enabled(), textureX, textureY, this.spriteWidth, this.spriteHeight, ARGB.color((int) (this.alpha*255), textColor));
        }
        this.renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
    }

    public interface PressAction extends Button.OnPress {
        void onPress(FlatIconButtonWidget button);
        @Override
        default void onPress(Button button) {
            if(button instanceof FlatIconButtonWidget flatIconButtonWidget) this.onPress(flatIconButtonWidget);
        }
    }
}
