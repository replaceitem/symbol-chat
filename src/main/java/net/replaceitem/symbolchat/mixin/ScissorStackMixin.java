package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.replaceitem.symbolchat.extensions.DrawContextExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Deque;

@Mixin(DrawContext.ScissorStack.class)
public abstract class ScissorStackMixin implements DrawContextExtension.ScissorStackExtension {
    @Shadow
    @Final
    private Deque<ScreenRect> stack;

    @Override
    public boolean overlaps(ScreenRect rect) {
        return this.stack.isEmpty() || this.stack.peek().overlaps(rect);
    }
}