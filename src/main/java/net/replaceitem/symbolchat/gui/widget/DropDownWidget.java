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
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;

import java.util.ArrayList;
import java.util.List;

public class DropDownWidget<T> extends ClickableWidget implements Drawable, Element, Narratable {

    public final List<DropDownElementWidget<T>> elements;
    private final GridWidget gridWidget;
    public int selected;
    public boolean expanded;

    public DropDownWidget(int x, int y, int width, int height, List<T> elementList, int defaultSelection) {
        super(x, y, width, height, Text.empty());
        this.elements = new ArrayList<>();
        this.gridWidget = new GridWidget(this.getX() + 1, this.getY()+this.getHeight()+1);
        for(int i = 0; i < elementList.size(); i++) {
            DropDownElementWidget<T> element = new DropDownElementWidget<>(0, 0, this.width - 2, this.height, elementList.get(i), i, this);
            this.elements.add(element);
            this.gridWidget.add(element, i, 0);
        }
        this.gridWidget.setRowSpacing(1);
        this.gridWidget.refreshPositions();
        this.expanded = false;
        this.selected = defaultSelection;
    }

    @Override
    public void renderButton(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            RenderSystem.disableDepthTest();
            drawContext.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, SymbolChat.config.getButtonColor());
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int z = this.isHovered() ? 16777215 : 10526880;
            drawContext.drawCenteredTextWithShadow(textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, z | MathHelper.ceil(this.alpha * 255.0F) << 24);

            if(this.expanded) {
                drawContext.fill(this.getX(), this.getY()+this.height, this.getX() + this.width, this.getY() + this.height + 1 + this.elements.size()*(this.height+1), SymbolChat.config.getHudColor());
                for(DropDownElementWidget<?> element : elements) {
                    element.render(drawContext,mouseX,mouseY,delta);
                }
            }
        }
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        this.gridWidget.setX(this.getX() + 1);
        this.gridWidget.refreshPositions();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.gridWidget.setY(this.getY()+this.getHeight()+1);
        this.gridWidget.refreshPositions();
    }

    @Override
    public Text getMessage() {
        return this.elements.get(selected).getMessage();
    }
    
    public Text getTextForElement(T element) {
        return Text.literal(element.toString());
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.expanded = !this.expanded;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.expanded) {
            for(DropDownElementWidget<?> element : elements) {
                if(element.mouseClicked(mouseX,mouseY,button)) return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void changeSelected(int index) {
        this.selected = index;
        this.expanded = false;
        this.onSelection(index, elements.get(index).getElement());
    }

    public void onSelection(int index, T element) {}

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
        builder.put(NarrationPart.HINT, "Dropdown: " + getSelection().toString());
    }
    public T getSelection() {
        return this.elements.get(selected).getElement();
    }
}
