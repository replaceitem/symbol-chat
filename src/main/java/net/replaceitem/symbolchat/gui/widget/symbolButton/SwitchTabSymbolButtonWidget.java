package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.tooltip.Tooltip;
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
    public boolean onClick(int button) {
        symbolSelectionPanel.setCurrentTab(index);
        return true;
    }

    @Override
    public boolean isSelected() {
        return super.isSelected() || symbolSelectionPanel.selectedTab == index;
    }
}
