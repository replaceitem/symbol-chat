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

    public static void loadLists() {
        symbolLists = new ArrayList<>();
        tryLoadList("1_people_nature");
        tryLoadList("2_objects");
        tryLoadList("3_arrows");
        tryLoadList("4_symbols");
        tryLoadList("5_shapes");
        tryLoadList("6_lines_blocks");
        tryLoadList("7_numbers");
        loadCustomList();

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
    
    public static void loadCustomList() {
        reloadCustomList();
        symbolLists.add(customList);
    }
    
    public static void reloadCustomList() {
        customList.items = Collections.singletonList(SymbolChat.config.getCustomSymbols());
        customList.splitStrings();
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
        
        SymbolList list;
        try {
            list = gson.fromJson(stringBuilder.toString(), SymbolList.class);
            list.splitStrings();
            list.id = id;
        } catch (JsonSyntaxException e) {
            SymbolChat.LOGGER.error("Could not load " + id + " list", e);
            list = null;
        }
        return list;
    }
}
