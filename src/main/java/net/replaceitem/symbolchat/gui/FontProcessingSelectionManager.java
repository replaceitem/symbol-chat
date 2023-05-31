package net.replaceitem.symbolchat.gui;

import net.minecraft.client.util.SelectionManager;
import net.replaceitem.symbolchat.font.FontProcessor;
import net.replaceitem.symbolchat.font.Fonts;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FontProcessingSelectionManager extends SelectionManager {
    public FontProcessingSelectionManager(Supplier<String> stringGetter, Consumer<String> stringSetter, Supplier<String> clipboardGetter, Consumer<String> clipboardSetter, Predicate<String> stringFilter) {
        super(stringGetter, stringSetter, clipboardGetter, clipboardSetter, stringFilter);
    }

    @Override
    protected void insert(String string, String insertion) {
        FontProcessor fontProcessor = FontProcessor.getCurrentScreenFontProcessor();
        insertion = fontProcessor.convertString(insertion);
        super.insert(string, insertion);
        if(fontProcessor == Fonts.INVERSE) {
            int pos = this.getSelectionStart()-insertion.length();
            this.setSelection(pos, pos);
        }
    }
}
