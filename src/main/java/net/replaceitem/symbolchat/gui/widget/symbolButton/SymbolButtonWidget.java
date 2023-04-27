package net.replaceitem.symbolchat.gui.widget.symbolButton;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;

public abstract class SymbolButtonWidget extends ClickableWidget implements Drawable, Element, Narratable {

    public static final int SYMBOL_SIZE = 12;


    public SymbolButtonWidget(int x, int y, String symbol) {
        super(x, y, SYMBOL_SIZE, SYMBOL_SIZE, Text.literal(symbol));
    }

    public SymbolButtonWidget(int x, int y, int w, int h, String symbol) {
        super(x, y, w, h, Text.literal(symbol));
    }

    @Override
    public abstract void onClick(double mouseX, double mouseY);

    @Override
    protected MutableText getNarrationMessage() {
        return Text.literal("Add Symbol");
    }

    @Override
    public void renderButton(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            RenderSystem.disableDepthTest();
            int backgroundColor = this.isSelected() ? SymbolChat.config.getButtonHoverColor() : SymbolChat.config.getButtonColor();
            drawContext.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, backgroundColor);
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            int rgb = this.isHovered() ? 0xFFFFFF : 0xA0A0A0;
            int argb = rgb | MathHelper.ceil(this.alpha * 255.0F) << 24;

            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0.0, 0.0, 200.0f);

            MatrixStack textMatrices = new MatrixStack();
            textMatrices.translate(0.0, 0.0, 0 + 200.0f);
            drawContext.drawCenteredTextWithShadow(textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, argb);
            drawContext.getMatrices().pop();
        }
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    /**
     * Just to make setFocused publicly accessible
     */
    public void makeFocused(boolean focused) {
        this.setFocused(focused);
    }


    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
        builder.put(NarrationPart.HINT, "Symbol chat button");
    }
}
