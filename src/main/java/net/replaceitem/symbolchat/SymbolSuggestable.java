package net.replaceitem.symbolchat;

import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CursorMovement;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.mixin.EditBoxAccessor;
import net.replaceitem.symbolchat.mixin.widget.TextFieldWidgetAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface SymbolSuggestable {
    ScreenPos getCursorPosition();
    @Nullable
    String getSuggestionTerm();
    void replaceSuggestion(String symbol);
    
    default boolean disabled() {
        return false;
    }
    
    interface TextFieldWidgetSymbolSuggestable extends SymbolSuggestable {
        default ScreenPos getCursorPosition() {
            TextFieldWidget chatField = getTextField();
            int x = chatField.getX() + chatField.getCharacterX(chatField.getCursor())-chatField.getCharacterX(((TextFieldWidgetAccessor) chatField).getFirstCharacterIndex());
            x = MathHelper.clamp(x, 0, chatField.getCharacterX(0) + chatField.getInnerWidth());
            return new ScreenPos(x, chatField.getY());
        }

        default String getSuggestionTerm() {
            if(disabled()) return null;
            TextFieldWidget chatField = getTextField();
            if(((TextFieldWidgetAccessor) chatField).getSelectionStart() != ((TextFieldWidgetAccessor) chatField).getSelectionEnd()) return null;
            MatchResult suggestionArea = getSuggestionArea(chatField);
            if(suggestionArea == null) return null;
            return getSuggestionSearch(suggestionArea);
        }
        
        default void replaceSuggestion(String symbol) {
            TextFieldWidget textFieldWidget = this.getTextField();
            MatchResult suggestionArea = getSuggestionArea(textFieldWidget);
            if(suggestionArea == null) return;
            textFieldWidget.setSelectionStart(suggestionArea.start());
            textFieldWidget.setSelectionEnd(suggestionArea.end());

            textFieldWidget.write(symbol);
        }

        @Nullable
        static MatchResult getSuggestionArea(TextFieldWidget textFieldWidget) {
            return SymbolSuggestable.getSuggestionArea(textFieldWidget.getText(), textFieldWidget.getCursor());
        }

        TextFieldWidget getTextField();
    }

    interface SelectionManagerSymbolSuggestable extends SymbolSuggestable {
        default String getSuggestionTerm() {
            if(disabled()) return null;
            if(this.getSelectionManager().isSelecting()) return null;
            int cursor = this.getSelectionManager().getSelectionStart();
            String string = this.getText();
            MatchResult suggestionArea = SymbolSuggestable.getSuggestionArea(string, cursor);
            if(suggestionArea == null) return null;
            return getSuggestionSearch(suggestionArea);
        }

        default void replaceSuggestion(String symbol) {
            if(this.getSelectionManager().isSelecting()) return;
            int cursor = this.getSelectionManager().getSelectionStart();
            String string = this.getText();
            MatchResult suggestionArea = SymbolSuggestable.getSuggestionArea(string, cursor);
            if(suggestionArea == null) return;
            
            this.getSelectionManager().setSelection(suggestionArea.start(), suggestionArea.end());
            this.getSelectionManager().insert(symbol);
        }

        String getText();
        SelectionManager getSelectionManager();
    }

    interface EditBoxSymbolSuggestable extends SymbolSuggestable {
        default String getSuggestionTerm() {
            if(disabled()) return null;
            if(this.getEditBox().hasSelection()) return null;
            int cursor = this.getEditBox().getCursor();
            String string = this.getEditBox().getText();
            MatchResult suggestionArea = SymbolSuggestable.getSuggestionArea(string, cursor);
            if(suggestionArea == null) return null;
            return getSuggestionSearch(suggestionArea);
        }

        default void replaceSuggestion(String symbol) {
            if(this.getEditBox().hasSelection()) return;
            int cursor = this.getEditBox().getCursor();
            String string = this.getEditBox().getText();
            MatchResult suggestionArea = SymbolSuggestable.getSuggestionArea(string, cursor);
            if(suggestionArea == null) return;

            this.getEditBox().moveCursor(CursorMovement.ABSOLUTE, suggestionArea.start());
            ((EditBoxAccessor) this.getEditBox()).setSelectionEnd(suggestionArea.end());
            this.getEditBox().replaceSelection(symbol);
        }

        EditBox getEditBox();
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
