package net.replaceitem.symbolchat.mixin.widget;

import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EditBox.class)
public interface EditBoxAccessor {
    @Accessor("displayPos")
    int getDisplayPos();
    @Accessor("cursorPos")
    int getCursorPos();
    @Accessor("highlightPos")
    int getHighlightPos();
}
