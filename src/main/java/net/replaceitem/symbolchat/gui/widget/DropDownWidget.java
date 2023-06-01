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
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;

import java.util.ArrayList;
import java.util.List;

public class DropDownWidget<T> extends ClickableWidget implements Drawable, Element, Narratable {

    public final List<DropDownElementWidget<T>> elements;
    private final DropDownSelectionWidget selectionWidget;
    public int selected;
    public boolean expanded;

    public DropDownWidget(int x, int y, int width, int height, List<T> elementList, int defaultSelection) {
        super(x, y, width, height, Text.empty());
        this.elements = new ArrayList<>();
        this.selectionWidget = new DropDownSelectionWidget(this.getX(), this.getY()+this.getHeight(), this.width, 200);
        for(int i = 0; i < elementList.size(); i++) {
            DropDownElementWidget<T> element = new DropDownElementWidget<>(0, 0, this.width - 2, this.height, elementList.get(i), i, this);
            this.elements.add(element);
            this.selectionWidget.add(element, i);
        }
        this.selectionWidget.refreshPositions();
        this.expanded = false;
        this.selected = defaultSelection;
    }

    @Override
    public void renderButton(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if(!this.visible) return;
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0.0, 0.0, 1000.0f);
        RenderSystem.disableDepthTest();
        drawContext.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, SymbolChat.config.getButtonColor());
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int z = this.isHovered() ? 16777215 : 10526880;
        drawContext.drawCenteredTextWithShadow(textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, z | MathHelper.ceil(this.alpha * 255.0F) << 24);

        if(this.expanded) {
            this.selectionWidget.render(drawContext, mouseX, mouseY, delta);
        }
        drawContext.getMatrices().pop();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(this.expanded && this.selectionWidget.mouseScrolled(mouseX, mouseY, amount)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        this.selectionWidget.setX(this.getX());
        this.selectionWidget.refreshPositions();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.selectionWidget.setY(this.getY()+this.getHeight());
        this.selectionWidget.refreshPositions();
    }

    @Override
    public Text getMessage() {
        return this.elements.get(selected).getMessage();
    }
    
    public Text getTextForElement(T element) {
        return Text.literal(element.toString());
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || (this.expanded && this.selectionWidget.isMouseOver(mouseX, mouseY));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.expanded = !this.expanded;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.expanded) {
            this.selectionWidget.mouseClicked(mouseX, mouseY, button);
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
    
    
    private class DropDownSelectionWidget extends ScrollableWidget {
        
        private final GridWidget gridWidget;

        public DropDownSelectionWidget(int x, int y, int w, int h) {
            super(x, y, w, h, Text.empty());
            this.gridWidget = new GridWidget(x, y);
            this.gridWidget.setRowSpacing(1);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(this.isWithinBounds(mouseX, mouseY)) {
                this.gridWidget.forEachChild(clickableWidget -> clickableWidget.mouseClicked(mouseX, mouseY + getScrollY(), button));
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        protected int getContentsHeight() {
            return gridWidget.getHeight();
        }

        @Override
        protected boolean overflows() {
            return this.getContentsHeight() > this.height;
        }

        @Override
        protected double getDeltaYPerScroll() {
            return 10;
        }

        @Override
        protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
            this.gridWidget.forEachChild(clickableWidget -> clickableWidget.render(context, mouseX, mouseY + (int) getScrollY(), delta));
        }

        @Override
        protected void drawScrollbar(DrawContext context) {
            int scrollbarHeight = this.getScrollbarThumbHeight();
            int scrollbarX = this.getX() + this.width - 1;
            int scrollbarY = Math.max(this.getY(), (int)this.getScrollY() * (this.height - scrollbarHeight) / this.getMaxScrollY() + this.getY());
            context.fill(scrollbarX, scrollbarY, scrollbarX+1, scrollbarY + scrollbarHeight, 0xFFA0A0A0);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }

        public void add(DropDownElementWidget<T> element, int i) {
            this.gridWidget.add(element, i, 0);
        }

        @Override
        public void setX(int x) {
            super.setX(x);
            this.gridWidget.setX(x);
            this.refreshPositions();
        }

        @Override
        public void setY(int y) {
            super.setY(y);
            this.gridWidget.setY(y);
            this.refreshPositions();
        }

        public void refreshPositions() {
            this.gridWidget.refreshPositions();
        }

        @Override
        protected void drawBox(DrawContext context) {
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, SymbolChat.config.getHudColor());
        }
    }
}
