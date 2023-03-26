package net.replaceitem.symbolchat.font;

import java.util.Map;

public class MappedFontProcessor extends FontProcessor {

    public MappedFontProcessor(String nameKey, Map<String, String> map) {
        super(nameKey, key -> map.getOrDefault(key, key));
    }
}
