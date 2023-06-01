package net.replaceitem.symbolchat.config;

import net.minecraft.client.gui.screen.Screen;

import java.util.List;

public class ConfigProvider {
    public int getHudColor() {return 0x80000000;}
    public int getButtonColor() {return 0xA0000000;}
    public int getButtonHoverColor() {return 0xA0303030;}
    public boolean getHideFontButton() {return false;}
    public boolean getHideSettingsButton() {return false;}
    public boolean getHideTableButton() {return false;}
    public int getMaxSymbolSuggestions() {return 5;}
    public SymbolTooltipMode getSymbolTooltipMode() {return SymbolTooltipMode.DELAYED;}
    public HudPosition getHudPosition() {return HudPosition.RIGHT;}
    public String getCustomSymbols() {return "";}

    public List<String> getCustomKaomojis() {return List.of();}

    public Screen getConfigScreen(Screen parent) {
        return null;
    }
    
    
    
    public void addCustomSymbol(String symbols) {}


    @SuppressWarnings("unused")
    public enum SymbolTooltipMode {
        OFF(Integer.MAX_VALUE),
        ON(0),
        DELAYED(500);

        public final int delay;

        SymbolTooltipMode(int delay) {
            this.delay = delay;
        }
    }

    public enum HudPosition {
        LEFT,
        RIGHT
    }
}
