package net.replaceitem.symbolchat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.replaceitem.symbolchat.config.Config;
import net.replaceitem.symbolchat.resource.FontManager;
import net.replaceitem.symbolchat.resource.SymbolManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class SymbolChat implements ClientModInitializer {
    public static final String NAMESPACE = "symbol-chat";
    public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);

    public static boolean isPanelOpen = false;
    public static int selectedTab = 0;

    @Nullable
    private static Config config;

    @Nullable
    private static SymbolManager symbolManager;
    @Nullable
    private static FontManager fontManager;


    @Override
    public void onInitializeClient() {
        config = new Config();
        symbolManager = new SymbolManager();
        fontManager = new FontManager();
        config.load();
        config.favoriteSymbols.observe(favoriteSymbols -> symbolManager.onCustomSymbolsChanged(favoriteSymbols));
        config.customKaomojis.observe(customKaomojis -> symbolManager.onCustomKaomojisChanged(customKaomojis));
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(symbolManager);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(fontManager);
    }

    public static Config getConfig() {
        return Objects.requireNonNull(config);
    }

    public static SymbolManager getSymbolManager() {
        return Objects.requireNonNull(symbolManager);
    }

    public static FontManager getFontManager() {
        return Objects.requireNonNull(fontManager);
    }
}
