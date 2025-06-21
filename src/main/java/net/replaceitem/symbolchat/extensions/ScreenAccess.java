package net.replaceitem.symbolchat.extensions;

import net.replaceitem.symbolchat.resource.FontProcessor;
import org.jetbrains.annotations.NotNull;

public interface ScreenAccess {
    void addSymbolChatComponents();
    @NotNull FontProcessor getFontProcessor();
    void refreshSuggestions();
    boolean handleSuggestorKeyPressed(int keyCode, int scanCode, int modifiers);
    boolean handlePanelKeyPressed(int keyCode, int scanCode, int modifiers);
    boolean handlePanelCharTyped(char chr, int modifiers);
    
    default boolean handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if(handleSuggestorKeyPressed(keyCode, scanCode, modifiers)) return true;
        return handlePanelKeyPressed(keyCode, scanCode, modifiers);
    }
}
