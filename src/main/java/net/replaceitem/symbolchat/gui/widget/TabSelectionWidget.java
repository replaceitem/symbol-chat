package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.replaceitem.symbolchat.*;
import net.replaceitem.symbolchat.gui.container.NonScrollableContainerWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.*;
import net.replaceitem.symbolchat.resource.*;

public class TabSelectionWidget extends NonScrollableContainerWidget {

    private static final int HEIGHT = SymbolButtonWidget.GRID_SPCAING;
    private int selectedTab;

    public TabSelectionWidget(int x, int y, int width) {
        super(x, y, width, HEIGHT);
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public void addTab(SymbolTab tab) {
        int index = this.children().size();
        MutableComponent narration = tab.getTooltipText().copy();
        ResourceLocation icon = tab.getIcon();
        String textIcon = tab.getTextIcon();
        boolean hasLiteralIcon = textIcon != null;
        FlatIconButtonWidget switchTabWidget = new FlatIconButtonWidget(
                SymbolButtonWidget.SYMBOL_SIZE, SymbolButtonWidget.SYMBOL_SIZE,
                hasLiteralIcon ? Component.literal(textIcon) : null,
                SymbolButtonWidget.SYMBOL_SIZE, SymbolButtonWidget.SYMBOL_SIZE,
                hasLiteralIcon ? null : new WidgetSprites(icon),
                button -> setTab(index),
                tab.getTooltipText(),
                textSupplier -> narration
        );
        this.children().add(switchTabWidget);
    }

    public void refreshPositions() {
        GridLayout gridWidget = new GridLayout(this.getX()+1, this.getY());
        gridWidget.columnSpacing(1);
        GridLayout.RowHelper adder = gridWidget.createRowHelper(Integer.MAX_VALUE);
        this.children().forEach(adder::addChild);
        gridWidget.arrangeElements();
        this.setTab(this.selectedTab);
    }

    public void setTab(int tab) {
        for (LayoutElement child : this.children()) {
            child.setY(getY());
        }
        int previousTab = this.getSelectedTab();
        this.selectedTab = Mth.clamp(tab, 0, this.children().size()-1);
        if(selectedTab != -1) {
            SymbolChat.selectedTab = this.selectedTab;
            children().get(selectedTab).setY(getY()+1);
        }
        if(previousTab != this.selectedTab) this.onTabSwitched(previousTab, selectedTab);
    }

    protected void onTabSwitched(int previousIndex, int newIndex) {}

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        int dx = this.getX() + selectedTab * SymbolButtonWidget.GRID_SPCAING;
        int color = 0xFFFFFFFF;
        if(dx > getX()) context.hLine(getX(), dx-1, getY() + SymbolButtonWidget.SYMBOL_SIZE, color);
        if(getRight() > dx + SymbolButtonWidget.GRID_SPCAING) context.hLine(dx + SymbolButtonWidget.GRID_SPCAING, getRight() - 1, getY() + SymbolButtonWidget.SYMBOL_SIZE, color);
        if(selectedTab != -1) {
            context.vLine(dx, getY(), getBottom(), color);
            context.hLine(dx, dx + SymbolButtonWidget.GRID_SPCAING, getY(), color);
            context.vLine(dx + SymbolButtonWidget.GRID_SPCAING, getY(), getY() + SymbolButtonWidget.SYMBOL_SIZE, color);
        }
    }
//
//    @Override
//    public void setX(int x) {
//        this.x = x;
//        refreshPositions();
//    }
//
//    @Override
//    public void setY(int y) {
//        this.y = y;
//        refreshPositions();
//    }

}
