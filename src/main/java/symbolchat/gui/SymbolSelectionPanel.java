package symbolchat.gui;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import symbolchat.SymbolChat;
import symbolchat.SymbolInsertable;
import symbolchat.SymbolStorage;
import symbolchat.gui.widget.symbolButton.SwitchTabSymbolButtonWidget;
import symbolchat.gui.widget.symbolButton.SymbolButtonWidget;

import java.util.ArrayList;
import java.util.List;

public class SymbolSelectionPanel extends AbstractParentElement implements Drawable, Selectable {
    private final List<Element> children;
    
    protected List<Pair<SymbolTab,SymbolButtonWidget>> tabs;

    protected int x,y;
    public static final int width, height;

    public boolean visible;
    public int selectedTab;

    protected Screen screen;

    static {
        width = SymbolTab.width;
        height = SymbolTab.height + SymbolButtonWidget.symbolSize + 2;
    }

    public SymbolSelectionPanel(Screen screen, int x, int y) {
        this.tabs = new ArrayList<>();
        this.children = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.visible = false;
        this.selectedTab = 0;
        this.screen = screen;

        int buttonY = this.y+height-1- SymbolButtonWidget.symbolSize;
        SymbolStorage.loadCustomList();

        for(int i = 0; i < SymbolStorage.symbolLists.size(); i++) {
            SymbolTab tab = new SymbolTab(screen, SymbolStorage.symbolLists.get(i),this.x,this.y, this);
            int buttonX = this.x+1+((SymbolButtonWidget.symbolSize+1)*i);
            SymbolButtonWidget buttonWidget = new SwitchTabSymbolButtonWidget(screen, buttonX, buttonY, i, this);
            tabs.add(new Pair<>(tab,buttonWidget));
            
            this.children.add(buttonWidget);
            this.children.add(tab);
        }
    }

    public SymbolTab getSymbolTab(int index) {
        return tabs.get(index).getLeft();
    }

    public SymbolTab getCurrentTab() {
        return this.getSymbolTab(selectedTab);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!this.visible) return;
        fill(matrices, this.x, this.y, this.x + width, this.y + height, SymbolChat.config.getHudColor());
        fill(matrices, this.x, this.y + height - 2 - SymbolButtonWidget.symbolSize, this.x + width, this.y + height, SymbolChat.config.getHudColor());
        this.getCurrentTab().render(matrices,mouseX,mouseY,delta);
        for(Pair<SymbolTab,SymbolButtonWidget> tab : tabs) {
            tab.getRight().render(matrices, mouseX, mouseY, delta);
        }
        for(Pair<SymbolTab,SymbolButtonWidget> tab : tabs) {
            tab.getRight().renderTooltip(matrices, mouseX, mouseY);
        }
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
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + width && mouseY < this.y + height;
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
