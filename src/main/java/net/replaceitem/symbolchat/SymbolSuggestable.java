package net.replaceitem.symbolchat;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.util.Mth;
import net.replaceitem.symbolchat.mixin.MultilineTextFieldAccessor;
import net.replaceitem.symbolchat.mixin.widget.EditBoxAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface SymbolSuggestable {
    ScreenPosition getCursorPosition();
    @Nullable
    String getSuggestionTerm();
    void replaceSuggestion(String symbol);
    
    default boolean suggestionsDisabled() {
        return false;
    }
    
    interface TextFieldWidgetSymbolSuggestable extends SymbolSuggestable {
        default ScreenPosition getCursorPosition() {
            EditBox chatField = getTextField();
            int x = chatField.getX() + chatField.getScreenX(chatField.getCursorPosition())-chatField.getScreenX(((EditBoxAccessor) chatField).getDisplayPos());
            x = Mth.clamp(x, 0, chatField.getScreenX(0) + chatField.getInnerWidth());
            return new ScreenPosition(x, chatField.getY());
        }

        default String getSuggestionTerm() {
            if(suggestionsDisabled()) return null;
            EditBox chatField = getTextField();
            if(((EditBoxAccessor) chatField).getCursorPos() != ((EditBoxAccessor) chatField).getHighlightPos()) return null;
            MatchResult suggestionArea = getSuggestionArea(chatField);
            if(suggestionArea == null) return null;
            return getSuggestionSearch(suggestionArea);
        }
        
        default void replaceSuggestion(String symbol) {
            EditBox textFieldWidget = this.getTextField();
            MatchResult suggestionArea = getSuggestionArea(textFieldWidget);
            if(suggestionArea == null) return;
            textFieldWidget.setCursorPosition(suggestionArea.start());
            textFieldWidget.setHighlightPos(suggestionArea.end());

            textFieldWidget.insertText(symbol);
        }

        @Nullable
        static MatchResult getSuggestionArea(EditBox textFieldWidget) {
            return SymbolSuggestable.getSuggestionArea(textFieldWidget.getValue(), textFieldWidget.getCursorPosition());
        }

        EditBox getTextField();
    }

    interface SelectionManagerSymbolSuggestable extends SymbolSuggestable {
        default String getSuggestionTerm() {
            if(suggestionsDisabled()) return null;
            if(this.getTextFieldHelper().isSelecting()) return null;
            int cursor = this.getTextFieldHelper().getCursorPos();
            String string = this.getText();
            MatchResult suggestionArea = SymbolSuggestable.getSuggestionArea(string, cursor);
            if(suggestionArea == null) return null;
            return getSuggestionSearch(suggestionArea);
        }

        default void replaceSuggestion(String symbol) {
            if(this.getTextFieldHelper().isSelecting()) return;
            int cursor = this.getTextFieldHelper().getCursorPos();
            String string = this.getText();
            MatchResult suggestionArea = SymbolSuggestable.getSuggestionArea(string, cursor);
            if(suggestionArea == null) return;
            
            this.getTextFieldHelper().setSelectionRange(suggestionArea.start(), suggestionArea.end());
            this.getTextFieldHelper().insertText(symbol);
        }

        String getText();
        TextFieldHelper getTextFieldHelper();
    }

    interface EditBoxSymbolSuggestable extends SymbolSuggestable {
        default String getSuggestionTerm() {
            if(suggestionsDisabled()) return null;
            if(this.getMultilineTextField().hasSelection()) return null;
            int cursor = this.getMultilineTextField().cursor();
            String string = this.getMultilineTextField().value();
            MatchResult suggestionArea = SymbolSuggestable.getSuggestionArea(string, cursor);
            if(suggestionArea == null) return null;
            return getSuggestionSearch(suggestionArea);
        }

        default void replaceSuggestion(String symbol) {
            if(this.getMultilineTextField().hasSelection()) return;
            int cursor = this.getMultilineTextField().cursor();
            String string = this.getMultilineTextField().value();
            MatchResult suggestionArea = SymbolSuggestable.getSuggestionArea(string, cursor);
            if(suggestionArea == null) return;

            this.getMultilineTextField().seekCursor(Whence.ABSOLUTE, suggestionArea.start());
            ((MultilineTextFieldAccessor) this.getMultilineTextField()).setSelectCursor(suggestionArea.end());
            this.getMultilineTextField().insertText(symbol);
        }

        MultilineTextField getMultilineTextField();
    }

    Pattern SUGGESTION_PATTERN = Pattern.compile("(?<=\\s|^):(\\S+)$", Pattern.MULTILINE);
    Pattern SEPERATION_PATTERN = Pattern.compile("[_\\-,.+ ]");

    @Nullable
    static MatchResult getSuggestionArea(String text, int cursor) {
        Matcher matcher = SUGGESTION_PATTERN.matcher(text).region(0, cursor);
        if(!matcher.find()) return null;
        return matcher.toMatchResult();
    }

    static String getSuggestionSearch(MatchResult result) {
        return SEPERATION_PATTERN.matcher(result.group(1)).replaceAll(" ");
    }
}
