package net.replaceitem.symbolchat.gui.widget;

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
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;

public class DropDownElementWidget<T> extends ClickableWidget implements Drawable, Element, Narratable {

    private final DropDownWidget<T> dropDownWidget;
    private final T element;
    private final int index;

    public DropDownElementWidget(int x, int y, int width, int height, T element, int index, DropDownWidget<T> dropDownWidget) {
        super(x, y, width, height, Text.empty());
        this.element = element;
        this.dropDownWidget = dropDownWidget;
        this.index = index;
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            RenderSystem.disableDepthTest();
            drawContext.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, SymbolChat.config.getButtonColor());
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int z = this.isHovered() ? 16777215 : 10526880;
            drawContext.drawCenteredTextWithShadow(textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, z | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.dropDownWidget.changeSelected(this.index);
    }

    @Override
    public Text getMessage() {
        return Text.literal(element.toString());
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
        builder.put(NarrationPart.HINT, "Dropdown element: " + element.toString());
    }

    public T getElement() {
        return element;
    }
}
