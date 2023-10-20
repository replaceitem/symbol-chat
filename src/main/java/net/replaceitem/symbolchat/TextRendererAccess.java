package net.replaceitem.symbolchat;

import net.minecraft.text.Style;

public interface TextRendererAccess {
    boolean isMissingGlyph(int codepoint, Style style);
}
