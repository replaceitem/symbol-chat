package net.replaceitem.symbolchat.extensions;

import net.minecraft.client.gui.navigation.ScreenPosition;
import org.spongepowered.asm.mixin.Unique;

public interface MultilineEditBoxExtension {
    @Unique
    void insert(String text);
    @Unique

    ScreenPosition getCursorPosition();
}
