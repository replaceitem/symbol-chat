package net.replaceitem.symbolchat;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.MinecraftClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SymbolListFile {
    public String id;

    @SerializedName("name")
    public String nameKey;

    @SerializedName("icon")
    public String icon;

    @SerializedName("symbols")
    public List<String> symbols;



    public List<String> getSeparatedSymbols() {
        return separateSymbols(symbols);
    }

    public static List<String> separateSymbols(List<String> symbols) {
        return symbols.stream().flatMapToInt(String::codePoints).mapToObj(Character::toString).toList();
    }

    public static SymbolListFile loadFromFile(String category) {
        String path = String.format("assets/symbol-chat/symbols/%s.json", category);
        InputStream stream = MinecraftClient.getInstance().getClass().getClassLoader().getResourceAsStream(path);
        if(stream == null) throw new RuntimeException(String.format("Could not open '%s' to get symbols", path));
        InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        SymbolListFile symbolListFile = new Gson().fromJson(streamReader, SymbolListFile.class);
        symbolListFile.id = category;
        return symbolListFile;
    }
}
