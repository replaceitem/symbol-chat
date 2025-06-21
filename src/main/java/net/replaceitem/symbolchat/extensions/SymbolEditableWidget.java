package net.replaceitem.symbolchat.extensions;

import net.replaceitem.symbolchat.resource.FontProcessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

public interface SymbolEditableWidget {
    @Unique
    void setFontProcessorSupplier(@Nullable Supplier<FontProcessor> fontProcessorSupplier);

    @Unique
    void setRefreshSuggestions(@Nullable Runnable refreshSuggestions);
}
