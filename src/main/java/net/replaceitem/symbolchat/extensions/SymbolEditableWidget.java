package net.replaceitem.symbolchat.extensions;

import net.replaceitem.symbolchat.resource.FontProcessor;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface SymbolEditableWidget {
    void setFontProcessorSupplier(@Nullable Supplier<FontProcessor> fontProcessorSupplier);

    void setConvertFontsPredicate(@Nullable BiFunction<String, @Nullable String, Boolean> convertFontsPredicate);

    void setRefreshSuggestions(@Nullable Runnable refreshSuggestions);
}
