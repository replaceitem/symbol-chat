package net.replaceitem.symbolchat.extensions;

import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.replaceitem.symbolchat.resource.FontProcessor;
import org.jetbrains.annotations.NotNull;

public interface ScreenAccess {
    void addSymbolChatComponents();
    @NotNull FontProcessor getFontProcessor();
    void refreshSuggestions();
    boolean handleSuggestorKeyPressed(KeyInput input);
    boolean handlePanelKeyPressed(KeyInput input);
    boolean handlePanelCharTyped(CharInput input);
    
    default boolean handleKeyPressed(KeyInput input) {
        if(handleSuggestorKeyPressed(input)) return true;
        return handlePanelKeyPressed(input);
    }
}
