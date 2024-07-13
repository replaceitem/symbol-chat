package net.replaceitem.symbolchat;

import com.ibm.icu.lang.UCharacter;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Util {

    /**
     * @param string input string to be converted
     * @return The provided string, with capitalization applied to the first character of each word.
     */
    public static String generateCapitalization(String string) {
        char[] charArray = string.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if(i == 0 || charArray[i-1]==' ') charArray[i] = Character.toTitleCase(charArray[i]);
        }
        return new String(charArray);
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

    /**
     * @return The name of the codepoint in all lowercase
     */
    public static String getCodepointName(int codepoint) {
        String name = UCharacter.getName(codepoint);
        if(name != null) return name.toLowerCase();
        name = Character.getName(codepoint);
        if(name != null) return name.toLowerCase();
        return UCharacter.getExtendedName(codepoint).toLowerCase();
    }

    public static String getCapitalizedSymbolName(int codePoint) {
        return generateCapitalization(getCodepointName(codePoint));
    }
    
    public static String getCapitalizedSymbolName(String symbol) {
        return symbol.codePoints().mapToObj(Util::getCapitalizedSymbolName).collect(Collectors.joining(", "));
    }

    public static String getPrettySymbolName(int codePoint) {
        return getCapitalizedSymbolName(codePoint);
    }
}
