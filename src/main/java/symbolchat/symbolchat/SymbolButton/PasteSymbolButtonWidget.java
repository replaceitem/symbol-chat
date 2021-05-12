package symbolchat.symbolchat.SymbolButton;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class PasteSymbolButtonWidget extends SymbolButtonWidget {

    protected TextFieldWidget textFieldWidget;

    protected String symbol;

    public PasteSymbolButtonWidget(Screen screen, int x, int y, TextFieldWidget textFieldWidget, String symbol) {
        super(screen, x, y, symbol);
        this.textFieldWidget = textFieldWidget;
        this.symbol = symbol;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.textFieldWidget.write(this.symbol);
    }
}
