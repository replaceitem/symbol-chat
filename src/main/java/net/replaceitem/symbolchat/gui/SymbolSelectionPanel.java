package net.replaceitem.symbolchat.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Pair;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolStorage;
import net.replaceitem.symbolchat.gui.widget.SymbolSearchBar;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SwitchTabSymbolButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SymbolSelectionPanel extends AbstractParentElement implements Drawable, Selectable {
    private final List<Element> children;
    
    protected List<Pair<SymbolTab, SymbolButtonWidget>> tabs;
    protected SearchTab searchTab;
    protected TextFieldWidget searchBar;

    protected int x,y;
    public static final int WIDTH, HEIGHT;
    public static final int SEARCH_BAR_HEIGHT = SymbolButtonWidget.SYMBOL_SIZE + 2;

    public boolean visible;
    public int selectedTab;

    protected Consumer<String> symbolInsertable;

    static {
        WIDTH = SymbolTab.width;
        HEIGHT = SEARCH_BAR_HEIGHT + SymbolTab.height + (SymbolButtonWidget.SYMBOL_SIZE + 2);
    }

    public SymbolSelectionPanel(Consumer<String> symbolConsumer, int x, int y) {
        this.tabs = new ArrayList<>();
        this.children = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.visible = false;
        this.selectedTab = -1;
        this.symbolInsertable = symbolConsumer;

        this.searchBar = new SymbolSearchBar(this.x + 3 + SymbolButtonWidget.SYMBOL_SIZE, this.y + 1 + 2, WIDTH - 2 - SymbolButtonWidget.SYMBOL_SIZE - 10, SymbolButtonWidget.SYMBOL_SIZE - 2) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                setCurrentTab(-1);
            }
        };
        this.searchBar.setChangedListener(s -> getCurrentTab().arrangeButtons());
        this.children.add(searchBar);

        int i;
        for(i = 0; i < SymbolStorage.categories.size(); i++) {
            addTab(SymbolStorage.categories.get(i), i);
        }
        
        addTab(SymbolStorage.kaomojis, i++);
        addTab(SymbolStorage.customSymbols, i);

        searchTab = new SearchTab(symbolConsumer, SymbolStorage.all, this, this.x, this.y + SEARCH_BAR_HEIGHT);
        SwitchTabSymbolButtonWidget searchButtonWidget = new SwitchTabSymbolButtonWidget(this.x+1, this.y+1, -1, SymbolStorage.all, this);
        tabs.add(new Pair<>(searchTab,searchButtonWidget));
        this.children.add(searchButtonWidget);
        this.children.add(searchTab);
    }
    
    private void addTab(SymbolCategory category, int index) {
        SymbolTab tab = SymbolTab.fromCategory(symbolInsertable, category, this, this.x, this.y + SEARCH_BAR_HEIGHT);
        int buttonX = this.x+1+((SymbolButtonWidget.GRID_SPCAING)*index);
        int buttonY = this.y + HEIGHT - SymbolButtonWidget.GRID_SPCAING;
        SwitchTabSymbolButtonWidget buttonWidget = new SwitchTabSymbolButtonWidget(buttonX, buttonY, index, category, this);
        tabs.add(new Pair<>(tab,buttonWidget));

        this.children.add(buttonWidget);
        this.children.add(tab);
    }

    protected String getSearchTerm() {
        return searchBar.getText();
    }

    public SymbolTab getSymbolTab(int index) {
        if(index == -1) return searchTab;
        return tabs.get(index).getLeft();
    }

    public SymbolTab getCurrentTab() {
        return this.getSymbolTab(selectedTab);
    }

    public void setCurrentTab(int index) {
        this.selectedTab = index;
        getCurrentTab().arrangeButtons();
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if(!this.visible) return;
        RenderSystem.disableDepthTest();
        drawContext.fill(this.x, this.y, this.x + WIDTH, this.y + HEIGHT, SymbolChat.config.getHudColor());
        drawContext.fill(this.x, this.y + HEIGHT - 2 - SymbolButtonWidget.SYMBOL_SIZE, this.x + WIDTH, this.y + HEIGHT, SymbolChat.config.getHudColor());
        this.searchBar.render(drawContext, mouseX, mouseY, delta);
        this.getCurrentTab().render(drawContext,mouseX,mouseY,delta);
        for(Pair<SymbolTab,SymbolButtonWidget> tab : tabs) {
            tab.getRight().render(drawContext, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.isMouseOver(mouseX, mouseY)) {
            super.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        this.setFocused(null);
        this.searchBar.setFocused(false);
        return false;
    }

    @Override
    public List<? extends Element> children() {
        return this.children;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(!visible) return false;
        return this.getCurrentTab().mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + WIDTH && mouseY < this.y + HEIGHT;
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        if(focused != searchBar) return;
        super.setFocused(focused);
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
