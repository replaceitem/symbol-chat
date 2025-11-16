package net.replaceitem.symbolchat.gui.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import net.replaceitem.symbolchat.mixin.widget.AbstractScrollAreaAccessor;

public abstract class SmoothScrollableContainerWidget extends AbstractContainerWidget {
    public static final int SLIM_SCROLLBAR_WIDTH = 2;
    
    private boolean smoothScrolling;
    private double scrollTarget;
    private static final double scrollSpeed = 20;
    private ScrollbarStyle scrollbarStyle = ScrollbarStyle.VANILLA;
    private boolean scrollbarHovered;

    public SmoothScrollableContainerWidget(int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
    }

    public void setSmoothScrolling(boolean smoothScrolling) {
        this.smoothScrolling = smoothScrolling;
    }
    public void setScrollbarStyle(ScrollbarStyle scrollbarStyle) {
        this.scrollbarStyle = scrollbarStyle;
    }

    @Override
    protected double scrollRate() {
        return Minecraft.getInstance().hasControlDown() ? scrollSpeed * 3 : scrollSpeed;
    }

    @Override
    public void setScrollAmount(double scrollY) {
        super.setScrollAmount(scrollY);
        if(((AbstractScrollAreaAccessor) this).isScrolling()) this.scrollTarget = scrollAmount();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.visible) return false;
        double deltaY = verticalAmount * this.scrollRate();
        if(smoothScrolling) {
            this.scrollTarget = Mth.clamp(this.scrollTarget - deltaY, 0, maxScrollAmount());
        } else {
            this.setScrollAmount(this.scrollTarget - deltaY);
            this.scrollTarget = scrollAmount();
        }
        return true;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if(smoothScrolling) scrollSmooth(delta);
        this.scrollbarHovered = getScrollbarThumbRect().containsPoint(mouseX, mouseY);
        if (this.visible) {
            context.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
            this.renderContents(context, mouseX, mouseY, delta);
            context.disableScissor();
            this.renderScrollbar(context, mouseX, mouseY);
        }
    }
    
    protected abstract void renderContents(GuiGraphics context, int mouseX, int mouseY, float delta);
    
    private void scrollSmooth(float delta) {
        double scrollY = scrollAmount();
        if(scrollTarget == scrollY) return;
        scrollY = Mth.lerp(((AbstractScrollAreaAccessor) this).isScrolling() ? 1 : 1-Math.pow(2, -delta/0.4), scrollY, this.scrollTarget);
        if(Math.abs(scrollTarget - scrollY) < 0.5) scrollY = scrollTarget;
        this.setScrollAmount(scrollY);
    }
    
    public int getScrollbarThumbWidth() {
        return scrollbarStyle.getWidth();
    }

    @Override
    protected void renderScrollbar(GuiGraphics context, int mouseX, int mouseY) {
        if(scrollbarStyle == ScrollbarStyle.VANILLA) {
            super.renderScrollbar(context, mouseX, mouseY);
        } else {
            if(!scrollbarVisible()) return;
            ScreenRectangle rect = getScrollbarThumbRect();
            context.fill(rect.left(), rect.top(), rect.right(), rect.bottom(), scrollbarHovered || ((AbstractScrollAreaAccessor) this).isScrolling() ? 0xFFFFFFFF : 0xFFA0A0A0);
        }
    }

    @Override
    protected int scrollBarX() {
        return this.getRight() - this.getScrollbarThumbWidth();
    }

    public ScreenRectangle getScrollbarThumbRect() {
        return new ScreenRectangle(
                this.scrollBarX(),
                scrollbarVisible() ? this.scrollBarY() : getY(),
                this.getScrollbarThumbWidth(),
                this.scrollerHeight()
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
