package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.replaceitem.symbolchat.SymbolChat;
import org.jspecify.annotations.Nullable;

public class FlatIconButtonWidget extends SpriteIconButton.CenteredIcon {
    private boolean outlined = false;

    public FlatIconButtonWidget(int width, int height, Component message, int textureWidth, int textureHeight, @Nullable WidgetSprites buttonTextures, PressAction pressAction, @Nullable Component tooltipText, Button.@Nullable CreateNarration narrationSupplier) {
        // buttonTextures is not nullable, but is not used in this subclass
        //noinspection DataFlowIssue
        super(width, height, message, textureWidth, textureHeight, buttonTextures, pressAction, tooltipText, narrationSupplier);
    }

    protected int getBackgroundColor() {
        return this.isHovered() ? SymbolChat.getConfig().buttonActiveColor.get() : SymbolChat.getConfig().buttonColor.get();
    }

    public void setOutlined(boolean outlined) {
        this.outlined = outlined;
    }

    public boolean isOutlined() {
        return outlined;
    }

    public void drawOutline(GuiGraphicsExtractor drawContext, int color) {
        int alphaColor = ARGB.color((int) (this.alpha*255), color);
        drawContext.horizontalLine(this.getX()-1, this.getX()+width, this.getY()-1, alphaColor);
        drawContext.verticalLine(this.getX()-1, this.getY()-1, this.getY()+height, alphaColor);
        drawContext.horizontalLine(this.getX()-1, this.getX()+width, this.getY()+height, alphaColor);
        drawContext.verticalLine(this.getX()+width, this.getY()-1, this.getY()+height, alphaColor);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), ARGB.multiply(getBackgroundColor(), ARGB.white(alpha)));
        if(outlined) drawOutline(graphics, 0xFFFFFFFF);
        int textureX = this.getX() + this.getWidth() / 2 - this.spriteWidth / 2;
        int textureY = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
        int textColor = this.isHovered() ? SymbolChat.getConfig().buttonTextHoverColor.get() : SymbolChat.getConfig().buttonTextColor.get();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite.enabled(), textureX, textureY, this.spriteWidth, this.spriteHeight, ARGB.color((int) (this.alpha * 255), textColor));
        this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
    }

    public interface PressAction extends Button.OnPress {
        void onPress(FlatIconButtonWidget button);
        @Override
        default void onPress(Button button) {
            if(button instanceof FlatIconButtonWidget flatIconButtonWidget) this.onPress(flatIconButtonWidget);
        }
    }
}
