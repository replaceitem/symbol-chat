package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.gui.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EditBox.class)
public interface EditBoxAccessor {
    @Accessor("selectionEnd")
    void setSelectionEnd(int cursorEnd);
}
