package net.replaceitem.symbolchat.font;


import net.minecraft.client.resource.language.I18n;

import java.util.function.Function;
import java.util.stream.Collectors;

public class FontProcessor {
    protected final String nameKey;
    protected final Function<String, String> codePointConverter;
    public FontProcessor(String nameKey, Function<String, String> codePointConverter) {
        this.nameKey = "symbolchat.font." + nameKey;
        this.codePointConverter = codePointConverter;
    }

    public String convertString(String string) {
        return string.codePoints().mapToObj(FontProcessor::stringFromCodePoint).map(codePointConverter).collect(Collectors.joining());
    }

    public String getConvertedName() {
        return this.convertString(I18n.translate(this.nameKey));
    }

    @Override
    public String toString() {
        return getConvertedName();
    }


    public static String stringFromCodePoint(int num) {
        return new String(Character.toChars(num));
    }

    public static int getCodePointCount(String string) {
        return string.codePointCount(0, string.length());
    }
}
