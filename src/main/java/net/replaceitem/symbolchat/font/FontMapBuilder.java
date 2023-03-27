package net.replaceitem.symbolchat.font;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FontMapBuilder {

    protected final Map<String, String> map = new HashMap<>();

    public FontMapBuilder put(String from, String to) {
        map.put(from, to);
        return this;
    }

    public FontMapBuilder put(String from, int to) {
        return put(from, FontProcessor.stringFromCodePoint(to));
    }

    public FontMapBuilder put(int from, String to) {
        return put(FontProcessor.stringFromCodePoint(from), to);
    }

    public FontMapBuilder put(int from, int to) {
        return put(FontProcessor.stringFromCodePoint(from), FontProcessor.stringFromCodePoint(to));
    }

    public FontMapBuilder putSeperated(String fromSequence, String... toStrings) {
        int codePoints = FontProcessor.getCodePointCount(fromSequence);
        if(codePoints != toStrings.length) throw new IllegalArgumentException("Mismatch in codepoint count");
        for (int i = 0; i < codePoints; i++) {
            String from = FontProcessor.stringFromCodePoint(fromSequence.codePointAt(i));
            String to = toStrings[i];
            map.put(from, to);
        }
        return this;
    }

    public FontMapBuilder putAlphabetLower(String to) {
        assertCodePointCount(to, 26);
        return putSequence(to, 'a');
    }

    public FontMapBuilder putAlphabetUpper(String to) {
        assertCodePointCount(to, 26);
        return putSequence(to, 'A');
    }

    public FontMapBuilder putNumbers(String to) {
        assertCodePointCount(to, 10);
        return putSequence(to, '0');
    }


    public FontMapBuilder putSequence(String toSequence, int codePointStart) {
        int codePoints = FontProcessor.getCodePointCount(toSequence);
        for (int i = 0; i < codePoints; i++) {
            String from = FontProcessor.stringFromCodePoint(codePointStart + i);
            map.put(from, FontProcessor.stringFromCodePoint(toSequence.codePointAt(i)));
        }
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

    public FontMapBuilder shiftSequence(int codePointStart, int codePointShiftStart, int sequenceLength) {
        int shiftDiff = codePointShiftStart-codePointStart;
        for (int i = 0; i < sequenceLength; i++) {
            int fromCodePoint = codePointStart + i;
            String from = FontProcessor.stringFromCodePoint(fromCodePoint);
            String to = FontProcessor.stringFromCodePoint(fromCodePoint + shiftDiff);
            map.put(from, to);
        }
        return this;
    }

    private void assertCodePointCount(String string, int count) {
        if(FontProcessor.getCodePointCount(string) != count) {
            throw new IllegalArgumentException("Expected " + count + " codepoints: \n" + string.codePoints().mapToObj(FontProcessor::stringFromCodePoint).collect(Collectors.joining("\n")));
        }
    }


    public Map<String, String> build() {
        return map;
    }
}
