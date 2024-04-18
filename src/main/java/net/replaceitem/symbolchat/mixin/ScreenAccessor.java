package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor
    Screen.PositionedTooltip getTooltip();

    @Accessor
    void setTooltip(Screen.PositionedTooltip tooltip);
}
