package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.gui.font.FontSet;
import net.replaceitem.symbolchat.extensions.FontSetSourceAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FontSet.Source.class)
public class FontSetSourceMixin implements FontSetSourceAccess {
    // synthetic field for outer FontSet instance
    @Final
    @Shadow
    FontSet field_61619;

    @Override
    public FontSet getFontSet() {
        return field_61619;
    }
}
