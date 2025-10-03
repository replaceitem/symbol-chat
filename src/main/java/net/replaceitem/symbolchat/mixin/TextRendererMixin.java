package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.font.*;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.extensions.TextRendererAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

@Mixin(TextRenderer.class)
public abstract class TextRendererMixin implements TextRendererAccess {
    @Shadow protected abstract GlyphProvider getGlyphs(StyleSpriteSource source);

    @Shadow public abstract TextHandler getTextHandler();

    @Unique
    @Override
    public IntPredicate getMissingGlyphPredicate(Style style) {
        GlyphProvider glyphs = this.getGlyphs(style.getFont());
        return codepoint -> {
            GlyphMetrics metrics = glyphs.get(codepoint).getMetrics();
            return metrics == BuiltinEmptyGlyph.MISSING || metrics == BuiltinEmptyGlyph.WHITE;
        };
    }

    @Unique
    @Override
    public IntUnaryOperator getCodepointWidthGetter(Style style) {
        TextHandler.WidthRetriever widthRetriever = ((TextHandlerAccessor) this.getTextHandler()).getWidthRetriever();
        return codepoint -> MathHelper.ceil(widthRetriever.getWidth(codepoint, style));
    }
}
