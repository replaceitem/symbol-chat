package symbolchat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SymbolStorage {

    public static ArrayList<SymbolList> symbolLists;
    public static SymbolList customList = SymbolList.createCustom();
    public static SymbolList kaomojiList;

    public static void loadLists() {
        symbolLists = new ArrayList<>();
        symbolLists.add(tryLoadList("people_nature"));
        symbolLists.add(tryLoadList("objects"));
        symbolLists.add(tryLoadList("arrows"));
        symbolLists.add(tryLoadList("symbols"));
        symbolLists.add(tryLoadList("shapes"));
        symbolLists.add(tryLoadList("numbers"));
        symbolLists.add(loadCustomList());
        symbolLists.add(loadKaomojiList());
    }

    private static SymbolList tryLoadList(String name) {
        SymbolList list = loadList(name, true);
        if(list == null) {
            SymbolChat.LOGGER.warn("Could not load symbol file " + name);
            return null;
        }
        return list;
    }
    
    public static SymbolList loadCustomList() {
        reloadCustomList();
        return customList;
    }

    public static void reloadCustomList() {
        customList.items = Collections.singletonList(SymbolChat.config.getCustomSymbols());
        customList.postProcess();
    }

    public static SymbolList loadKaomojiList() {
        reloadKaomojiList();
        return kaomojiList;
    }

    public static void reloadKaomojiList() {
        SymbolList list = loadList("kaomojis", false);
        if(list == null) return;
        if(kaomojiList == null) {
            kaomojiList = list;
        } else {
            kaomojiList.items.clear();
            kaomojiList.items = list.items;
        }
        kaomojiList.items.addAll(SymbolChat.config.getCustomKaomojis());
    }

    public static SymbolList loadList(String id, boolean split) {
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
        
        SymbolList list;
        try {
            list = gson.fromJson(stringBuilder.toString(), SymbolList.class);
            if(split) list.postProcess();
            list.id = id;
        } catch (JsonSyntaxException e) {
            SymbolChat.LOGGER.error("Could not load " + id + " list", e);
            list = null;
        }
        return list;
    }
}
