package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class SymbolSearchBar extends TextFieldWidget {

    public static final Text HINT_TEXT = Text.translatable("symbolchat.symbol_panel.search");

    public SymbolSearchBar(int x, int y, int width, int height) {
        super(MinecraftClient.getInstance().textRenderer, x, y, width, height, Text.empty());
        this.setDrawsBackground(false);
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if(this.getText().isEmpty()) {
            drawContext.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, HINT_TEXT, this.getX(), this.getY(), 0xa0a0a0a0);
        }
        super.renderWidget(drawContext, mouseX, mouseY, delta);
        int lineY = this.getY() + this.height - 1;
        drawContext.fill(this.getX(), lineY, this.getX() + this.width - 1, lineY+1, this.isActive() || this.isHovered() ? 0x99FFFFFF : 0x99A0A0A0);
    }
}
