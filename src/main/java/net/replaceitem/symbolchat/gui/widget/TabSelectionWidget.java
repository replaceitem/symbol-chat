package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.resource.SymbolTab;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TabSelectionWidget extends AbstractParentElement implements Widget, Drawable, Element {

    private static final int HEIGHT = SymbolButtonWidget.GRID_SPCAING;
    private final List<SwitchTabButton> tabButtons;
    private int selectedTab;

    private int x, y;
    private final int width;

    public TabSelectionWidget(int x, int y, int width) {
        this.tabButtons = new ArrayList<>();
        this.selectedTab = SymbolChat.selectedTab;
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public void addTab(SymbolTab tab) {
        this.tabButtons.add(new SwitchTabButton(0, 0, this.tabButtons.size(), tab));
    }

    public void refreshPositions() {
        GridWidget gridWidget = new GridWidget(this.x+1, this.y);
        gridWidget.setColumnSpacing(1);
        GridWidget.Adder adder = gridWidget.createAdder(Integer.MAX_VALUE);
        this.tabButtons.forEach(adder::add);
        gridWidget.refreshPositions();
        this.setTab(this.selectedTab);
    }

    public void setTab(int tab) {
        int previousTab = this.getSelectedTab();
        this.selectedTab = MathHelper.clamp(tab, 0, this.tabButtons.size()-1);
        SymbolChat.selectedTab = this.selectedTab;
        for (SymbolButtonWidget child : this.tabButtons) {
            child.setY(y);
        }
        this.tabButtons.get(selectedTab).setY(y+1);
        if(previousTab != this.selectedTab) this.onTabSwitched(previousTab, selectedTab);
    }

    protected void onTabSwitched(int previousIndex, int newIndex) {}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        for (SymbolButtonWidget tabButton : this.tabButtons) {
            tabButton.render(context, mouseX, mouseY, delta);
        }
        int dx = this.x + selectedTab * SymbolButtonWidget.GRID_SPCAING;
        if(dx > x) context.drawHorizontalLine(x, dx-1, y + SymbolButtonWidget.SYMBOL_SIZE, 0xFFFFFFFF);
        if(x + width > dx + SymbolButtonWidget.GRID_SPCAING) context.drawHorizontalLine(dx + SymbolButtonWidget.GRID_SPCAING, x + width - 1, y + SymbolButtonWidget.SYMBOL_SIZE, 0xFFFFFFFF);
    }


    @Override
    public ScreenRect getNavigationFocus() {
        return new ScreenRect(new ScreenPos(x, y), width, HEIGHT);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        this.tabButtons.forEach(consumer);
    }

    @Override
    public void setX(int x) {
        this.x = x;
        refreshPositions();
    }

    @Override
    public void setY(int y) {
        this.y = y;
        refreshPositions();
    }

    @Override
    public List<? extends Element> children() {
        return this.tabButtons;
    }

    protected class SwitchTabButton extends SymbolButtonWidget {

        protected final int index;

        public SwitchTabButton(int x, int y, int index, SymbolTab tab) {
            super(x, y, tab.getIcon());
            this.index = index;
            this.setTooltip(Tooltip.of(tab.getTooltipText()));
        }

        @Override
        protected boolean shouldDrawOutline() {
            return selectedTab == index;
        }

        @Override
        protected void drawOutline(DrawContext context) {
            context.drawVerticalLine(getX()-1, y, y + GRID_SPCAING, 0xFFFFFFFF);
            context.drawHorizontalLine(getX()-1, getX() + SYMBOL_SIZE, y, 0xFFFFFFFF);
            context.drawVerticalLine(getX() + SYMBOL_SIZE, y, y + SYMBOL_SIZE, 0xFFFFFFFF);
        }

        @Override
        public boolean onClick(int button) {
            setTab(index);
            return true;
        }
    }

}
