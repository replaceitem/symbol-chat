package symbolchat.symbolchat;

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;


public class SymbolChat implements ClientModInitializer {
    public static Logger LOGGER;


    @Override
    public void onInitializeClient() {
        LOGGER = LogManager.getLogger("SymbolChat");
        SymbolStorage.loadLists();
        Config.loadConfig();
    }
}
