package net.replaceitem.symbolchat.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.replaceitem.symbolchat.Util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MappedFontProcessor extends FontProcessor {

    public MappedFontProcessor(Identifier id, Map<String, String> map, int order, boolean reverseDirection) {
        super(id, key -> map.getOrDefault(key, key), order, reverseDirection);
    }
    
    public static MappedFontProcessor read(Identifier id, JsonObject object, int order, boolean reverseDirection) throws JsonParseException {
        JsonObject mappings = JsonHelper.getObject(object, "mappings");
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : mappings.entrySet()) {
            String key = stringJsonElementEntry.getKey();
            JsonElement value = stringJsonElementEntry.getValue();
            readMapping(key, value, map);
        }
        return new MappedFontProcessor(id, map, order, reverseDirection);
    }

    private static void readMapping(String key, JsonElement value, Map<String, String> map) throws JsonParseException {
        try {
            if(key.codePoints().count() == 1) {
                if(!(value instanceof JsonPrimitive primitive) || !primitive.isString()) throw new JsonSyntaxException("Expected string for single codepoint mapping");
                map.put(key, primitive.getAsString());
                return;
            }
            CodepointIterator lRange = readRange(key);
            CodepointIterator rRange = readRange(value);
            if(lRange.isOpenEnded() && rRange.isOpenEnded()) throw new JsonSyntaxException("Left and right side of mapping can't both be open ended");
            if(!lRange.isOpenEnded() && !rRange.isOpenEnded() && lRange.size() != rRange.size()) throw new JsonSyntaxException("Mismatched lengths: " + lRange.size() + " and " + rRange.size());
            while (lRange.hasNext() && rRange.hasNext()) {
                map.put(lRange.next(), rRange.next());
            }
        } catch (Exception e) {
            throw new JsonSyntaxException("Error parsing mapping with key '" + key + "'", e);
        }
    }

    private static CodepointIterator readRange(JsonElement value) {
        if(value instanceof JsonPrimitive primitive && primitive.isString()) return readRange(primitive.getAsString());
        if(value instanceof JsonArray array) {
            return new CodepointIterator.Sequence(array.asList().stream().map(MappedFontProcessor::readStringOrCodepoint).toList());
        }
        throw new JsonSyntaxException("Expected string or array as mapping");
    }

    private static CodepointIterator readRange(String key) {
        if(key.contains("..")) {
            String[] split = key.split("\\.\\.");
            if(split.length == 1) {
                String from = split[0];
                if(from.codePoints().count() != 1) throw new JsonSyntaxException("Only one codepoint before '..' allowed");
                return new CodepointIterator.Range(from.codePoints().findFirst().orElseThrow());
            } else if(split.length == 2) {
                String from = split[0];
                String to = split[1];
                if(from.codePoints().count() != 1 || to.codePoints().count() != 1) throw new JsonSyntaxException("Only one codepoint before and after '..' allowed");
                return new CodepointIterator.Range(from.codePoints().findFirst().orElseThrow(), to.codePoints().findFirst().orElseThrow());
            } else throw new JsonSyntaxException("Expected only one occurence of '..' in mapping range");
        }
        return new CodepointIterator.Sequence(key.codePoints().mapToObj(Util::stringFromCodePoint).toList());
    }
    
    private static String readStringOrCodepoint(JsonElement element) {
        if(element instanceof JsonPrimitive primitive) {
            if(primitive.isNumber()) return Util.stringFromCodePoint(primitive.getAsNumber().intValue());
            return primitive.getAsString();
        }
        throw new JsonSyntaxException("Not a string or codepoint number: " + element.toString());
    }
    
    static abstract class CodepointIterator implements Iterator<String> {
        public abstract boolean isOpenEnded();
        public boolean requiresNext() {
            return !isOpenEnded() && hasNext();
        }
        public abstract int size();

        static class Range extends CodepointIterator {
            // both inclusive
            int start;
            int end;
            int nextCodepoint;
            public static final int OPEN_ENDED = Integer.MIN_VALUE;

            public Range(int start, int end) {
                if(start > end && end != OPEN_ENDED) throw new IllegalArgumentException("Second codepoint is lower than first");
                this.start = start;
                this.end = end;
                this.nextCodepoint = start;
            }

            public Range(int start) {
                this(start, OPEN_ENDED);
            }

            @Override
            public boolean isOpenEnded() {
                return end == OPEN_ENDED;
            }

            @Override
            public int size() {
                if(isOpenEnded()) return -1;
                return end-start+1;
            }

            @Override
            public boolean hasNext() {
                return isOpenEnded() || nextCodepoint <= end;
            }

            @Override
            public String next() {
                return Util.stringFromCodePoint(nextCodepoint++);
            }
        }
        
        static class Sequence extends CodepointIterator {
            List<String> sequence;
            int index = 0;

            public Sequence(List<String> sequence) {
                this.sequence = sequence;
            }

            @Override
            public boolean isOpenEnded() {
                return false;
            }

            @Override
            public int size() {
                return sequence.size();
            }

            @Override
            public boolean hasNext() {
                return index < sequence.size();
            }

            @Override
            public String next() {
                return sequence.get(index++);
            }
        }
    }
}
