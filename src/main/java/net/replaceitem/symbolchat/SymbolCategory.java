package net.replaceitem.symbolchat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SymbolCategory {
    public String id;
    public String icon;
    public String nameKey;
    protected List<String> symbols;

    public SymbolCategory(String id, String icon, SymbolList... symbolLists) {
        this.id = id;
        this.icon = icon;
        this.nameKey = "symbolchat.tab." + id;
        this.symbols = new ArrayList<>();
        for (SymbolList symbolFile : symbolLists) {
            this.symbols.addAll(symbolFile.lines);
        }
    }
    
    public void assignSymbols(SymbolList... symbolLists) {
        this.symbols.clear();
        for (SymbolList symbolFile : symbolLists) {
            this.symbols.addAll(symbolFile.lines);
        }
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public Stream<String> stream() {
        return this.symbols.stream();
    }
}
