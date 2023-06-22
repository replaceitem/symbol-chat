package net.replaceitem.symbolchat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SymbolStorage {
    public static List<SymbolCategory> categories = new ArrayList<>();
    public static SymbolCategory favoriteSymbols;
    public static SymbolCategory kaomojis;
    
    public static SymbolCategory all;
    
    
    private static void addCategory(SymbolCategory category) {
        if(category == null) return;
        categories.add(category);
    }
    
    public static void load() {
        categories.clear();
        try {
            addCategory(new SymbolCategory(
                    "faces_people",
                    "🙂",
                    SymbolList.loadFromFile("faces", true),
                    SymbolList.loadFromFile("hands", true),
                    SymbolList.loadFromFile("people", true),
                    SymbolList.loadFromFile("body", true)
            ));
            addCategory(new SymbolCategory(
                    "nature_food",
                    "🌷",
                    SymbolList.loadFromFile("environment", true),
                    SymbolList.loadFromFile("greenery", true),
                    SymbolList.loadFromFile("animals", true),
                    SymbolList.loadFromFile("food", true)
            ));
            addCategory(new SymbolCategory(
                    "things",
                    "🧰",
                    SymbolList.loadFromFile("things", true),
                    SymbolList.loadFromFile("clothes", true)
            ));
            addCategory(new SymbolCategory(
                    "activities_transport_places",
                    "🎮",
                    SymbolList.loadFromFile("activities", true),
                    SymbolList.loadFromFile("transport", true)
            ));
            addCategory(new SymbolCategory(
                    "symbols",
                    "🗹",
                    SymbolList.loadFromFile("symbols", true),
                    SymbolList.loadFromFile("misc", true)
            ));
            addCategory(new SymbolCategory(
                    "shapes",
                    "🖤",
                    SymbolList.loadFromFile("shapes", true),
                    SymbolList.loadFromFile("arrows", true),
                    SymbolList.loadFromFile("numbers", true)
            ));
        } catch (Exception e) {
            SymbolChat.LOGGER.error("Could not load symbols", e);
        }
        
        reloadKaomojiList();
        reloadFavoritesList();
        reloadAllList();
    }
    
    private static void reloadAllList() {
        List<String> allSymbols = new ArrayList<>();
        allSymbols.addAll(categories.stream().flatMap(category -> category.symbols.stream()).toList());
        allSymbols.addAll(favoriteSymbols.symbols);
        all = new SymbolCategory(
                "all",
                "🔎",
                new SymbolList("all", allSymbols)
        );
    }
    
    private static void reloadKaomojiList() {
        try {
            kaomojis = new SymbolCategory(
                    "kaomoji",
                    "K",
                    SymbolList.loadFromFile("kaomojis", false),
                    new SymbolList("custom_kamojis", SymbolChat.config.getCustomKaomojis())
            );
        } catch (Exception e) {
            SymbolChat.LOGGER.error("Could not load kaomojis", e);
        }
    }
    
    private static void reloadFavoritesList() {
        favoriteSymbols = new SymbolCategory(
                "favorites",
                "✩",
                new SymbolList("favorites", List.of(SymbolChat.config.getFavoriteSymbols())).separateSymbols()
        );
    }

    public static void reloadCustomLists() {
        reloadKaomojiList();
        reloadFavoritesList();
        reloadAllList();
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

    public static Stream<String> performSearch(SymbolCategory category, String search) {
        if(search == null) return Stream.of();
        if(search.isBlank()) return category.stream();
        String upperSearch = search.toUpperCase();
        List<String> searchWords = Arrays.stream(upperSearch.split(" ")).toList();
        return category.stream()
                .map(symbol -> new CachedPriorityComparable<>(
                        symbol,
                        getSearchOrder(symbol, upperSearch, searchWords)
                ))
                .filter(item -> item.priority() >= 0)
                .sorted()
                .map(CachedPriorityComparable::element);
    }

    private record CachedPriorityComparable<T>(T element, double priority) implements Comparable<CachedPriorityComparable<?>> {
        @Override
        public int compareTo(@NotNull SymbolStorage.CachedPriorityComparable other) {
            return Double.compare(other.priority, priority);
        }
    }
}
