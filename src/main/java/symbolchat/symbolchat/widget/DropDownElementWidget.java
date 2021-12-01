package symbolchat.symbolchat.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import symbolchat.symbolchat.Config;

public class DropDownElementWidget<T> extends ClickableWidget implements Drawable, Element, Narratable {

    private final DropDownWidget<?> dropDownWidget;
    private final T element;
    private final int index;

    public DropDownElementWidget(int x, int y, int width, int height, T element, int index, DropDownWidget<?> dropDownWidget) {
        super(x, y, width, height, new LiteralText(""));
        this.element = element;
        this.dropDownWidget = dropDownWidget;
        this.index = index;
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
    public void onClick(double mouseX, double mouseY) {
        this.dropDownWidget.changeSelected(this.index);
    }

    @Override
    public Text getMessage() {
        return new LiteralText(element.toString());
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
        builder.put(NarrationPart.HINT, "Dropdown element: " + element.toString());
    }

    public T getElement() {
        return element;
    }
}
