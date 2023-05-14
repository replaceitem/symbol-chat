package net.replaceitem.symbolchat;

import net.minecraft.client.MinecraftClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SymbolList {
    public String id;
    public List<String> lines;

    public SymbolList(String id, List<String> lines) {
        this.id = id;
        this.lines = lines;
    }

    public SymbolList separateSymbols() {
        this.lines = this.lines.stream().flatMapToInt(String::codePoints).mapToObj(Character::toString).toList();
        return this;
    }

    public static SymbolList loadFromFile(String name, boolean separate) {
        String path = String.format("assets/symbol-chat/symbols/%s.txt", name);
        InputStream stream = MinecraftClient.getInstance().getClass().getClassLoader().getResourceAsStream(path);
        if(stream == null) throw new RuntimeException(String.format("Could not open '%s' to get symbols", path));
        List<String> lines;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            lines = bufferedReader.lines().toList();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not open '%s' to get symbols", path), e);
        }
        SymbolList symbolList = new SymbolList(name, lines);
        if(separate) symbolList.separateSymbols();
        return symbolList;
    }
}
