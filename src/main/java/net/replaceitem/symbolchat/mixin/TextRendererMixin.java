package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.font.BuiltinEmptyGlyph;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TextRenderer.class)
public abstract class TextRendererMixin {
    @Shadow abstract FontStorage getFontStorage(Identifier id);

    @Shadow @Final
    boolean validateAdvance;

    @Unique
    public boolean isMissingGlyph(int codepoint, Style style) {
        return this.getFontStorage(style.getFont()).getGlyph(codepoint, validateAdvance) == BuiltinEmptyGlyph.MISSING;
    }
}
