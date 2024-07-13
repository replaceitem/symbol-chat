package net.replaceitem.symbolchat;

import com.ibm.icu.lang.UCharacter;
import net.minecraft.util.math.MathHelper;

import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

public class UnicodeSearch {
    private final IntStream stream;

    private UnicodeSearch(IntStream stream) {
        this.stream = stream;
    }

    public static UnicodeSearch ofPage(int page) {
        int start = MathHelper.clamp(page << 16, 0, UCharacter.MAX_CODE_POINT - 1);
        int end = MathHelper.clamp((page + 1) << 16, 0, UCharacter.MAX_CODE_POINT - 1);
        return new UnicodeSearch(IntStream.range(start, end));
    }
    
    public static UnicodeSearch ofAll() {
        return new UnicodeSearch(IntStream.rangeClosed(0, UCharacter.MAX_CODE_POINT));
    }
    
    public UnicodeSearch search(String searchTerm) {
        if (searchTerm.isBlank()) return this;
        return new UnicodeSearch(stream.filter(getSearchFilter(searchTerm)));
    }
    
    public UnicodeSearch filterWidth(int width, IntUnaryOperator widthGetter) {
        return new UnicodeSearch(stream.filter(cp -> widthGetter.applyAsInt(cp) == width));
    }
    
    public UnicodeSearch filter(IntPredicate filter) {
        return new UnicodeSearch(stream.filter(filter));
    }

    public int[] collect() {
        return stream.toArray();
    }
    
    private static IntPredicate getSearchFilter(String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        String[] searchTerms = searchTerm.trim().split("\\s+");
        return cp -> isRelevant(cp, searchTerms);
    }

    private static boolean isRelevant(int codepoint, String[] searchTerms) {
        String name = Util.getCodepointName(codepoint);
        for (String s : searchTerms) {
            if(!name.contains(s)) return false;
        }
        return true;
    }
}
