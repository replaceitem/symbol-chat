package net.replaceitem.symbolchat.gui.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import net.replaceitem.symbolchat.mixin.widget.AbstractScrollAreaAccessor;

public abstract class SmoothScrollableContainerWidget extends AbstractContainerWidget {
    public static final int SLIM_SCROLLBAR_WIDTH = 2;
    
    private boolean smoothScrolling;
    private double scrollTarget;
    private ScrollbarStyle scrollbarStyle = ScrollbarStyle.VANILLA;
    private boolean scrollbarHovered;

    public SmoothScrollableContainerWidget(int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY, AbstractScrollArea.defaultSettings(20));
    }

    public void setSmoothScrolling(boolean smoothScrolling) {
        this.smoothScrolling = smoothScrolling;
    }
    public void setScrollbarStyle(ScrollbarStyle scrollbarStyle) {
        this.scrollbarStyle = scrollbarStyle;
    }

    @Override
    protected double scrollRate() {
        return Minecraft.getInstance().hasControlDown() ? super.scrollRate() * 3 : super.scrollRate();
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
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if(smoothScrolling) scrollSmooth(a);
        this.scrollbarHovered = getScrollbarThumbRect().containsPoint(mouseX, mouseY);
        if (this.visible) {
            graphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
            this.extractContentRenderState(graphics, mouseX, mouseY, a);
            graphics.disableScissor();
            this.extractScrollbar(graphics, mouseX, mouseY);
        }
    }

    protected abstract void extractContentRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta);
    
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
    protected void extractScrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if(scrollbarStyle == ScrollbarStyle.VANILLA) {
            super.extractScrollbar(graphics, mouseX, mouseY);
        } else {
            if(!scrollable()) return;
            ScreenRectangle rect = getScrollbarThumbRect();
            graphics.fill(rect.left(), rect.top(), rect.right(), rect.bottom(), scrollbarHovered || ((AbstractScrollAreaAccessor) this).isScrolling() ? 0xFFFFFFFF : 0xFFA0A0A0);
        }
    }

    @Override
    protected int scrollBarX() {
        return this.getRight() - this.getScrollbarThumbWidth();
    }

    public ScreenRectangle getScrollbarThumbRect() {
        return new ScreenRectangle(
                this.scrollBarX(),
                scrollable() ? this.scrollBarY() : getY(),
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
