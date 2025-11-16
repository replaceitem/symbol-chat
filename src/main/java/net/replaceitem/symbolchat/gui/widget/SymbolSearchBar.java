package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class SymbolSearchBar extends EditBox {

    public static final Component HINT_TEXT = Component.translatable("symbolchat.symbol_panel.search");

    public SymbolSearchBar(int x, int y, int width, int height) {
        super(Minecraft.getInstance().font, x, y, width, height, Component.empty());
        this.setBordered(false);
    }

    @Override
    public void renderWidget(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        if(this.getValue().isEmpty()) {
            drawContext.drawString(Minecraft.getInstance().font, HINT_TEXT, this.getX(), this.getY(), 0xa0a0a0a0);
        }
        super.renderWidget(drawContext, mouseX, mouseY, delta);
        int lineY = this.getY() + this.height - 1;
        drawContext.fill(this.getX(), lineY, this.getX() + this.width - 1, lineY+1, this.canConsumeInput() || this.isHovered() ? 0x99FFFFFF : 0x99A0A0A0);
    }
}
