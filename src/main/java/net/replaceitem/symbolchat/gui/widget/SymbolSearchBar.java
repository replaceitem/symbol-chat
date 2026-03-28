package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class SymbolSearchBar extends EditBox {

    public static final Component HINT_TEXT = Component.translatable("symbolchat.symbol_panel.search");

    public SymbolSearchBar(int x, int y, int width, int height) {
        super(Minecraft.getInstance().font, x, y, width, height, Component.empty());
        this.setBordered(false);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if(this.getValue().isEmpty()) {
            graphics.text(Minecraft.getInstance().font, HINT_TEXT, this.getX(), this.getY(), 0xa0a0a0a0);
        }
        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
        int lineY = this.getY() + this.height - 1;
        graphics.fill(this.getX(), lineY, this.getX() + this.width - 1, lineY+1, this.canConsumeInput() || this.isHovered() ? 0x99FFFFFF : 0x99A0A0A0);
    }
}
