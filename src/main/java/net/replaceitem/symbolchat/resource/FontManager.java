package net.replaceitem.symbolchat.resource;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.replaceitem.symbolchat.ScreenAccess;
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
    
    public static final Identifier IDENTIFIER = Identifier.of(NAMESPACE,"fonts");
    public static final ResourceFinder FONT_FINDER = new ResourceFinder("symbol_fonts", ".json");
    private FontProcessor normal;
    private List<FontProcessor> fonts = List.of();

    @Override
    public Identifier getFabricId() {
        return IDENTIFIER;
    }

    @Override
    public void reload(ResourceManager manager) {
        normal = new FontProcessor(Identifier.of(NAMESPACE, "normal"), Function.identity(), Integer.MIN_VALUE, false);
        fonts = new ArrayList<>();
        fonts.add(normal);
        for (Map.Entry<Identifier, Resource> identifierResourceEntry : FONT_FINDER.findResources(manager).entrySet()) {
            Identifier identifier = FONT_FINDER.toResourceId(identifierResourceEntry.getKey());
            Resource resource = identifierResourceEntry.getValue();
            try(BufferedReader reader = resource.getReader()) {
                FontProcessor font = readFont(reader, identifier);
                fonts.add(font);
            } catch (IOException | JsonParseException e) {
                SymbolChat.LOGGER.error("Could not load symbol tab " + identifier, e);
            }
        }
        fonts.sort(Comparator.comparingInt(value -> value.order));
        fonts = Collections.unmodifiableList(fonts);
    }

    private FontProcessor readFont(BufferedReader reader, Identifier identifier) throws JsonParseException {
        JsonObject object = JsonHelper.deserialize(reader);
        String type = JsonHelper.getString(object, "type", "mapped");
        int order = JsonHelper.getInt(object, "order", Integer.MAX_VALUE);
        boolean reverseDirection = JsonHelper.getBoolean(object, "reverse_direction", false);
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
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (!(screen instanceof ScreenAccess screenAccess)) {
            return normal;
        }
        return screenAccess.getFontProcessor();
    }
}
