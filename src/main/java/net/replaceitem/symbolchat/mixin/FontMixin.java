package net.replaceitem.symbolchat.mixin;

import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.replaceitem.symbolchat.extensions.FontAccess;
import net.replaceitem.symbolchat.extensions.FontSetSourceAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

@Mixin(Font.class)
public abstract class FontMixin implements FontAccess {
    @Shadow protected abstract GlyphSource getGlyphSource(FontDescription fontDescription);

    @Unique
    @Override
    public IntPredicate getMissingGlyphPredicate(Style style) {
        GlyphSource glyphs = this.getGlyphSource(style.getFont());
        if(glyphs instanceof FontSet.Source source) {
            Int2ObjectMap<IntList> glyphsByWidth = ((FontSetAccessor) ((FontSetSourceAccess) source).getFontSet()).getGlyphsByWidth();
            IntOpenHashSet nonMissingCodepoints = new IntOpenHashSet(glyphsByWidth.values().stream().flatMapToInt(IntCollection::intStream).iterator());
            return k -> !nonMissingCodepoints.contains(k);
        } else return i -> false;
    }

    @Unique
    @Override
    public IntUnaryOperator getCodepointWidthGetter(Style style) {
        GlyphSource glyphs = this.getGlyphSource(style.getFont());
        if(glyphs instanceof FontSet.Source source) {
            Int2ObjectMap<IntList> glyphsByWidth = ((FontSetAccessor) ((FontSetSourceAccess) source).getFontSet()).getGlyphsByWidth();
            Int2IntOpenHashMap widthByCodepoint = new Int2IntOpenHashMap((int) glyphsByWidth.values().stream().flatMapToInt(IntCollection::intStream).count());
            for (Map.Entry<Integer, IntList> entry : glyphsByWidth.int2ObjectEntrySet()) {
                int width = entry.getKey();
                for (int codepoint : entry.getValue()) {
                    widthByCodepoint.put(codepoint, width);
                }
            }
            return i -> widthByCodepoint.getOrDefault(i, -1);
        } else return i -> -1;
    }
}
