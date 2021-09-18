package symbolchat.symbolchat;

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;


public class SymbolChat implements ClientModInitializer {
    public static Logger LOGGER;
    public static int selectedFont = 0;


    @Override
    public void onInitializeClient() {
        LOGGER = LogManager.getLogger("SymbolChat");
        FontProcessor.registerFontProcessors();
        SymbolStorage.loadLists();
        Config.loadConfig();
    }
}
