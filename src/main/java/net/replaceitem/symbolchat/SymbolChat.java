package net.replaceitem.symbolchat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.replaceitem.symbolchat.config.ClothConfigProvider;
import net.replaceitem.symbolchat.config.ConfigProvider;
import net.replaceitem.symbolchat.font.Fonts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SymbolChat implements ClientModInitializer {
    public static Logger LOGGER;
    public static int selectedFont = 0;
    public static ConfigProvider config;
    public static boolean clothConfigEnabled;


    @Override
    public void onInitializeClient() {
        LOGGER = LogManager.getLogger("symbol-chat");
        clothConfigEnabled = FabricLoader.getInstance().isModLoaded("cloth-config2");
        config = clothConfigEnabled ? new ClothConfigProvider() : new ConfigProvider();
        Fonts.registerFonts();
        SymbolStorage.load();
    }
}
