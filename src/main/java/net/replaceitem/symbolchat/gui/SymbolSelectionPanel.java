package net.replaceitem.symbolchat.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolStorage;
import net.replaceitem.symbolchat.gui.widget.TabSelectionWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SymbolSelectionPanel extends AbstractParentElement implements Drawable, Selectable {
    private final TabSelectionWidget tabSelectionWidget;
    private final List<SymbolTab> tabs;

    protected int x,y;
    public final int height;

    public static int WIDTH = SymbolTab.COLUMNS * (SymbolButtonWidget.GRID_SPCAING) + 1;

    public boolean visible;

    protected Consumer<String> symbolInsertable;

    public SymbolSelectionPanel(Consumer<String> symbolConsumer, int x, int y, int height) {
        this.height = height;
        this.tabs = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.visible = false;
        this.symbolInsertable = symbolConsumer;
        this.tabSelectionWidget = new TabSelectionWidget(this.x, this.y + 1, WIDTH);

        addTab(SymbolStorage.all);
        SymbolStorage.categories.forEach(this::addTab);
        addTab(SymbolStorage.kaomojis);
        addTab(SymbolStorage.customSymbols);

        tabSelectionWidget.refreshPositions();
    }
    
    private void addTab(SymbolCategory category) {
        int tabY = this.y + tabSelectionWidget.getHeight() + 2;
        int tabHeight = this.height - (tabSelectionWidget.getHeight() + 2) - 1;
        SymbolTab tab = SymbolTab.fromCategory(symbolInsertable, category, this, this.x + 1, tabY, tabHeight);
        this.tabs.add(tab);
        this.tabSelectionWidget.addTab(category);
    }

    public SymbolTab getSymbolTab(int index) {
        return this.tabs.get(index);
    }

    public SymbolTab getCurrentTab() {
        return this.getSymbolTab(this.tabSelectionWidget.getSelectedTab());
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if(!this.visible) return;
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0.0, 0.0, 350.0);
        RenderSystem.disableDepthTest();
        drawContext.fill(this.x, this.y + SymbolButtonWidget.GRID_SPCAING + 1, this.x + WIDTH, this.y + height, SymbolChat.config.getHudColor());
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(!visible) return false;
        return this.getCurrentTab().mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + WIDTH && mouseY < this.y + height;
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
