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
            setZOffset(10000000);
            int backgroundColor = this.isSelected() ? SymbolChat.config.getButtonHoverColor() : SymbolChat.config.getButtonColor();
            fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, backgroundColor);
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            int rgb = this.isHovered() ? 0xFFFFFF : 0xA0A0A0;
            int argb = rgb | MathHelper.ceil(this.alpha * 255.0F) << 24;


            //setZOffset(10000000);
            matrices.push();
            matrices.translate(0.0, 0.0, 200.0f);

            MatrixStack textMatrices = new MatrixStack();
            textMatrices.translate(0.0, 0.0, 0 + 200.0f);
            drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, argb);
            matrices.pop();


            /*
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.translate(0.0, 0.0, 0 + 200.0f);
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            textRenderer.draw(this.getMessage(), (float)(x + 19 - 2 - textRenderer.getWidth(this.getMessage())), (float)(y + 6 + 3), 0xFFFFFF, true, matrixStack.peek().getPositionMatrix(), (VertexConsumerProvider)immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            immediate.draw();
             */

            /* In drawCenteredText:
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            int i = this.draw(text, x, y, color, shadow, matrix, (VertexConsumerProvider)immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            immediate.draw();
            */

            this.renderTooltip(matrices, mouseX, mouseY);
        }
    }

    protected boolean isSelected() {
        return false;
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {

    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
        builder.put(NarrationPart.HINT, "Symbol chat button");
    }
}
