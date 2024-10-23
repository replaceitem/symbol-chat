package net.replaceitem.symbolchat.gui.container;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.mixin.ScrollableWidgetAccessor;

public class ScrollableContainer extends ScrollableWidget {
    private final ClickableWidget child;
    public final static int SCROLLBAR_WIDTH = 2;
    private boolean scrollbarHovered;
    private boolean smoothScrolling;
    private double scrollTarget;

    public ScrollableContainer(int x, int y, int w, int h, ClickableWidget widget) {
        super(x, y, w, h, Text.empty());
        widget.setPosition(x, y);
        this.child = widget;
        refreshPositions();
    }

    public void setSmoothScrolling(boolean smoothScrolling) {
        this.smoothScrolling = smoothScrolling;
    }

    public void refreshPositions() {
        if(this.child instanceof LayoutContainer<?> layoutContainer) layoutContainer.refreshPositions();
        this.setScrollY(getScrollY());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!visible) return false;
        if(this.isWithinBounds(mouseX, mouseY)) {
            if(child.mouseClicked(mouseX, mouseY, button)) return true;
        }
        if (isScrollbarHovered((int) mouseX, (int) mouseY) && button == 0) {
            ((ScrollableWidgetAccessor) this).setScrollbarDragged(true);
            return true;
        }
        return false;
    }

    @Override
    protected void setScrollY(double scrollY) {
        super.setScrollY(scrollY);
        if(((ScrollableWidgetAccessor) this).isScrollbarDragged()) this.scrollTarget = getScrollY();
        this.child.setY(this.getY() - ((int) getScrollY()));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(this.isWithinBounds(mouseX, mouseY)) {
            if(child.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        }
        if (!this.visible) return false;
        
        double deltaY = verticalAmount * this.getDeltaYPerScroll();
        
        if(smoothScrolling) {
            this.scrollTarget = MathHelper.clamp(this.scrollTarget - deltaY, 0, getMaxScrollY());
        } else {
            this.setScrollY(this.scrollTarget - deltaY);
            this.scrollTarget = getScrollY();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(this.isWithinBounds(mouseX, mouseY)) {
            if(child.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(this.isWithinBounds(mouseX, mouseY)) {
            if(child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
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
        return Screen.hasControlDown() ? 50 : 20;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if(smoothScrolling) scrollSmooth(delta);
        scrollbarHovered = isScrollbarHovered(mouseX, mouseY);
        if (this.visible) {
            this.drawBox(context);
            context.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
            this.renderContents(context, mouseX, mouseY, delta);
            context.disableScissor();
            this.renderOverlay(context);
        }
    }

    private void scrollSmooth(float delta) {
        double scrollY = getScrollY();
        if(scrollTarget == scrollY) return;
        scrollY = MathHelper.lerp(((ScrollableWidgetAccessor) this).isScrollbarDragged() ? 1 : 1-Math.pow(2, -delta/0.4), scrollY, this.scrollTarget);
        if(Math.abs(scrollTarget - scrollY) < 0.5) scrollY = scrollTarget;
        this.setScrollY(scrollY);
    }

    private boolean isScrollbarHovered(int mouseX, int mouseY) {
        if(getMaxScrollY() == 0) return false;
        int scrollbarHeight = this.getScrollbarThumbHeight();
        int scrollbarX = this.getRight() - SCROLLBAR_WIDTH;
        int scrollbarY = Math.max(this.getY(), (int)this.getScrollY() * (this.height - scrollbarHeight) / this.getMaxScrollY() + this.getY());
        return mouseX >= scrollbarX && mouseX < scrollbarX+SCROLLBAR_WIDTH && mouseY >= scrollbarY && mouseY < scrollbarY+scrollbarHeight;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        child.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void drawScrollbar(DrawContext context) {
        if(getMaxScrollY() == 0) return;
        int scrollbarHeight = this.getScrollbarThumbHeight();
        int scrollbarX = this.getRight() - SCROLLBAR_WIDTH;
        int scrollbarY = Math.max(this.getY(), (int)this.getScrollY() * (this.height - scrollbarHeight) / this.getMaxScrollY() + this.getY());
        context.fill(scrollbarX, scrollbarY, scrollbarX+SCROLLBAR_WIDTH, scrollbarY + scrollbarHeight, scrollbarHovered || ((ScrollableWidgetAccessor) this).isScrollbarDragged() ? 0xFFFFFFFF : 0xFFA0A0A0);
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
        this.child.setY(y - ((int) getScrollY()));
        this.refreshPositions();
    }
}
