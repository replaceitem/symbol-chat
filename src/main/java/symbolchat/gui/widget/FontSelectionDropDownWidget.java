package symbolchat.gui.widget;

import net.minecraft.client.util.math.MatrixStack;
import symbolchat.FontProcessor;
import symbolchat.SymbolChat;

import java.util.List;

public class FontSelectionDropDownWidget extends DropDownWidget<FontProcessor> {
    public FontSelectionDropDownWidget(int x, int y, int width, int height, List<FontProcessor> elementList, int defaultSelection) {
        super(x, y, width, height, elementList, defaultSelection);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(SymbolChat.config.getHideFontButton()) return;
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(SymbolChat.config.getHideFontButton()) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onSelection(int index, FontProcessor element) {
        SymbolChat.selectedFont = index;
    }
}
