package net.replaceitem.symbolchat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.replaceitem.symbolchat.config.ClothConfigProvider;
import net.replaceitem.symbolchat.config.ConfigProvider;
import net.replaceitem.symbolchat.font.Fonts;
import net.replaceitem.symbolchat.resource.SymbolManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SymbolChat implements ClientModInitializer {
    public static Logger LOGGER;
    public static final String NAMESPACE = "symbol-chat";
    
    public static int selectedFont = 0;
    public static boolean isPanelOpen = false;
    public static int selectedTab = 0;
    
    public static ConfigProvider config;
    public static SymbolManager symbolManager;
    public static boolean clothConfigEnabled;


    @Override
    public void onInitializeClient() {
        LOGGER = LogManager.getLogger(NAMESPACE);
        clothConfigEnabled = FabricLoader.getInstance().isModLoaded("cloth-config2");
        config = clothConfigEnabled ? new ClothConfigProvider() : new ConfigProvider();
        symbolManager = new SymbolManager();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(symbolManager);
        Fonts.registerFonts();
    }
}
