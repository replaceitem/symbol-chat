package net.replaceitem.symbolchat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.replaceitem.symbolchat.config.Reconfig;
import net.replaceitem.symbolchat.resource.FontManager;
import net.replaceitem.symbolchat.resource.SymbolManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SymbolChat implements ClientModInitializer {
    public static Logger LOGGER;
    public static final String NAMESPACE = "symbol-chat";
    
    public static int selectedFont = 0;
    public static boolean isPanelOpen = false;
    public static int selectedTab = 0;
    
    public static SymbolManager symbolManager;
    public static FontManager fontManager;
    
    public static Reconfig reconfig = new Reconfig();


    @Override
    public void onInitializeClient() {
        LOGGER = LogManager.getLogger(NAMESPACE);
        symbolManager = new SymbolManager();
        fontManager = new FontManager();
        reconfig.config.load();
        reconfig.favoriteSymbols.observe(favoriteSymbols -> symbolManager.onCustomSymbolsChanged(favoriteSymbols));
        reconfig.customKaomojis.observe(customKaomojis -> symbolManager.onCustomKaomojisChanged(customKaomojis));
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(symbolManager);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(fontManager);
    }
}
