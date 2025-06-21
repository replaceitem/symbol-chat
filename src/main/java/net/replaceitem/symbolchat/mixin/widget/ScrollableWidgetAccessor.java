package net.replaceitem.symbolchat.mixin.widget;

import net.minecraft.client.gui.widget.ScrollableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScrollableWidget.class)
public interface ScrollableWidgetAccessor {
    @Accessor
    boolean isScrollbarDragged();
}
