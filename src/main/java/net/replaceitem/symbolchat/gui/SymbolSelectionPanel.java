package net.replaceitem.symbolchat.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.gui.widget.SymbolTabWidget;
import net.replaceitem.symbolchat.gui.widget.TabSelectionWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import net.replaceitem.symbolchat.resource.SymbolTab;

import java.util.ArrayList;
import java.util.List;

public class SymbolSelectionPanel extends AbstractParentElement implements Drawable, Selectable {
    private final TabSelectionWidget tabSelectionWidget;
    private final List<SymbolTabWidget> tabs;

    protected int x,y;
    private final int height;
    private final int width;

    private static final int MIN_COLUMNS = 8;

    private boolean visible;

    protected SymbolInsertable symbolInsertable;
    
    public static int getWidthForTabs(int tabCount) {
        int columns = Math.max(tabCount, MIN_COLUMNS);
        return columns * (SymbolButtonWidget.GRID_SPCAING) + 1;
    }

    public SymbolSelectionPanel(SymbolInsertable symbolInsertable, int x, int y, int height) {
        this.height = height;
        this.tabs = new ArrayList<>();
        this.x = x;
        this.y = y;
        int columns = Math.max(SymbolChat.symbolManager.getTabs().size(), MIN_COLUMNS);
        this.width = getWidthForTabs(SymbolChat.symbolManager.getTabs().size());
        this.visible = SymbolChat.config.getKeepPanelOpen() && SymbolChat.isPanelOpen;
        this.symbolInsertable = symbolInsertable;
        this.tabSelectionWidget = new TabSelectionWidget(this.x, this.y + 1, width) {
            @Override
            protected void onTabSwitched() {
                getCurrentTab().refresh();
            }
        };

        for (SymbolTab tab : SymbolChat.symbolManager.getTabs()) {
            addTab(tab, columns);
        }

        tabSelectionWidget.refreshPositions();
    }

    private void addTab(SymbolTab tab, int columns) {
        int tabY = this.y + tabSelectionWidget.getHeight() + 2;
        int tabHeight = this.height - (tabSelectionWidget.getHeight() + 2) - 1;
        SymbolTabWidget tabWidget = new SymbolTabWidget(tab, this, this.x + 1, tabY, width, tabHeight, columns);
        this.tabs.add(tabWidget);
        this.tabSelectionWidget.addTab(tab);
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
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if(!this.visible) return;
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0.0, 0.0, 350.0);
        RenderSystem.disableDepthTest();
        drawContext.fill(this.x, this.y + SymbolButtonWidget.GRID_SPCAING + 1, this.x + width, this.y + height, SymbolChat.config.getHudColor());
        tabSelectionWidget.render(drawContext, mouseX, mouseY, delta);
        getCurrentTab().render(drawContext, mouseX, mouseY, delta);
        drawContext.getMatrices().pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.isMouseOver(mouseX, mouseY)) {
            super.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        this.setFocused(null);
        return false;
    }

    @Override
    public List<? extends Element> children() {
        return List.of(tabSelectionWidget, getCurrentTab());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(!visible) return false;
        return this.getCurrentTab().mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + width && mouseY < this.y + height;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.HINT, "Symbol chat panel");
    }
}
