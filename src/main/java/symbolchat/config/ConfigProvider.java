package symbolchat.config;

import net.minecraft.client.gui.screen.Screen;

public class ConfigProvider {
    public int getHudColor() {
        return 0x80000000;
    }
    
    public int getButtonColor() {
        return 0xA0000000;
    }
    
    public String getCustomSymbols() {
        return "";
    }

    public Screen getConfigScreen(Screen parent) {
        return null;
    }
}
