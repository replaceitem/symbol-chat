package symbolchat.config;

import net.minecraft.client.gui.screen.Screen;

import java.util.List;

public class ConfigProvider {
    public int getHudColor() {
        return 0x80000000;
    }

    public int getButtonColor() {
        return 0xA0000000;
    }

    public int getOutlineColor() {
        return 0xFFFFFFFF;
    }

    public boolean getHideFontButton() {
        return false;
    }

    public HudPosition getHudPosition() {
        return HudPosition.RIGHT;
    }

    public String getCustomSymbols() {
        return "";
    }
    
    public List<String> getCustomKaomojis() {
        return List.of();
    }

    public Screen getConfigScreen(Screen parent) {
        return null;
    }

    public enum HudPosition {
        LEFT{
            public int getX(int width) {
                return 2;
            }
        },RIGHT{
            public int getX(int width) {
                return width - 140 - 2 -15 -2;
            }
        };

        public abstract int getX(int width);
    }
}
