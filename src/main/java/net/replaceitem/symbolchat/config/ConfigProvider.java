package net.replaceitem.symbolchat.config;

import net.minecraft.client.gui.screen.Screen;

import java.util.List;
import java.util.stream.IntStream;

public class ConfigProvider {
    public int getHudColor() {return 0x80000000;}
    public int getButtonColor() {return 0xA0000000;}
    public int getButtonHoverColor() {return 0xA0303030;}
    public int getFavoriteColor() {return 0xFFFFFF00;}
    public boolean getHideFontButton() {return false;}
    public boolean getHideSettingsButton() {return false;}
    public boolean getHideTableButton() {return false;}
    public int getSymbolPanelHeight() {return 200;}
    public int getMaxSymbolSuggestions() {return 5;}
    public SymbolTooltipMode getSymbolTooltipMode() {return SymbolTooltipMode.DELAYED;}
    public HudPosition getHudPosition() {return HudPosition.RIGHT;}
    public boolean getKeepPanelOpen() {return false;}
    public String getChatSuggestionRegex() {return "^(/(msg|tell|w|say|me|teammsg|tm) |[^/]).*";}
    public String getFavoriteSymbols() {return "";}

    public List<String> getCustomKaomojis() {return List.of();}

    public Screen getConfigScreen(Screen parent) {
        return null;
    }
    
    
    
    public void addFavorite(String symbols) {}
    public void removeFavorite(String symbol) {}
    public void toggleFavorite(IntStream codepoints) {}


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
