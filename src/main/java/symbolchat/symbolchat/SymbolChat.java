package symbolchat.symbolchat;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SymbolChat implements ClientModInitializer {
    public static Logger LOGGER;
    public static int selectedFont = 0;
    public static SymbolChatConfig config;


    @Override
    public void onInitializeClient() {
        LOGGER = LogManager.getLogger("symbol-chat");
        AutoConfig.register(SymbolChatConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(SymbolChatConfig.class).getConfig();
        FontProcessor.registerFontProcessors();
        SymbolStorage.loadLists();
    }
}
