package symbolchat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import symbolchat.config.ClothConfigProvider;
import symbolchat.config.ConfigProvider;

public class SymbolChat implements ClientModInitializer {
    public static Logger LOGGER;
    public static int selectedFont = 0;
    public static ConfigProvider config;


    @Override
    public void onInitializeClient() {
        LOGGER = LogManager.getLogger("symbol-chat");
        if(FabricLoader.getInstance().isModLoaded("cloth-config")) {
            config = new ClothConfigProvider();
        } else {
            config = new ConfigProvider();
        }
        FontProcessor.registerFontProcessors();
        SymbolStorage.loadLists();
    }
}
