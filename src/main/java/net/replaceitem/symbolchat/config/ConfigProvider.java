package net.replaceitem.symbolchat.config;

import net.minecraft.client.gui.screen.Screen;
import net.replaceitem.symbolchat.SymbolChat;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

public class ConfigProvider {
    public int getHudColor() {return 0x80000000;}
    public int getButtonColor() {return 0xA0000000;}
    public int getButtonHoverColor() {return 0xA0303030;}
    public int getFavoriteColor() {return 0xFFFFFF00;}
    public int getButtonTextColor() {return 0xFFA0A0A0;}
    public int getButtonTextHoverColor() {return 0xFFFFFFFF;}
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

    public void onConfigChange() {
        try {
            this.chatSuggestionPattern = Pattern.compile(getChatSuggestionRegex());
        } catch (PatternSyntaxException e) {
            this.chatSuggestionPattern = Pattern.compile(".*");
            SymbolChat.LOGGER.error("Invalid regex provided as the Chat Suggestion Regex", e);
        }
        SymbolChat.symbolManager.onConfigReload(this);
    }
    
    public Pattern chatSuggestionPattern;

    public Pattern getChatSuggestionPattern() {
        return chatSuggestionPattern;
    }
    
    public void addFavorite(String symbols) {}
    public void removeFavorite(String symbol) {}
    public void toggleFavorite(Stream<String> symbols) {}


    @SuppressWarnings("unused")
    public enum SymbolTooltipMode {
        OFF(Duration.ofSeconds(Integer.MAX_VALUE)),
        ON(Duration.ZERO),
        DELAYED(Duration.ofMillis(500));

        private final Duration delay;

        public Duration getDelay() {
            return delay;
        }

        SymbolTooltipMode(Duration delay) {
            this.delay = delay;
        }
    }

    public enum HudPosition {
        LEFT,
        RIGHT
    }
}
