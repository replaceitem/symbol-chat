package net.replaceitem.symbolchat;

import java.util.ArrayList;
import java.util.List;

public class SymbolCategory {
    public String id;
    public String icon;
    public String nameKey;
    public List<String> symbols;

    public SymbolCategory(String id, String icon, SymbolList... symbolLists) {
        this.id = id;
        this.icon = icon;
        this.nameKey = "symbolchat.tab." + id;
        this.symbols = new ArrayList<>();
        for (SymbolList symbolFile : symbolLists) {
            this.symbols.addAll(symbolFile.lines);
        }
    }
}
