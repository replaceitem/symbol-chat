package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.font.BuiltinEmptyGlyph;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.TextRendererAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

@Mixin(TextRenderer.class)
public abstract class TextRendererMixin implements TextRendererAccess {
    @Shadow abstract FontStorage getFontStorage(Identifier id);

    @Shadow @Final
    boolean validateAdvance;

    @Unique
    @Override
    public IntPredicate getMissingGlyphPredicate(Style style) {
        FontStorage fontStorage = this.getFontStorage(style.getFont());
        return codepoint -> {
            Glyph glyph = fontStorage.getGlyph(codepoint, validateAdvance);
            return glyph == BuiltinEmptyGlyph.MISSING || glyph == BuiltinEmptyGlyph.WHITE;
        };
    }

    @Unique
    @Override
    public IntUnaryOperator getCodepointWidthGetter(Style style) {
        FontStorage fontStorage = this.getFontStorage(style.getFont());
        return codepoint -> MathHelper.ceil(fontStorage.getGlyph(codepoint, this.validateAdvance).getAdvance(style.isBold()));
    }
}
