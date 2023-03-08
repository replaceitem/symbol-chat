package net.replaceitem.symbolchat;

import net.replaceitem.symbolchat.config.ClothConfig;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * 
     * @param symbol The symbol to determine the search order
     * @param searchWords The search terms
     * @return The search order, the higher, the more relevant to the search term (or -1 for no match)
     */
    private static double getSearchOrder(String symbol, String searchString, List<String> searchWords) {
        if(searchWords.isEmpty()) return -1;
        String symbolName = symbol.codePoints().mapToObj(Character::getName).collect(Collectors.joining(" "));
        if(symbolName.startsWith(searchString)) return Integer.MAX_VALUE - symbolName.length(); // until current search a perfect match -> highest priority sorted by symbol name length
        double sum = searchWords.stream().mapToDouble(searchWord -> getRelevance(symbolName, searchWord)).sum();
        if(sum < 0) return -1;
        return 100+sum;
    }
    
    private static double getRelevance(String symbolName, String searchWord) {
        int nameLength = symbolName.length();
        int searchLength = searchWord.length();
        if(searchLength > nameLength) return -0.0001; // tiny penalty for having words in the search that don't appear, for ordering’s sake
        int index = symbolName.indexOf(searchWord);
        if(index < 0) return -0.0001; 
        if(nameLength == searchLength) return 3;
        return 2 - ((double) index) / (nameLength-searchLength); // range of 1 (appears at the start of the name) to 2 (at the end)
    }

    public static Stream<String> performSearch(String search) {
        if(search == null) return Stream.of();
        if(search.isBlank()) return all.symbols.stream();
        String upperSearch = search.toUpperCase();
        List<String> searchWords = Arrays.stream(upperSearch.split(" ")).toList();
        return all.symbols.stream()
                .map(symbol -> new Pair<>(
                        symbol,
                        getSearchOrder(symbol, upperSearch, searchWords)
                ))
                .filter(stringIntegerPair -> stringIntegerPair.getB() >= 0)
                .sorted(Comparator.comparingDouble(stringDoublePair -> -stringDoublePair.getB()))
                .map(Pair::getA);
    }
}
