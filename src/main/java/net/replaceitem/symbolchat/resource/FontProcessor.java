package net.replaceitem.symbolchat.resource;


import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.replaceitem.symbolchat.Util;

import java.util.function.Function;
import java.util.stream.Collectors;

public class FontProcessor {
    protected final Identifier id;
    protected final Function<String, String> codePointConverter;
    protected final String convertedName;
    protected final int order;
    protected final boolean reverseDirection;

    public FontProcessor(Identifier id, Function<String, String> codePointConverter, int order, boolean reverseDirection) {
        this.id = id;
        this.order = order;
        this.codePointConverter = codePointConverter;
        this.reverseDirection = reverseDirection;
        String translationKey = id.toTranslationKey("symbolchat.font");
        if(I18n.hasTranslation(translationKey)) {
            String name = I18n.translate(translationKey);
            if(reverseDirection) name = new StringBuilder(name).reverse().toString();
            this.convertedName = convertString(name);
        } else {
            this.convertedName = translationKey;
        }
    }

    public String convertString(String string) {
        return string.codePoints().mapToObj(Util::stringFromCodePoint).map(codePointConverter).collect(Collectors.joining());
    }

    public boolean isReverseDirection() {
        return reverseDirection;
    }

    @Override
    public String toString() {
        return convertedName;
    }
}
