package net.replaceitem.symbolchat.resource;

import com.ibm.icu.lang.UCharacter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;

public class SymbolList {
    private final Identifier id;
    protected final List<String> symbols;

    public SymbolList(Identifier id, List<String> symbols) {
        this.id = id;
        this.symbols = symbols;
    }

    public Identifier getId() {
        return id;
    }

    public Stream<String> stream() {
        return symbols.stream();
    }
    
    public static class Mutable extends SymbolList {

        private final HashSet<String> set = new HashSet<>();
        
        public Mutable(Identifier id, List<String> symbols) {
            super(id, new ArrayList<>(symbols));
            set.addAll(symbols);
        }
        
        public Mutable(Identifier id) {
            super(id, new ArrayList<>());
        }

        public void addSymbol(String symbol) {
            this.symbols.add(symbol);
            this.set.add(symbol);
        }
        
        public boolean contains(String symbol) {
            return set.contains(symbol);
        }
        
        public void clear() {
            this.symbols.clear();
            this.set.clear();
        }
    }


    public static class SplitType {
        private final Function<BufferedReader,List<String>> splitter;

        private static final Map<String, SplitType> TYPES = new HashMap<>();
        
        public static final SplitType CODEPOINT = register("codepoint", new SplitType(reader -> reader.lines().flatMapToInt(String::codePoints).mapToObj(UCharacter::toString).toList()));
        public static final SplitType LINE = register("line", new SplitType(reader -> reader.lines().toList()));

        public SplitType(Function<BufferedReader, List<String>> splitter) {
            this.splitter = splitter;
        }

        public List<String> split(BufferedReader reader) {
            return splitter.apply(reader);
        }
        
        public static SplitType register(String name, SplitType splitType) {
            TYPES.put(name, splitType);
            return splitType;
        }

        @NotNull
        public static SplitType getOrDefault(String name, @NotNull SplitType defaultType) {
            SplitType splitType = get(name);
            return splitType == null ? defaultType : splitType;
        }

        @Nullable
        public static SplitType get(String name) {
            return TYPES.get(name);
        }
    }
}
