package net.replaceitem.symbolchat.resource;

import com.google.gson.*;
import com.ibm.icu.lang.UCharacter;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.IdentifierException;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.replaceitem.symbolchat.SymbolChat;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static net.replaceitem.symbolchat.SymbolChat.NAMESPACE;

public class SymbolManager implements SimpleSynchronousResourceReloadListener {
    
    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(NAMESPACE,"symbols");
    public static final FileToIdConverter SYMBOLS_FINDER = new FileToIdConverter("symbols", ".txt");
    public static final FileToIdConverter SYMBOL_TABS_FINDER = FileToIdConverter.json("symbol_tabs");

    private List<SymbolTab> tabs = List.of();
    private List<String> allSymbols = List.of();
    private final HashMap<Identifier, SymbolList> listCache = new HashMap<>();
    private final SymbolList.Mutable favoritesList = new SymbolList.Mutable(Identifier.fromNamespaceAndPath(NAMESPACE, "favorites"));
    private final SymbolList.Mutable customKaomojisList = new SymbolList.Mutable(Identifier.fromNamespaceAndPath(NAMESPACE, "custom_kaomojis"));
    
    @Override
    public Identifier getFabricId() {
        return IDENTIFIER;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        this.listCache.clear();
        this.addCachedList(favoritesList);
        this.addCachedList(customKaomojisList);
        tabs = new ArrayList<>();
        for (Map.Entry<Identifier, Resource> identifierListEntry : SYMBOL_TABS_FINDER.listMatchingResources(manager).entrySet()) {
            Identifier identifier = SYMBOL_TABS_FINDER.fileToId(identifierListEntry.getKey());
            Resource tabResource = identifierListEntry.getValue();
            try(BufferedReader symbolTabReader = tabResource.openAsReader()) {
                SymbolTab symbolTab = readTab(manager, symbolTabReader, identifier);
                tabs.add(symbolTab);
            } catch (IOException | JsonParseException | IdentifierException e) {
                SymbolChat.LOGGER.error("Could not load symbol tab {}", identifier, e);
            }
        }
        tabs.sort(SymbolTab::compareTo);
        tabs = Collections.unmodifiableList(tabs);
        allSymbols = tabs.stream().flatMap(SymbolTab::streamSymbols).distinct().toList();
    }

    public boolean isFavorite(String symbol) {
        return favoritesList.contains(symbol);
    }

    public List<SymbolTab> getTabs() {
        return tabs;
    }
    
    public Optional<SymbolTab> getTab(Identifier identifier) {
        return tabs.stream().filter(tab -> tab.getId().equals(identifier)).findFirst();
    }
    
    public Stream<String> getFavoriteSymbols() {
        return favoritesList.stream();
    }

    public Stream<String> streamAllSymbols() {
        return allSymbols.stream().filter(s -> s.codePoints().count() <= 1);
    }

    @NotNull
    private SymbolTab readTab(ResourceManager manager, BufferedReader symbolTabReader, Identifier identifier) {
        JsonObject object = GsonHelper.parse(symbolTabReader);
        String icon = GsonHelper.getAsString(object, "icon");
        int order = GsonHelper.getAsInt(object, "order");
        SymbolTab.Type type = SymbolTab.Type.getOrDefault(GsonHelper.getAsString(object, "type", null), SymbolTab.Type.SYMBOLS);
        boolean searchBar = GsonHelper.getAsBoolean(object, "search_bar", false);
        JsonArray symbolFiles = GsonHelper.getAsJsonArray(object, "symbols", new JsonArray(0));
        List<SymbolList> symbols = readSymbolLists(manager, symbolFiles);
        return new SymbolTab(identifier, icon, order, type, searchBar, symbols);
    }

    @NotNull
    private List<SymbolList> readSymbolLists(ResourceManager manager, JsonArray symbolFiles) {
        List<SymbolList> symbols = new ArrayList<>();
        for (JsonElement symbolFile : symbolFiles) {
            Identifier identifier;
            SymbolList.SplitType type;
            if(symbolFile instanceof JsonPrimitive primitive) {
                identifier = Identifier.parse(primitive.getAsString());
                type = SymbolList.SplitType.CODEPOINT;
            } else if(symbolFile instanceof JsonObject object) {
                identifier = Identifier.parse(GsonHelper.getAsString(object, "id"));
                type = SymbolList.SplitType.getOrDefault(GsonHelper.getAsString(object, "split", null), SymbolList.SplitType.CODEPOINT);
            } else {
                continue;
            }
            try {
                symbols.add(readSymbolList(manager, identifier, type));
            } catch (IOException | JsonParseException e) {
                SymbolChat.LOGGER.error("Could not load symbols {}", identifier, e);
            }
        }
        return symbols;
    }
    
    @NotNull
    private SymbolList readSymbolList(ResourceManager manager, Identifier identifier, SymbolList.SplitType type) throws IOException, JsonParseException {
        if(listCache.containsKey(identifier)) return listCache.get(identifier);
        try(BufferedReader symbolsReader = manager.openAsReader(SYMBOLS_FINDER.idToFile(identifier))) {
            SymbolList symbolList = new SymbolList(identifier, type.split(symbolsReader));
            this.addCachedList(symbolList);
            return symbolList;
        }
    }
    
    private void addCachedList(SymbolList symbolList) {
        this.listCache.put(symbolList.getId(), symbolList);
    }

    public void onCustomSymbolsChanged(String favoriteSymbols) {
        favoritesList.clear();
        favoriteSymbols.codePoints().mapToObj(UCharacter::toString).forEach(favoritesList::addSymbol);
    }

    public void onCustomKaomojisChanged(List<String> customKaomojis) {
        customKaomojisList.clear();
        customKaomojis.forEach(customKaomojisList::addSymbol);
    }

    public boolean isOnlyFavorites(SymbolTab tab) {
        return tab.getSymbols().size() == 1 && tab.getSymbols().getFirst() == favoritesList;
    }
}
