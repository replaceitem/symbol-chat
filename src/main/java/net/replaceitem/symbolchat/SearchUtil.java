package net.replaceitem.symbolchat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchUtil {
    @NotNull
    public static Stream<String> performSearch(@NotNull Stream<String> symbols, @Nullable String search) {
        if(search == null || search.isBlank()) return symbols;
        String lowerSearch = search.toLowerCase();
        List<String> searchWords = Arrays.stream(lowerSearch.split(" ")).toList();
        record CachedPriorityComparable<T>(T element, double priority) implements Comparable<CachedPriorityComparable<?>> {
            @Override
            public int compareTo(@NotNull CachedPriorityComparable other) {
                return Double.compare(other.priority, priority);
            }
        }
        return symbols
                .map(symbol -> new CachedPriorityComparable<>(
                        symbol,
                        getSearchOrder(symbol, lowerSearch, searchWords)
                ))
                .filter(item -> item.priority() >= 0)
                .sorted()
                .map(CachedPriorityComparable::element);
    }

    /**
     * 
     * @param symbol The symbol to determine the search order
     * @param searchWords The search terms
     * @return The search order, the higher, the more relevant to the search term (or -1 for no match)
     */
    private static double getSearchOrder(String symbol, String searchString, List<String> searchWords) {
        if(searchWords.isEmpty()) return -1;
        String symbolName = symbol.codePoints().mapToObj(Util::getCodepointName).collect(Collectors.joining(" "));
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
}
