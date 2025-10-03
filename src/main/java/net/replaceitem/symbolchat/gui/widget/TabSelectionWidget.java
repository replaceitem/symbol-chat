package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.tooltip.*;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
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
        MutableText narration = tab.getTooltipText().copy();
        Identifier icon = tab.getIcon();
        String textIcon = tab.getTextIcon();
        boolean hasLiteralIcon = textIcon != null;
        FlatIconButtonWidget switchTabWidget = new FlatIconButtonWidget(
                SymbolButtonWidget.SYMBOL_SIZE, SymbolButtonWidget.SYMBOL_SIZE,
                hasLiteralIcon ? Text.literal(textIcon) : null,
                SymbolButtonWidget.SYMBOL_SIZE, SymbolButtonWidget.SYMBOL_SIZE,
                hasLiteralIcon ? null : new ButtonTextures(icon),
                button -> setTab(index),
                tab.getTooltipText(),
                textSupplier -> narration
        );
        this.children().add(switchTabWidget);
    }

    public void refreshPositions() {
        GridWidget gridWidget = new GridWidget(this.getX()+1, this.getY());
        gridWidget.setColumnSpacing(1);
        GridWidget.Adder adder = gridWidget.createAdder(Integer.MAX_VALUE);
        this.children().forEach(adder::add);
        gridWidget.refreshPositions();
        this.setTab(this.selectedTab);
    }

    public void setTab(int tab) {
        for (Widget child : this.children()) {
            child.setY(getY());
        }
        int previousTab = this.getSelectedTab();
        this.selectedTab = MathHelper.clamp(tab, 0, this.children().size()-1);
        if(selectedTab != -1) {
            SymbolChat.selectedTab = this.selectedTab;
            children().get(selectedTab).setY(getY()+1);
        }
        if(previousTab != this.selectedTab) this.onTabSwitched(previousTab, selectedTab);
    }

    protected void onTabSwitched(int previousIndex, int newIndex) {}

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        int dx = this.getX() + selectedTab * SymbolButtonWidget.GRID_SPCAING;
        int color = 0xFFFFFFFF;
        if(dx > getX()) context.drawHorizontalLine(getX(), dx-1, getY() + SymbolButtonWidget.SYMBOL_SIZE, color);
        if(getRight() > dx + SymbolButtonWidget.GRID_SPCAING) context.drawHorizontalLine(dx + SymbolButtonWidget.GRID_SPCAING, getRight() - 1, getY() + SymbolButtonWidget.SYMBOL_SIZE, color);
        if(selectedTab != -1) {
            context.drawVerticalLine(dx, getY(), getBottom(), color);
            context.drawHorizontalLine(dx, dx + SymbolButtonWidget.GRID_SPCAING, getY(), color);
            context.drawVerticalLine(dx + SymbolButtonWidget.GRID_SPCAING, getY(), getY() + SymbolButtonWidget.SYMBOL_SIZE, color);
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
