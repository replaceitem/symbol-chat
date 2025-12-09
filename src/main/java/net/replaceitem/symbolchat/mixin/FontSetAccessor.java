package net.replaceitem.symbolchat.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.gui.font.FontSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FontSet.class)
public interface FontSetAccessor {

    @Accessor
    Int2ObjectMap<IntList> getGlyphsByWidth();
}
