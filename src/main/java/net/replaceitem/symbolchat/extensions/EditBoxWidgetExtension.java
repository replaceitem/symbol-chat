package net.replaceitem.symbolchat.extensions;

import net.minecraft.client.gui.ScreenPos;
import org.spongepowered.asm.mixin.Unique;

public interface EditBoxWidgetExtension {
    @Unique
    void insert(String text);
    @Unique

    ScreenPos getCursorPosition();
}
