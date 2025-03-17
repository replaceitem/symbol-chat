package net.replaceitem.symbolchat.resource;

import com.google.gson.*;
import com.ibm.icu.lang.UCharacter;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.JsonHelper;
import net.replaceitem.symbolchat.SymbolChat;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static net.replaceitem.symbolchat.SymbolChat.NAMESPACE;

public class SymbolManager implements SimpleSynchronousResourceReloadListener {
    
    public static final Identifier IDENTIFIER = Identifier.of(NAMESPACE,"symbols");
    public static final ResourceFinder SYMBOLS_FINDER = new ResourceFinder("symbols", ".txt");
    public static final ResourceFinder SYMBOL_TABS_FINDER = ResourceFinder.json("symbol_tabs");

    private List<SymbolTab> tabs = List.of();
    private List<String> allSymbols = List.of();
    private final HashMap<Identifier, SymbolList> listCache = new HashMap<>();
    private final SymbolList.Mutable favoritesList = new SymbolList.Mutable(Identifier.of(NAMESPACE, "favorites"));
    private final SymbolList.Mutable customKaomojisList = new SymbolList.Mutable(Identifier.of(NAMESPACE, "custom_kaomojis"));
    
    @Override
    public Identifier getFabricId() {
        return IDENTIFIER;
    }

    @Override
    public void reload(ResourceManager manager) {
        this.listCache.clear();
        this.addCachedList(favoritesList);
        this.addCachedList(customKaomojisList);
        tabs = new ArrayList<>();
        for (Map.Entry<Identifier, Resource> identifierListEntry : SYMBOL_TABS_FINDER.findResources(manager).entrySet()) {
            Identifier identifier = SYMBOL_TABS_FINDER.toResourceId(identifierListEntry.getKey());
            Resource tabResource = identifierListEntry.getValue();
            try(BufferedReader symbolTabReader = tabResource.getReader()) {
                SymbolTab symbolTab = readTab(manager, symbolTabReader, identifier);
                tabs.add(symbolTab);
            } catch (IOException | JsonParseException | InvalidIdentifierException e) {
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
        return allSymbols.stream();
    }

    @NotNull
    private SymbolTab readTab(ResourceManager manager, BufferedReader symbolTabReader, Identifier identifier) {
        JsonObject object = JsonHelper.deserialize(symbolTabReader);
        String icon = JsonHelper.getString(object, "icon");
        int order = JsonHelper.getInt(object, "order");
        SymbolTab.Type type = SymbolTab.Type.getOrDefault(JsonHelper.getString(object, "type", null), SymbolTab.Type.SYMBOLS);
        boolean searchBar = JsonHelper.getBoolean(object, "search_bar", false);
        JsonArray symbolFiles = JsonHelper.getArray(object, "symbols", new JsonArray(0));
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
                identifier = Identifier.of(primitive.getAsString());
                type = SymbolList.SplitType.CODEPOINT;
            } else if(symbolFile instanceof JsonObject object) {
                identifier = Identifier.of(JsonHelper.getString(object, "id"));
                type = SymbolList.SplitType.getOrDefault(JsonHelper.getString(object, "split", null), SymbolList.SplitType.CODEPOINT);
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
        try(BufferedReader symbolsReader = manager.openAsReader(SYMBOLS_FINDER.toResourcePath(identifier))) {
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
