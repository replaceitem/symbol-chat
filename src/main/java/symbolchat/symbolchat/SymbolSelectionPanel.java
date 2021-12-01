package symbolchat.symbolchat;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import symbolchat.symbolchat.widget.symbolButton.SwitchTabSymbolButtonWidget;
import symbolchat.symbolchat.widget.symbolButton.SymbolButtonWidget;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.DrawableHelper.fill;

public class SymbolSelectionPanel implements Element, Drawable {
    protected List<Pair<SymbolTab,SymbolButtonWidget>> tabs;
    protected TextFieldWidget textFieldWidget;

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
        tabs = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.visible = false;
        this.selectedTab = 0;
        this.screen = screen;

        int buttonY = this.y+height-1- SymbolButtonWidget.symbolSize;

        for(int i = 0; i < SymbolStorage.symbolLists.size(); i++) {
            SymbolTab tab = new SymbolTab(screen, SymbolStorage.symbolLists.get(i),this.x,this.y, this);
            int buttonX = this.x+1+((SymbolButtonWidget.symbolSize+1)*i);
            SymbolButtonWidget buttonWidget = new SwitchTabSymbolButtonWidget(screen, buttonX, buttonY, i, this);
            tabs.add(new Pair<>(tab,buttonWidget));
        }
    }

    public SymbolButtonWidget getSymbolButtonWidget(int index) {
        return tabs.get(index).getRight();
    }

    public SymbolTab getSymbolTab(int index) {
        return tabs.get(index).getLeft();
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!this.visible) return;
        fill(matrices, this.x, this.y, this.x + width, this.y + height, Config.hud_color);
        fill(matrices, this.x, this.y + height - 2 - SymbolButtonWidget.symbolSize, this.x + width, this.y + height, Config.hud_color);
        for(Pair<SymbolTab,SymbolButtonWidget> tab : tabs) {
            tab.getRight().render(matrices,mouseX,mouseY,delta);
        }
        for(Pair<SymbolTab,SymbolButtonWidget> tab : tabs) {
            tab.getRight().renderTooltip(matrices,mouseX,mouseY);
        }
        getSymbolTab(selectedTab).render(matrices,mouseX,mouseY,delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!visible) return false;
        for(Pair<SymbolTab,SymbolButtonWidget> tab : tabs) {
            if(tab.getRight().mouseClicked(mouseX,mouseY,button)) return true;
        }
        if(getSymbolTab(selectedTab).mouseClicked(mouseX,mouseY,button)) return true;
        return false;
    }

    public void onSymbolPasted(String symbol) {
        if(this.screen instanceof SymbolInsertable) {
            ((SymbolInsertable) this.screen).insertSymbol(symbol);
        }
    }
}
