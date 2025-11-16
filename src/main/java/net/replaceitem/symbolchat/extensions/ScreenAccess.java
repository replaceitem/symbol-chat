package net.replaceitem.symbolchat.extensions;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.replaceitem.symbolchat.resource.FontProcessor;
import org.jetbrains.annotations.NotNull;

public interface ScreenAccess {
    void addSymbolChatComponents();
    @NotNull FontProcessor getFontProcessor();
    void refreshSuggestions();
    boolean handleSuggestorKeyPressed(KeyEvent input);
    boolean handlePanelKeyPressed(KeyEvent input);
    boolean handlePanelCharTyped(CharacterEvent input);
    
    default boolean handleKeyPressed(KeyEvent input) {
        if(handleSuggestorKeyPressed(input)) return true;
        return handlePanelKeyPressed(input);
    }
}
