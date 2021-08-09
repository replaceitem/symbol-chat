package symbolchat.symbolchat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SymbolStorage {

    public static ArrayList<SymbolList> symbolLists;

    public static void loadLists() {
        symbolLists = new ArrayList<>();

        tryLoadList("faces");
        tryLoadList("nature");
        tryLoadList("objects");
        tryLoadList("arrows");
        tryLoadList("symbols");
        tryLoadList("shapes");
        tryLoadList("characters");
        tryLoadCustomList();

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

    public static void tryLoadCustomList() {
        try {
            Path config = FabricLoader.getInstance().getConfigDir().normalize();
            Files.createDirectories(config);
            Path symbolPath = config.resolve("symbols.txt").normalize();
            File symbolFile = symbolPath.toFile();
            if(!symbolFile.exists()) {
                SymbolChat.LOGGER.info("No custom symbol file present");
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(symbolFile),StandardCharsets.UTF_8));
            String read = reader.readLine();
            List<String> symbols = new ArrayList<>();
            while(read != null) {
                symbols.add(read);
                read = reader.readLine();
            }
            SymbolList symbolList = new SymbolList(symbols);
            symbolList.splitStrings();
            symbolLists.add(symbolList);
        } catch (IOException e) {
            SymbolChat.LOGGER.error("Could not load custom symbol file");
            SymbolChat.LOGGER.error(e);
        }
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

        SymbolList list = gson.fromJson(stringBuilder.toString(), SymbolList.class);
        list.splitStrings();
        list.id = id;
        return list;
    }
}
