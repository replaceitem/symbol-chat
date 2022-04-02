package symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import symbolchat.gui.SymbolSelectionPanel;
import symbolchat.SymbolStorage;

public class SwitchTabSymbolButtonWidget extends SymbolButtonWidget {

    protected SymbolSelectionPanel symbolSelectionPanel;
    protected Text tooltip;

    protected int index;

    public SwitchTabSymbolButtonWidget(Screen screen, int x, int y, int index, SymbolSelectionPanel symbolSelectionPanel) {
        super(screen, x, y, SymbolStorage.symbolLists.get(index).icon);
        this.symbolSelectionPanel = symbolSelectionPanel;
        this.index = index;
        this.tooltip = new LiteralText(SymbolStorage.symbolLists.get(index).name);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        symbolSelectionPanel.selectedTab = index;
    }

    @Override
    public boolean isHovered() {
        return super.isHovered() || symbolSelectionPanel.selectedTab == index;
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if(this.isMouseOver(mouseX,mouseY)) screen.renderTooltip(matrices,tooltip,mouseX,mouseY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }
}
