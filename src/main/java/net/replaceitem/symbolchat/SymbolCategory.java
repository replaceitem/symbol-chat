package net.replaceitem.symbolchat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class SymbolCategory {
    public String id;
    public String icon;
    public String nameKey;
    public List<String> symbols;

    public SymbolCategory(String id, String icon, String nameKey, List<String> symbols) {
        this.id = id;
        this.icon = icon;
        this.nameKey = nameKey;
        this.symbols = symbols;
    }
    
    public static SymbolCategory fromSymbolListFile(SymbolListFile symbolFile) {
        return new SymbolCategory(
                symbolFile.id,
                symbolFile.icon,
                symbolFile.nameKey,
                symbolFile.getSeparatedSymbols()
        );
    }

    public static SymbolCategory fromKaomojiListFile(SymbolListFile symbolFile, List<String> customKaomojis) {
        return new SymbolCategory(
                symbolFile.id,
                symbolFile.icon,
                symbolFile.nameKey,
                Stream.of(symbolFile.symbols, customKaomojis).flatMap(Collection::stream).toList()
        );
    }

    public static SymbolCategory createCustom(String symbols) {
        return new SymbolCategory(
                "custom",
                "✎",
                "symbolchat.tab.custom",
                SymbolListFile.separateSymbols(List.of(symbols))
        );
    }

    public static SymbolCategory createAll(List<String> symbols) {
        return new SymbolCategory(
                "all",
                "∞",
                "symbolchat.tab.all",
                symbols
        );
    }
}
