package net.replaceitem.symbolchat.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.gui.container.ContainerWidgetImpl;
import net.replaceitem.symbolchat.gui.widget.SymbolTabWidget;
import net.replaceitem.symbolchat.gui.widget.TabSelectionWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import net.replaceitem.symbolchat.resource.SymbolTab;

import java.util.ArrayList;
import java.util.List;

public class SymbolSelectionPanel extends ContainerWidgetImpl {
    private final TabSelectionWidget tabSelectionWidget;
    private final List<SymbolTabWidget> tabs = new ArrayList<>();

    private static final int MIN_COLUMNS = 8;

    protected SymbolInsertable symbolInsertable;
    
    public static int getWidthForTabs(int tabCount) {
        int columns = Math.max(tabCount, MIN_COLUMNS);
        return columns * (SymbolButtonWidget.GRID_SPCAING) + 1;
    }

    public SymbolSelectionPanel(int x, int y, int height, SymbolInsertable symbolInsertable) {
        super(x, y, 0, height);
        int columns = Math.max(SymbolChat.symbolManager.getTabs().size(), MIN_COLUMNS);
        this.width = getWidthForTabs(SymbolChat.symbolManager.getTabs().size());
        this.visible = SymbolChat.config.getKeepPanelOpen() && SymbolChat.isPanelOpen;
        this.symbolInsertable = symbolInsertable;
        this.tabSelectionWidget = new TabSelectionWidget(this.getX(), this.getY() + 1, width) {
            @Override
            protected void onTabSwitched(int previousIndex, int newIndex) {
                tabs.forEach(symbolTabWidget -> symbolTabWidget.visible = false);
                getSymbolTab(newIndex).visible = true;
                getCurrentTab().refresh();
            }
        };
        this.addChildren(tabSelectionWidget);

        for (SymbolTab tab : SymbolChat.symbolManager.getTabs()) {
            addTab(tab, columns);
        }


        tabs.forEach(symbolTabWidget -> symbolTabWidget.visible = false);
        getCurrentTab().visible = true;
        tabSelectionWidget.refreshPositions();
    }

    private void addTab(SymbolTab tab, int columns) {
        int tabY = this.getY() + tabSelectionWidget.getHeight() + 2;
        int tabHeight = this.height - (tabSelectionWidget.getHeight() + 2) - 1;
        SymbolTabWidget tabWidget = new SymbolTabWidget(this.getX() + 1, tabY, width, tabHeight, tab, this, columns);
        this.tabs.add(tabWidget);
        this.tabSelectionWidget.addTab(tab);
        this.addChildren(tabWidget);
    }

    public SymbolTabWidget getSymbolTab(int index) {
        return this.tabs.get(index);
    }

    public SymbolTabWidget getCurrentTab() {
        return this.getSymbolTab(this.tabSelectionWidget.getSelectedTab());
    }

    public SymbolInsertable getSymbolInsertable() {
        return symbolInsertable;
    }

    public boolean isVisible() {
        return visible;
    }
    
    public void toggleVisible() {
        this.visible = !this.visible;
        if(SymbolChat.config.getKeepPanelOpen()) SymbolChat.isPanelOpen = this.visible;
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0.0, 0.0, 350.0);
        RenderSystem.disableDepthTest();
        drawContext.fill(this.getX(), this.getY() + SymbolButtonWidget.GRID_SPCAING + 1, this.getX() + width, this.getY() + height, SymbolChat.config.getHudColor());
        super.renderWidget(drawContext, mouseX, mouseY, delta);
        drawContext.getMatrices().pop();
    }
}
