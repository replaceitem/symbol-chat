package net.replaceitem.symbolchat;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.mixin.TextFieldWidgetAccessor;
import org.joml.Vector2i;
import oshi.util.tuples.Pair;

public interface SymbolSuggestable {
    Vector2i getCursorPosition();
    String getSuggestionTerm();
    void replaceSuggestion(String symbol);
    
    default boolean disabled() {
        return false;
    }
    
    interface TextFieldWidgetSymbolSuggestable extends SymbolSuggestable {
        default Vector2i getCursorPosition() {
            TextFieldWidget chatField = getTextField();
            int x = chatField.getX() + chatField.getCharacterX(chatField.getCursor())-chatField.getCharacterX(((TextFieldWidgetAccessor) chatField).getFirstCharacterIndex());
            x = MathHelper.clamp(x, 0, chatField.getCharacterX(0) + chatField.getInnerWidth());
            return new Vector2i(x, chatField.getY());
        }

        default String getSuggestionTerm() {
            if(disabled()) return null;
            TextFieldWidget chatField = getTextField();
            if(((TextFieldWidgetAccessor) chatField).getSelectionStart() != ((TextFieldWidgetAccessor) chatField).getSelectionEnd()) return null;
            Pair<Integer, Integer> suggestionArea = getSuggestionArea(chatField);
            if(suggestionArea == null) return null;
            return chatField.getText().substring(suggestionArea.getA()+1, suggestionArea.getB()).replace('_',' ');
        }
        
        default void replaceSuggestion(String symbol) {
            TextFieldWidget textFieldWidget = this.getTextField();
            Pair<Integer, Integer> suggestionArea = SymbolSuggestable.getSuggestionArea(textFieldWidget);

            textFieldWidget.setSelectionStart(suggestionArea.getA());
            textFieldWidget.setSelectionEnd(suggestionArea.getB());

            textFieldWidget.write(symbol);
        }

        TextFieldWidget getTextField();
    }

    interface SelectionManagerSymbolSuggestable extends SymbolSuggestable {
        default String getSuggestionTerm() {
            if(disabled()) return null;
            if(this.getSelectionManager().isSelecting()) return null;
            int cursor = this.getSelectionManager().getSelectionStart();
            String string = this.getText();
            Pair<Integer, Integer> suggestionArea = SymbolSuggestable.getSuggestionArea(string, cursor);
            if(suggestionArea == null) return null;
            return string.substring(suggestionArea.getA()+1, suggestionArea.getB()).replace('_',' ');
        }

        default void replaceSuggestion(String symbol) {
            if(this.getSelectionManager().isSelecting()) return;
            int cursor = this.getSelectionManager().getSelectionStart();
            String string = this.getText();
            Pair<Integer, Integer> suggestionArea = SymbolSuggestable.getSuggestionArea(string, cursor);
            if(suggestionArea == null) return;
            
            this.getSelectionManager().setSelection(suggestionArea.getA(), suggestionArea.getB());
            this.getSelectionManager().insert(symbol);
        }

        String getText();
        SelectionManager getSelectionManager();
    }

    static Pair<Integer, Integer> getSuggestionArea(String text, int cursor) {
        int colonIndex = text.lastIndexOf(':', cursor-1);
        if(colonIndex == -1 || (colonIndex > 0 && text.charAt(colonIndex-1) != ' ')) return null;
        int spaceIndex = text.lastIndexOf(' ', cursor-1);
        if(spaceIndex >= colonIndex) return null;
        return new Pair<>(colonIndex, cursor);
    }

    static Pair<Integer, Integer> getSuggestionArea(TextFieldWidget textFieldWidget) {
        return getSuggestionArea(textFieldWidget.getText(), textFieldWidget.getCursor());
    }
}
