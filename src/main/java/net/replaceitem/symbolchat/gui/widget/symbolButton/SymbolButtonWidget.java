package net.replaceitem.symbolchat.gui.widget.symbolButton;

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

    @Override
    public abstract void onClick(double mouseX, double mouseY);

    @Override
    protected MutableText getNarrationMessage() {
        return Text.literal("Add Symbol");
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, SymbolChat.config.getButtonColor());
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int z = this.isHovered() ? 0xFFFFFF : 0xA0A0A0;
            if(this.isSelected()) {
                int color = SymbolChat.config.getOutlineColor();
                drawHorizontalLine(matrices, x-1, x+width, y-1, color);
                drawHorizontalLine(matrices, x-1, x+width, y+height, color);
                drawVerticalLine(matrices, x-1, y-1, y+height, color);
                drawVerticalLine(matrices, x+width, y-1, y+height, color);
            }
            drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, z | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }

    protected boolean isSelected() {
        return false;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
        builder.put(NarrationPart.HINT, "Symbol chat button");
    }
}
