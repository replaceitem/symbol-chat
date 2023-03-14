package net.replaceitem.symbolchat.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolStorage;
import net.replaceitem.symbolchat.gui.widget.SymbolSearchBar;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SwitchTabSymbolButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;

import java.util.ArrayList;
import java.util.List;

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

    protected SymbolInsertable symbolInsertable;

    static {
        WIDTH = SymbolTab.width;
        HEIGHT = SEARCH_BAR_HEIGHT + SymbolTab.height + (SymbolButtonWidget.SYMBOL_SIZE + 2);
    }

    public SymbolSelectionPanel(SymbolInsertable symbolInsertable, int x, int y) {
        this.tabs = new ArrayList<>();
        this.children = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.visible = false;
        this.selectedTab = -1;
        this.symbolInsertable = symbolInsertable;

        this.searchBar = new SymbolSearchBar(this.x + 2 + SymbolButtonWidget.SYMBOL_SIZE, this.y + 1 + 2, WIDTH - 2 - SymbolButtonWidget.SYMBOL_SIZE - 10, SymbolButtonWidget.SYMBOL_SIZE - 2) {
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

        searchTab = new SearchTab(symbolInsertable, SymbolStorage.all, this, this.x, this.y + SEARCH_BAR_HEIGHT);
        SwitchTabSymbolButtonWidget searchButtonWidget = new SwitchTabSymbolButtonWidget(this.x+1, this.y+1, -1, SymbolStorage.all, this);
        tabs.add(new Pair<>(searchTab,searchButtonWidget));
        this.children.add(searchButtonWidget);
        this.children.add(searchTab);
    }
    
    private void addTab(SymbolCategory category, int index) {
        SymbolTab tab = SymbolTab.fromCategory(symbolInsertable, category, this, this.x, this.y + SEARCH_BAR_HEIGHT);
        int buttonX = this.x+1+((SymbolButtonWidget.SYMBOL_SIZE +1)*index);
        int buttonY = this.y + HEIGHT - 1 - SymbolButtonWidget.SYMBOL_SIZE;
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!this.visible) return;
        RenderSystem.disableDepthTest();
        fill(matrices, this.x, this.y, this.x + WIDTH, this.y + HEIGHT, SymbolChat.config.getHudColor());
        fill(matrices, this.x, this.y + HEIGHT - 2 - SymbolButtonWidget.SYMBOL_SIZE, this.x + WIDTH, this.y + HEIGHT, SymbolChat.config.getHudColor());
        this.searchBar.render(matrices, mouseX, mouseY, delta);
        this.getCurrentTab().render(matrices,mouseX,mouseY,delta);
        for(Pair<SymbolTab,SymbolButtonWidget> tab : tabs) {
            tab.getRight().render(matrices, mouseX, mouseY, delta);
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
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.HINT, "Symbol chat panel");
    }
}
