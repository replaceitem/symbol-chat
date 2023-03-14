package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolCategory;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;

public class SwitchTabSymbolButtonWidget extends SymbolButtonWidget {

    protected SymbolSelectionPanel symbolSelectionPanel;

    protected int index;

    public SwitchTabSymbolButtonWidget(int x, int y, int index, SymbolCategory category, SymbolSelectionPanel symbolSelectionPanel) {
        super(x, y, category.icon);
        this.symbolSelectionPanel = symbolSelectionPanel;
        this.index = index;
        this.setTooltip(Tooltip.of(Text.translatable(category.nameKey)));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        symbolSelectionPanel.setCurrentTab(index);
    }

    @Override
    public boolean isSelected() {
        return symbolSelectionPanel.selectedTab == index;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }
}
