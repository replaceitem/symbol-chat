package symbolchat.symbolchat.widget;

import symbolchat.symbolchat.FontProcessor;
import symbolchat.symbolchat.SymbolChat;

import java.util.List;

public class FontSelectionDropDownWidget extends DropDownWidget<FontProcessor> {
    public FontSelectionDropDownWidget(int x, int y, int width, int height, List<FontProcessor> elementList, int defaultSelection) {
        super(x, y, width, height, elementList, defaultSelection);
    }


    @Override
    public void onSelection(int index, FontProcessor element) {
        SymbolChat.selectedFont = index;
    }
}
