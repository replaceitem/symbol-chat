package net.replaceitem.symbolchat.gui.container;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.mixin.ScrollableWidgetAccessor;

public abstract class SmoothScrollableContainerWidget extends ContainerWidget {
    public static final int SLIM_SCROLLBAR_WIDTH = 2;
    
    private boolean smoothScrolling;
    private double scrollTarget;
    private ScrollbarStyle scrollbarStyle;
    private boolean scrollbarHovered;

    public SmoothScrollableContainerWidget(int x, int y, int width, int height) {
        super(x, y, width, height, ScreenTexts.EMPTY);
    }

    public void setSmoothScrolling(boolean smoothScrolling) {
        this.smoothScrolling = smoothScrolling;
    }
    public void setScrollbarStyle(ScrollbarStyle scrollbarStyle) {
        this.scrollbarStyle = scrollbarStyle;
    }

    @Override
    public void setScrollY(double scrollY) {
        super.setScrollY(scrollY);
        if(((ScrollableWidgetAccessor) this).isScrollbarDragged()) this.scrollTarget = getScrollY();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
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
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if(smoothScrolling) scrollSmooth(delta);
        this.scrollbarHovered = getScrollbarThumbRect().contains(mouseX, mouseY);
        if (this.visible) {
            context.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
            this.renderContents(context, mouseX, mouseY, delta);
            context.disableScissor();
            this.drawScrollbar(context);
        }
    }
    
    protected abstract void renderContents(DrawContext context, int mouseX, int mouseY, float delta);
    
    private void scrollSmooth(float delta) {
        double scrollY = getScrollY();
        if(scrollTarget == scrollY) return;
        scrollY = MathHelper.lerp(((ScrollableWidgetAccessor) this).isScrollbarDragged() ? 1 : 1-Math.pow(2, -delta/0.4), scrollY, this.scrollTarget);
        if(Math.abs(scrollTarget - scrollY) < 0.5) scrollY = scrollTarget;
        this.setScrollY(scrollY);
    }
    
    protected int getScrollbarThumbWidth() {
        return scrollbarStyle.getWidth();
    }

    @Override
    protected void drawScrollbar(DrawContext context) {
        if(scrollbarStyle == ScrollbarStyle.VANILLA) {
            super.drawScrollbar(context);
        } else {
            if(!overflows()) return;
            ScreenRect rect = getScrollbarThumbRect();
            context.fill(rect.getLeft(), rect.getTop(), rect.getRight(), rect.getBottom(), scrollbarHovered || ((ScrollableWidgetAccessor) this).isScrollbarDragged() ? 0xFFFFFFFF : 0xFFA0A0A0);
        }
    }

    @Override
    protected int getScrollbarX() {
        return this.getRight() - this.getScrollbarThumbWidth();
    }

    public ScreenRect getScrollbarThumbRect() {
        return new ScreenRect(
                this.getScrollbarX(),
                overflows() ? this.getScrollbarThumbY() : getY(),
                this.getScrollbarThumbWidth(),
                this.getScrollbarThumbHeight()
        );
    }
    
    public enum ScrollbarStyle {
        VANILLA(SCROLLBAR_WIDTH),
        SLIM(SLIM_SCROLLBAR_WIDTH);
        
        private final int width;

        ScrollbarStyle(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }
    }
}
