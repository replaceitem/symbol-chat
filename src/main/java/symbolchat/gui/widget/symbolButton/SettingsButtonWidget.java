package symbolchat.gui.widget.symbolButton;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import symbolchat.SymbolChat;

public class SettingsButtonWidget extends SymbolButtonWidget implements Drawable, Element, Narratable {

    public SettingsButtonWidget(Screen screen, int x, int y) {
        super(screen, x, y, "⚙");
        this.width = 15;
        this.height = 15;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if(SymbolChat.clothConfigEnabled) {
            MinecraftClient.getInstance().setScreen(SymbolChat.config.getConfigScreen(this.screen));
        }
    }

    @Override
    protected MutableText getNarrationMessage() {
        return new TranslatableText("text.autoconfig.symbol-chat.title");
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, SymbolChat.config.getButtonColor());
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int z = this.isHovered() ? 0xFFFFFF : 0xA0A0A0;
            drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, z | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
        builder.put(NarrationPart.HINT, "Symbol chat button");
    }
    
    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        Text tooltip = new TranslatableText(SymbolChat.clothConfigEnabled ? "text.autoconfig.symbol-chat.title" : "symbolchat.no_clothconfig");
        if(this.isMouseOver(mouseX,mouseY))
            screen.renderTooltip(matrices,tooltip,mouseX,mouseY);
    }
}
