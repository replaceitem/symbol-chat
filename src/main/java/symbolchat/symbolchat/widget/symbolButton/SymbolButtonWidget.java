package symbolchat.symbolchat.widget.symbolButton;

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
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.MathHelper;
import symbolchat.symbolchat.Config;

public abstract class SymbolButtonWidget extends ClickableWidget implements Drawable, Element, Narratable {

    public static final int symbolSize = 12;

    protected Screen screen;

    public SymbolButtonWidget(Screen screen, int x, int y, String symbol) {
        super(x, y, symbolSize, symbolSize, new LiteralText(symbol));
        this.screen = screen;
    }

    @Override
    public abstract void onClick(double mouseX, double mouseY);

    @Override
    protected MutableText getNarrationMessage() {
        return new LiteralText("Add Symbol");
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, Config.button_color);
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int z = this.isHovered() ? 16777215 : 10526880;
            drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, z | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
        builder.put(NarrationPart.HINT, "Symbol chat button");
    }
}
