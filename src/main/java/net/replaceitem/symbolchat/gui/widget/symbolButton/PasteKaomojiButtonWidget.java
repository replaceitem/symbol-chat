package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.gui.screen.Screen;
import net.replaceitem.symbolchat.gui.SymbolTab;

public class PasteKaomojiButtonWidget extends PasteSymbolButtonWidget {

    public PasteKaomojiButtonWidget(Screen screen, int x, int y, SymbolTab symbolTab, String symbol) {
        super(screen, x, y, symbolTab, symbol);
        this.width = SymbolTab.width - 2;
    }
}
