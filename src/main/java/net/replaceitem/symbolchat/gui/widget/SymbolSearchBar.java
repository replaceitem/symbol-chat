package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SymbolSearchBar extends TextFieldWidget {

    public SymbolSearchBar(int x, int y, int width, int height) {
        super(MinecraftClient.getInstance().textRenderer, x, y, width, height, Text.empty());
        this.setDrawsBackground(false);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        int lineY = this.y + this.height - 1;
        fill(matrices, this.x, lineY, this.x + this.width - 1, lineY+1, this.isActive() || this.isHovered() ? 0x99FFFFFF : 0x99A0A0A0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean b = super.mouseClicked(mouseX, mouseY, button);
        this.onClick(mouseX, mouseY);
        return b;
    }
}
