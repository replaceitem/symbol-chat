package net.replaceitem.symbolchat.extensions;

import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import net.minecraft.network.chat.Style;

public interface FontAccess {
    IntPredicate getMissingGlyphPredicate(Style style);
    IntUnaryOperator getCodepointWidthGetter(Style style);
}
