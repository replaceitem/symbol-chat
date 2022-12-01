package net.replaceitem.symbolchat.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.replaceitem.symbolchat.SymbolStorage;
import net.replaceitem.symbolchat.gui.widget.SymbolSearchBar;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SwitchTabSymbolButtonWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SymbolSelectionPanel extends AbstractParentElement implements Drawable, Selectable {
    private final List<Element> children;
    
    protected List<Pair<SymbolTab, SymbolButtonWidget>> tabs;
    protected SearchTab searchTab;
    protected TextFieldWidget searchBar;

    protected int x,y;
    public static final int WIDTH, HEIGHT;
    public static final int SEARCH_BAR_HEIGHT = SymbolButtonWidget.symbolSize + 2;

    public boolean visible;
    public int selectedTab;

    protected Screen screen;

    static {
        WIDTH = SymbolTab.width;
        HEIGHT = SEARCH_BAR_HEIGHT + SymbolTab.height + (SymbolButtonWidget.symbolSize + 2);
    }

    public SymbolSelectionPanel(Screen screen, int x, int y) {
        this.tabs = new ArrayList<>();
        this.children = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.visible = false;
        this.selectedTab = -1;
        this.screen = screen;

        int buttonY = this.y + HEIGHT - 1 - SymbolButtonWidget.symbolSize;
        SymbolStorage.reloadCustomList();
        SymbolStorage.reloadKaomojiList();

        this.searchBar = new SymbolSearchBar(this.x + 2 + SymbolButtonWidget.symbolSize, this.y + 1 + 2, WIDTH - 2 - SymbolButtonWidget.symbolSize - 10, SymbolButtonWidget.symbolSize - 2);
        this.searchBar.setChangedListener(s -> getCurrentTab().rearrangeSymbols());
        this.children.add(searchBar);

        for(int i = 0; i < SymbolStorage.symbolLists.size(); i++) {
            SymbolTab tab = SymbolTab.fromList(screen, SymbolStorage.symbolLists.get(i), this, this.x, this.y + SEARCH_BAR_HEIGHT);
            int buttonX = this.x+1+((SymbolButtonWidget.symbolSize+1)*i);
            SwitchTabSymbolButtonWidget buttonWidget = new SwitchTabSymbolButtonWidget(screen, buttonX, buttonY, i, this);
            tabs.add(new Pair<>(tab,buttonWidget));
            
            this.children.add(buttonWidget);
            this.children.add(tab);
        }

        searchTab = new SearchTab(screen, SymbolStorage.allList, this, this.x, this.y + SEARCH_BAR_HEIGHT);
        SwitchTabSymbolButtonWidget searchButtonWidget = new SwitchTabSymbolButtonWidget(screen, this.x+1, this.y+1, -1, this);
        tabs.add(new Pair<>(searchTab,searchButtonWidget));
        this.children.add(searchButtonWidget);
        this.children.add(searchTab);
    }

    protected String getSearchTerm() {
        return searchBar.getText();
    }

    public int getSearchOrder(PasteSymbolButtonWidget pasteSymbolButtonWidget) {
        String searchTerm = getSearchTerm();
        if(getSearchTerm().isEmpty()) return 0;
        String symbol = pasteSymbolButtonWidget.getSymbol();
        if(symbol.contains(searchTerm)) return 1;
        int searchIndex = symbol.codePoints().map(codePoint -> Character.getName(codePoint).indexOf(searchTerm.toUpperCase(Locale.ROOT))).findFirst().orElse(-1);
        if(searchIndex != -1) return searchIndex + 2;
        return -1;
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
        getCurrentTab().rearrangeSymbols();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!this.visible) return;
        RenderSystem.disableDepthTest();
        fill(matrices, this.x, this.y, this.x + WIDTH, this.y + HEIGHT, SymbolChat.config.getHudColor());
        fill(matrices, this.x, this.y + HEIGHT - 2 - SymbolButtonWidget.symbolSize, this.x + WIDTH, this.y + HEIGHT, SymbolChat.config.getHudColor());
        this.searchBar.render(matrices, mouseX, mouseY, delta);
        this.getCurrentTab().render(matrices,mouseX,mouseY,delta);
        for(Pair<SymbolTab,SymbolButtonWidget> tab : tabs) {
            tab.getRight().render(matrices, mouseX, mouseY, delta);
        }
        for(Pair<SymbolTab,SymbolButtonWidget> tab : tabs) {
            tab.getRight().renderTooltip(matrices, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.isMouseOver(mouseX, mouseY)) {
            super.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        this.setFocused(null);
        this.searchBar.setTextFieldFocused(false);
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

    public void onSymbolPasted(String symbol) {
        if(this.screen instanceof SymbolInsertable symbolInsertable) {
            symbolInsertable.insertSymbol(symbol);
        }
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
