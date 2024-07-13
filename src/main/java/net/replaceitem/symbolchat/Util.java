package net.replaceitem.symbolchat;

import com.ibm.icu.lang.UCharacter;

import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Util {

    /**
     * @param string input string to be converted
     * @return The provided string, with capitalization applied to the first character of each word.
     */
    public static String generateCapitalization(String string) {
        // Todo this can surely be optimized
        if(string == null) string = "Null";
        StringBuilder newString = new StringBuilder();
        String lower = string.toLowerCase(Locale.ROOT);
        newString.append(string.charAt(0));
        for (int i = 1; i < string.length(); i++) {
            newString.append(UCharacter.isUAlphabetic(string.charAt(i-1)) ? lower.charAt(i) : string.charAt(i));
        }
        return newString.toString();
    }


    public static String stringFromCodePoint(int num) {
        return UCharacter.toString(num);
    }

    public static String stringFromCodePoints(IntStream codepoints) {
        return codepoints.mapToObj(Util::stringFromCodePoint).collect(Collectors.joining());
    }

    public static int getCodePointCount(String string) {
        return string.codePointCount(0, string.length());
    }


    public static String getCapitalizedSymbolName(int codePoint) {
        return generateCapitalization(UCharacter.getName(codePoint));
    }
    
    public static String getCapitalizedSymbolName(String symbol) {
        return symbol.codePoints().mapToObj(Util::getCapitalizedSymbolName).collect(Collectors.joining(", "));
    }
}
