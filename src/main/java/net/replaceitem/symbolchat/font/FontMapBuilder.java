package net.replaceitem.symbolchat.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.replaceitem.symbolchat.Util;

import java.util.stream.Collectors;

public class FontMapBuilder {

    //protected final Map<String, String> map = new HashMap<>();
    protected final JsonObject map = new JsonObject();

    public FontMapBuilder put(String from, String to) {
        map.add(from, new JsonPrimitive(to));
        return this;
    }

    public FontMapBuilder put(String from, int to) {
        return put(from, Util.stringFromCodePoint(to));
    }

    public FontMapBuilder put(int from, String to) {
        return put(Util.stringFromCodePoint(from), to);
    }

    public FontMapBuilder put(int from, int to) {
        return put(Util.stringFromCodePoint(from), Util.stringFromCodePoint(to));
    }

    public FontMapBuilder putSeperated(String fromSequence, String... toStrings) {
        JsonArray array = new JsonArray(toStrings.length);
        for (String toString : toStrings) {
            array.add(toString);
        }
        map.add(fromSequence, array);
        return this;
    }

    public FontMapBuilder putAlphabetLower(String to) {
        assertCodePointCount(to, 26);
        return putSequence('a', to);
    }

    public FontMapBuilder putAlphabetUpper(String to) {
        assertCodePointCount(to, 26);
        return putSequence('A', to);
    }

    public FontMapBuilder putNumbers(String to) {
        assertCodePointCount(to, 10);
        return putSequence('0', to);
    }


    public FontMapBuilder putSequence(int codePointStart, String toSequence) {
        put(Util.stringFromCodePoint(codePointStart) + "..", toSequence);
        return this;
    }


    public FontMapBuilder shiftAlphabetLower(int to) {
        return shiftSequence('a', to, 26);
    }

    public FontMapBuilder shiftAlphabetUpper(int to) {
        return shiftSequence('A', to, 26);
    }

    public FontMapBuilder shiftNumbers(int to) {
        return shiftSequence('0', to, 10);
    }

    public FontMapBuilder shiftAlphabetLower(String to) {
        return shiftSequence('a', to.codePoints().findFirst().orElseThrow(), 26);
    }

    public FontMapBuilder shiftAlphabetUpper(String to) {
        return shiftSequence('A', to.codePoints().findFirst().orElseThrow(), 26);
    }

    public FontMapBuilder shiftNumbers(String to) {
        return shiftSequence('0', to.codePoints().findFirst().orElseThrow(), 10);
    }

    public FontMapBuilder shiftSequence(int codePointStart, int codePointShiftStart, int sequenceLength) {
        put(Util.stringFromCodePoint(codePointStart) + ".." + Util.stringFromCodePoint(codePointStart+sequenceLength-1), Util.stringFromCodePoint(codePointShiftStart) + "..");
        return this;
    }

    private void assertCodePointCount(String string, int count) {
        if (Util.getCodePointCount(string) != count) {
            throw new IllegalArgumentException("Expected " + count + " codepoints: \n" + string.codePoints().mapToObj(Util::stringFromCodePoint).collect(Collectors.joining("\n")));
        }
    }
}
