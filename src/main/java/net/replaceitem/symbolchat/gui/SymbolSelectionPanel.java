package net.replaceitem.symbolchat.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.gui.container.NonScrollableContainerWidget;
import net.replaceitem.symbolchat.gui.container.SmoothScrollableContainerWidget;
import net.replaceitem.symbolchat.gui.widget.SymbolTabWidget;
import net.replaceitem.symbolchat.gui.widget.TabSelectionWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import net.replaceitem.symbolchat.resource.SymbolTab;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SymbolSelectionPanel extends NonScrollableContainerWidget {
    private final TabSelectionWidget tabSelectionWidget;
    private final List<SymbolTabWidget> tabs = new ArrayList<>();

    private static final int MIN_COLUMNS = 8;

    protected final SymbolInsertable symbolInsertable;
    
    public static int getWidthForTabs(int tabCount) {
        int columns = Math.max(tabCount, MIN_COLUMNS);
        return columns * (SymbolButtonWidget.GRID_SPCAING) + 1 + SmoothScrollableContainerWidget.ScrollbarStyle.SLIM.getWidth();
    }

    public SymbolSelectionPanel(int x, int y, int height, SymbolInsertable symbolInsertable) {
        super(x, y, 0, height);
        int columns = Math.max(SymbolChat.symbolManager.getTabs().size(), MIN_COLUMNS);
        this.width = getWidthForTabs(SymbolChat.symbolManager.getTabs().size());
        this.visible = SymbolChat.config.keepPanelOpen.get() && SymbolChat.isPanelOpen;
        this.symbolInsertable = symbolInsertable;
        this.tabSelectionWidget = new TabSelectionWidget(this.getX(), this.getY() + 1, width) {
            @Override
            protected void onTabSwitched(int previousIndex, int newIndex) {
                getSymbolTab(newIndex).ifPresent(SymbolSelectionPanel.this::setVisibleTab);
                getCurrentTab().ifPresent(SymbolTabWidget::refresh);
            }
        };
        this.tabSelectionWidget.setTab(SymbolChat.selectedTab);
        this.addChildren(tabSelectionWidget);

        for (SymbolTab tab : SymbolChat.symbolManager.getTabs()) {
            addTab(tab, columns);
        }


        getCurrentTab().ifPresent(this::setVisibleTab);
        tabSelectionWidget.refreshPositions();
    }
    
    private void setVisibleTab(SymbolTabWidget tab) {
        tabs.forEach(symbolTabWidget -> symbolTabWidget.visible = false);
        tab.visible = true;
    }

    private void addTab(SymbolTab tab, int columns) {
        int tabY = this.getY() + tabSelectionWidget.getHeight() + 2;
        int tabHeight = this.height - (tabSelectionWidget.getHeight() + 2) - 1;
        SymbolTabWidget tabWidget = new SymbolTabWidget(this.getX(), tabY, width, tabHeight, tab, this, columns);
        this.tabs.add(tabWidget);
        this.tabSelectionWidget.addTab(tab);
        this.addChildren(tabWidget);
    }

    public Optional<SymbolTabWidget> getSymbolTab(int index) {
        if(index < 0 || index >= tabs.size()) return Optional.empty();
        return Optional.of(this.tabs.get(index));
    }

    public Optional<SymbolTabWidget> getCurrentTab() {
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
        if(SymbolChat.config.keepPanelOpen.get()) SymbolChat.isPanelOpen = this.visible;
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0.0, 0.0, 350.0);
        RenderSystem.disableDepthTest();
        drawContext.fill(this.getX(), this.getY() + SymbolButtonWidget.GRID_SPCAING + 1, this.getX() + width, this.getY() + height, SymbolChat.config.hudColor.get());
        super.renderWidget(drawContext, mouseX, mouseY, delta);
        drawContext.getMatrices().pop();
    }
}
