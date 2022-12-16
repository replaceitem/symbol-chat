package net.replaceitem.symbolchat.gui.widget.symbolButton;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;

public abstract class SymbolButtonWidget extends ClickableWidget implements Drawable, Element, Narratable {

    public static final int symbolSize = 12;

    protected Screen screen;

    public SymbolButtonWidget(Screen screen, int x, int y, String symbol) {
        super(x, y, symbolSize, symbolSize, Text.literal(symbol));
        this.screen = screen;
    }

    public SymbolButtonWidget(Screen screen, int x, int y, int w, int h, String symbol) {
        super(x, y, w, h, Text.literal(symbol));
        this.screen = screen;
    }

    @Override
    public abstract void onClick(double mouseX, double mouseY);

    @Override
    protected MutableText getNarrationMessage() {
        return Text.literal("Add Symbol");
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            RenderSystem.disableDepthTest();
            int backgroundColor = this.isSelected() ? SymbolChat.config.getButtonHoverColor() : SymbolChat.config.getButtonColor();
            fill(matrices, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, backgroundColor);
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            int rgb = this.isHovered() ? 0xFFFFFF : 0xA0A0A0;
            int argb = rgb | MathHelper.ceil(this.alpha * 255.0F) << 24;

            matrices.push();
            matrices.translate(0.0, 0.0, 200.0f);

            MatrixStack textMatrices = new MatrixStack();
            textMatrices.translate(0.0, 0.0, 0 + 200.0f);
            drawCenteredText(matrices, textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, argb);
            matrices.pop();
        }
    }

    protected boolean isSelected() {
        return false;
    }


    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
        builder.put(NarrationPart.HINT, "Symbol chat button");
    }
}
