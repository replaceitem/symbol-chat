package symbolchat.symbolchat;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SymbolChat implements ModInitializer {

    public static Logger LOGGER;

    @Override
    public void onInitialize() {
        LOGGER = LogManager.getLogger("SymbolChat");
    }
}
