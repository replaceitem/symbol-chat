package net.replaceitem.symbolchat.resource;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.replaceitem.symbolchat.extensions.ScreenAccess;
import net.replaceitem.symbolchat.SymbolChat;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static net.replaceitem.symbolchat.SymbolChat.NAMESPACE;

public class FontManager implements SimpleSynchronousResourceReloadListener {
    
    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(NAMESPACE,"fonts");
    public static final FileToIdConverter FONT_FINDER = new FileToIdConverter("symbol_fonts", ".json");
    private FontProcessor normal;
    private List<FontProcessor> fonts = List.of();

    @Override
    public Identifier getFabricId() {
        return IDENTIFIER;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        normal = new FontProcessor(Identifier.fromNamespaceAndPath(NAMESPACE, "normal"), Function.identity(), Integer.MIN_VALUE, false);
        fonts = new ArrayList<>();
        fonts.add(normal);
        for (Map.Entry<Identifier, Resource> identifierResourceEntry : FONT_FINDER.listMatchingResources(manager).entrySet()) {
            Identifier identifier = FONT_FINDER.fileToId(identifierResourceEntry.getKey());
            Resource resource = identifierResourceEntry.getValue();
            try(BufferedReader reader = resource.openAsReader()) {
                FontProcessor font = readFont(reader, identifier);
                fonts.add(font);
            } catch (IOException | JsonParseException e) {
                SymbolChat.LOGGER.error("Could not load symbol tab {}", identifier, e);
            }
        }
        fonts.sort(Comparator.comparingInt(value -> value.order));
        fonts = Collections.unmodifiableList(fonts);
    }

    private FontProcessor readFont(BufferedReader reader, Identifier identifier) throws JsonParseException {
        JsonObject object = GsonHelper.parse(reader);
        String type = GsonHelper.getAsString(object, "type", "mapped");
        int order = GsonHelper.getAsInt(object, "order", Integer.MAX_VALUE);
        boolean reverseDirection = GsonHelper.getAsBoolean(object, "reverse_direction", false);
        if(type.equals("mapped")) return MappedFontProcessor.read(identifier, object, order, reverseDirection);
        throw new JsonSyntaxException("Invalid font type: " + type);
    }

    public FontProcessor getNormal() {
        return normal;
    }
    
    public List<FontProcessor> getFontProcessors() {
        return fonts;
    }
    
    @NotNull
    public FontProcessor getCurrentScreenFontProcessor() {
        Screen screen = Minecraft.getInstance().screen;
        if (!(screen instanceof ScreenAccess screenAccess)) {
            return normal;
        }
        return screenAccess.getFontProcessor();
    }
}
