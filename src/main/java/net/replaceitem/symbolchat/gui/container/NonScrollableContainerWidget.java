package net.replaceitem.symbolchat.gui.container;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class NonScrollableContainerWidget extends ContainerWidget {
    private final List<ClickableWidget> children = new ArrayList<>();
    
    public NonScrollableContainerWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }
    
    public void addChildren(ClickableWidget element) {
        this.children.add(element);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        for (Element child : this.children) {
            if(child instanceof Drawable drawable) drawable.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if(this.isMouseOver(click.x(), click.y())) {
            return super.mouseClicked(click, doubled);
        }
        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        Element focused = getFocused();
        if(focused instanceof Narratable narratable) narratable.appendNarrations(builder);
    }

    @Override
    public List<ClickableWidget> children() {
        return this.children;
    }
    
    public void clearElements() {
        this.children.clear();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return this.hoveredElement(mouseX, mouseY).filter(element -> element.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)).isPresent();
    }

    @Override
    public void setX(int x) {
        if(x == this.getX()) return;
        for (ClickableWidget child : children) {
            child.setX(child.getX() - this.getX() + x);
        }
        super.setX(x);
    }

    @Override
    public void setY(int y) {
        if(y == this.getY()) return;
        for (ClickableWidget child : children) {
            child.setY(child.getY() - this.getY() + y);
        }
        super.setY(y);
    }

    @Override
    protected int getContentsHeightWithPadding() {
        return height;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 0;
    }

    @Override
    protected boolean overflows() {
        return false;
    }
}
