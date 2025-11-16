package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.replaceitem.symbolchat.extensions.FontAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import com.mojang.blaze3d.font.GlyphInfo;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

@Mixin(Font.class)
public abstract class FontMixin implements FontAccess {
    @Shadow protected abstract GlyphSource getGlyphSource(FontDescription fontDescription);

    @Shadow public abstract StringSplitter getSplitter();

    @Unique
    @Override
    public IntPredicate getMissingGlyphPredicate(Style style) {
        GlyphSource glyphs = this.getGlyphSource(style.getFont());
        return codepoint -> {
            GlyphInfo metrics = glyphs.getGlyph(codepoint).info();
            return metrics == SpecialGlyphs.MISSING || metrics == SpecialGlyphs.WHITE;
        };
    }

    @Unique
    @Override
    public IntUnaryOperator getCodepointWidthGetter(Style style) {
        StringSplitter.WidthProvider widthRetriever = ((StringSplitterAccessor) this.getSplitter()).getWidthProvider();
        return codepoint -> Mth.ceil(widthRetriever.getWidth(codepoint, style));
    }
}
