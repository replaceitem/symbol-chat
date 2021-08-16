package symbolchat.symbolchat;

import net.fabricmc.loader.api.FabricLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Config {

    public static Properties CONFIG;

    public static int hud_color;
    public static int button_color;


    public static void loadConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("symbol-chat.properties");
        Properties properties = new Properties();
        if(!configPath.toFile().exists()) {
            try {
                Files.copy(SymbolChat.class.getResourceAsStream("/default_config.properties"),configPath);
            } catch (IOException e) {
                SymbolChat.LOGGER.error("Could not create config file");
                SymbolChat.LOGGER.error(e);
            }
        }
        try {
            properties.load(new FileInputStream(configPath.toFile()));
        } catch (IOException e) {
            SymbolChat.LOGGER.error("Could not read config file");
            SymbolChat.LOGGER.error(e);
        }

        CONFIG = properties;

        hud_color = getColor("hud_color",0x80000000);
        button_color = getColor("button_color",0xA0000000);
    }

    private static int getColor(String name,int defaultValue) {
        try {
            String buttonColorString = CONFIG.getProperty(name);
            if(buttonColorString==null) {
                SymbolChat.LOGGER.error(name + " property not found");
            } else {
                return Long.decode(buttonColorString).intValue();
            }
        } catch (NumberFormatException e) {
            SymbolChat.LOGGER.error("Could not parse " + name + " value");
            SymbolChat.LOGGER.error(e);
        }
        return defaultValue;
    }
}
