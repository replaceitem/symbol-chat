package symbolchat.symbolchat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;

public class SymbolStorage {

    public static ArrayList<SymbolList> symbolLists;

    static {
        loadLists();
    }

    public static void loadLists() {
        symbolLists = new ArrayList<>();

        tryLoadList("faces");
        tryLoadList("nature");
        tryLoadList("objects");
        tryLoadList("arrows");
        tryLoadList("symbols");
        tryLoadList("shapes");
        tryLoadList("characters");

        symbolLists.sort(Comparator.comparingInt(o -> o.position));
    }

    private static void tryLoadList(String name) {
        SymbolList list = loadList(name);
        if(list == null) {
            SymbolChat.LOGGER.warn("Could not load symbol file " + name);
            return;
        }
        symbolLists.add(list);
    }

    public static SymbolList loadList(String id) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        InputStream stream = MinecraftClient.getInstance().getClass().getClassLoader().getResourceAsStream("assets/symbol-chat/symbols/" + id + ".json");
        if(stream == null) return null;
        InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_16);

        StringBuilder stringBuilder = new StringBuilder();

        try {
            int data = streamReader.read();
            while (data != -1) {
                stringBuilder.append((char) data);
                data = streamReader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        SymbolList list = gson.fromJson(stringBuilder.toString(),SymbolList.class);
        list.splitStrings();
        list.id = id;
        return list;
    }
}
