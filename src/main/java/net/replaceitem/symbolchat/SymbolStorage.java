package net.replaceitem.symbolchat;

import net.replaceitem.symbolchat.config.ClothConfig;

import java.util.ArrayList;
import java.util.List;

public class SymbolStorage {
    
    
    public static List<SymbolCategory> categories = new ArrayList<>();
    public static SymbolCategory customSymbols;
    public static SymbolCategory kaomojis;
    
    public static SymbolCategory all;
    
    
    private static void addCategory(SymbolCategory category) {
        if(category == null) return;
        categories.add(category);
    }
    
    public static void load() {
        categories.clear();
        try {
            addCategory(SymbolCategory.fromSymbolListFile(SymbolListFile.loadFromFile("people_nature")));
            addCategory(SymbolCategory.fromSymbolListFile(SymbolListFile.loadFromFile("objects")));
            addCategory(SymbolCategory.fromSymbolListFile(SymbolListFile.loadFromFile("arrows")));
            addCategory(SymbolCategory.fromSymbolListFile(SymbolListFile.loadFromFile("symbols")));
            addCategory(SymbolCategory.fromSymbolListFile(SymbolListFile.loadFromFile("shapes")));
            addCategory(SymbolCategory.fromSymbolListFile(SymbolListFile.loadFromFile("numbers")));
        } catch (Exception e) {
            SymbolChat.LOGGER.error("Could not load symbols", e);
        }
        try {
            kaomojis = SymbolCategory.fromKaomojiListFile(SymbolListFile.loadFromFile("kaomojis"), SymbolChat.config.getCustomKaomojis());
        } catch (Exception e) {
            SymbolChat.LOGGER.error("Could not load kaomojis", e);
        }
        
        customSymbols = SymbolCategory.createCustom(SymbolChat.config.getCustomSymbols());
        
        List<String> allSymbols = new ArrayList<>();
        allSymbols.addAll(categories.stream().flatMap(category -> category.symbols.stream()).toList());
        allSymbols.addAll(customSymbols.symbols);
        all = SymbolCategory.createAll(allSymbols);
    }

    public static void reloadCustomLists(ClothConfig clothConfig) {
        try {
            kaomojis = SymbolCategory.fromKaomojiListFile(SymbolListFile.loadFromFile("kaomojis"), clothConfig.custom_kaomojis);
        } catch (Exception e) {
            SymbolChat.LOGGER.error("Could not load kaomojis", e);
        }

        customSymbols = SymbolCategory.createCustom(clothConfig.custom_symbols);

        List<String> allSymbols = new ArrayList<>();
        allSymbols.addAll(categories.stream().flatMap(category -> category.symbols.stream()).toList());
        allSymbols.addAll(customSymbols.symbols);
        all = SymbolCategory.createAll(allSymbols);
    }
}
