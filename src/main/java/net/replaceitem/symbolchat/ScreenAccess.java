package net.replaceitem.symbolchat;

import net.replaceitem.symbolchat.font.FontProcessor;

public interface ScreenAccess {
    void addSymbolChatComponents();
    FontProcessor getFontProcessor();
    void refreshSuggestions();
    boolean onKeyPressed(int keyCode, int scanCode, int modifiers);
    boolean onCharTyped(char chr, int modifiers);
}