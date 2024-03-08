package net.replaceitem.symbolchat;

import net.minecraft.client.gui.Element;
import net.replaceitem.symbolchat.resource.FontProcessor;
import org.jetbrains.annotations.NotNull;

public interface ScreenAccess {
    void addSymbolChatComponents();
    @NotNull
    FontProcessor getFontProcessor();
    void refreshSuggestions();
    boolean onKeyPressed(int keyCode, int scanCode, int modifiers);
    boolean onCharTyped(char chr, int modifiers);
    boolean isSymbolChatWidget(Element element);
}
