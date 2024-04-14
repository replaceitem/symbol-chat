package net.replaceitem.symbolchat.gui.container;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

public class ScrollableContainer extends ScrollableWidget {
    private final ClickableWidget child;

    public ScrollableContainer(int x, int y, int w, int h, ClickableWidget widget) {
        super(x, y, w, h, Text.empty());
        widget.setPosition(x, y);
        widget.setWidth(w);
        widget.setHeight(h);
        this.child = widget;
        refreshPositions();
    }

    public void refreshPositions() {
        if(this.child instanceof LayoutContainer<?> layoutContainer) layoutContainer.refreshPositions();
        this.setScrollY(getScrollY());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.isWithinBounds(mouseX, mouseY)) {
            double scrolledMouseY = mouseY + getScrollY();
            if(child.mouseClicked(mouseX, scrolledMouseY, button)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(this.isWithinBounds(mouseX, mouseY)) {
            double scrolledMouseY = mouseY + getScrollY();
            if(child.mouseScrolled(mouseX, scrolledMouseY, horizontalAmount, verticalAmount)) return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(this.isWithinBounds(mouseX, mouseY)) {
            double scrolledMouseY = mouseY + getScrollY();
            if(child.mouseReleased(mouseX, scrolledMouseY, button)) return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(this.isWithinBounds(mouseX, mouseY)) {
            double scrolledMouseY = mouseY + getScrollY();
            if(child.mouseDragged(mouseX, scrolledMouseY, button, deltaX, deltaY)) return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    protected int getContentsHeight() {
        return this.child.getHeight();
    }

    @Override
    protected int getMaxScrollY() {
        return Math.max(0, this.getContentsHeight() - this.height);
    }

    @Override
    protected boolean overflows() {
        return this.getContentsHeight() > this.height;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return Screen.hasControlDown() ? 50 : 7;
    }

    // overriding just to not crop scissor by 1px
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            this.drawBox(context);
            context.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
            context.getMatrices().push();
            context.getMatrices().translate(0.0, -this.getScrollY(), 0.0);
            this.renderContents(context, mouseX, mouseY, delta);
            context.getMatrices().pop();
            context.disableScissor();
            this.renderOverlay(context);
        }
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        int scrolledMouseY = mouseY + (int) getScrollY();
        if(child instanceof ContainerWidgetImpl containerWidget) {
            ScreenRect navigationFocus = this.getNavigationFocus();
            ScreenRect shiftedNavigationFocus = new ScreenRect(navigationFocus.getLeft(), navigationFocus.getTop() + (int) getScrollY(), navigationFocus.width(), navigationFocus.height());
            containerWidget.renderInside(context, mouseX, scrolledMouseY, delta, shiftedNavigationFocus);
        } else {
            child.render(context, mouseX, scrolledMouseY, delta);
        }
        /*this.gridWidget.forEachChild(clickableWidget -> {
            if(clickableWidget.getY()+clickableWidget.getHeight() > getY() + getScrollY() && clickableWidget.getY() < getY() + getHeight() + getScrollY())
                clickableWidget.render(context, mouseX, mouseY + (int) getScrollY(), delta);
        });*/ // TODO remove if not needed for reference
    }

    @Override
    protected void drawScrollbar(DrawContext context) {
        int scrollbarHeight = this.getScrollbarThumbHeight();
        int scrollbarX = this.getX() + this.width - 1;
        int scrollbarY = Math.max(this.getY(), (int)this.getScrollY() * (this.height - scrollbarHeight) / this.getMaxScrollY() + this.getY());
        context.fill(scrollbarX, scrollbarY, scrollbarX+1, scrollbarY + scrollbarHeight, 0xFFA0A0A0);
    }


    @Override
    protected void drawBox(DrawContext context) {
        //context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, this.backgroundColor);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        child.appendNarrations(builder);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        this.child.setX(x);
        this.refreshPositions();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.child.setY(y);
        this.refreshPositions();
    }
}
