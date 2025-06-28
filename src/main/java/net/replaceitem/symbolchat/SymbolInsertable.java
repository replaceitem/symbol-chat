package net.replaceitem.symbolchat;

public interface SymbolInsertable {
    void insertSymbol(String symbol);
    default void focusTextbox() {}
}
