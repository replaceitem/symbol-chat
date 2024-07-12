package net.replaceitem.symbolchat;

import net.minecraft.text.Style;

import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

public interface TextRendererAccess {
    IntPredicate getMissingGlyphPredicate(Style style);
    IntUnaryOperator getCodepointWidthGetter(Style style);
}
