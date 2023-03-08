package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BookEditScreen.PageContent.class)
public interface PageContentAccessor {
    @Accessor
    BookEditScreen.Position getPosition();
}
