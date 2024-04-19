package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.replaceitem.symbolchat.DrawContextExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin implements DrawContextExtension {
    @Shadow @Final private DrawContext.ScissorStack scissorStack;

    @Override
    public boolean scissorOverlaps(ScreenRect rect) {
        return ((ScissorStackExtension) this.scissorStack).overlaps(rect);
    }
}
