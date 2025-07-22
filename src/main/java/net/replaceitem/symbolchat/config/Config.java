package net.replaceitem.symbolchat.config;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.screen.Screen;
import net.replaceitem.reconfigure.api.*;
import net.replaceitem.reconfigure.api.serializer.Serializers;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Config {
    private final net.replaceitem.reconfigure.api.Config config = net.replaceitem.reconfigure.api.Config.builder("symbol-chat")
            .serializer(Serializers.buildJson().preLoad(Config::updateConfig).build())
            .build();
    
    public final ConfigTab tab = config.createDefaultTab().build();
    
    private final Void general = tab.createHeadline("general");
    public final Property<Boolean> keepPanelOpen = tab.createBooleanProperty("keep_panel_open").asCheckbox().build();
    public final Property<Boolean> keepFontSelected = tab.createBooleanProperty("keep_font_selected").defaultValue(true).asCheckbox().build();
    public final Property<String> favoriteSymbols = tab.createStringProperty("favorite_symbols").asTextField().tooltip().build();
    public final Property<List<String>> customKaomojis = tab.createListProperty("custom_kaomojis").asChipList().build();
    public final Property<Integer> maxSymbolSuggestions = tab.createIntegerProperty("max_symbol_suggestions").defaultValue(5).range(0, 50).asSlider().build();
    public final Property<SymbolTooltipMode> symbolTooltipMode = tab.createEnumProperty("symbol_tooltip_mode", SymbolTooltipMode.class).defaultValue(SymbolTooltipMode.DELAYED).asCyclingButton().tooltip().build();
    public final Property<String> chatSuggestionRegex = tab.createStringProperty("chat_suggestion_regex").defaultValue("^(/(msg|tell|w|say|me|teammsg|tm) |[^/]).*").asTextField().tooltip().build();
    public final Property<String> fontConversionRegex = tab.createStringProperty("font_conversion_regex").defaultValue("^[^/].*").asTextField().tooltip().build();

    private final Void hudLayoutHeadline = tab.createHeadline("hud_layout");
    public final Property<Boolean> hideFontButton = tab.createBooleanProperty("hide_font_button").asCheckbox().build();
    public final Property<Boolean> hideSettingsButton = tab.createBooleanProperty("hide_settings_button").asCheckbox().tooltip().build();
    public final Property<Boolean> hideTableButton = tab.createBooleanProperty("hide_table_button").asCheckbox().tooltip().build();
    public final Property<Integer> symbolPanelHeight = tab.createIntegerProperty("symbol_panel_height").defaultValue(200).range(100, 500).asSlider().build();
    public final Property<HudCorner> hudPosition = tab.createEnumProperty("hud_position", HudCorner.class).defaultValue(HudCorner.TOP_RIGHT).asCyclingButton().tooltip().build();
    public final Property<HudCorner> symbolButtonPosition = tab.createEnumProperty("symbol_button_position", HudCorner.class).defaultValue(HudCorner.BOTTOM_RIGHT).asCyclingButton().tooltip().build();

    private final Void colorsHeadline = tab.createHeadline("colors");
    public final Property<Integer> hudColor = tab.createIntegerProperty("hud_color").defaultValue(0x80000000).asColorPicker().build();
    public final Property<Integer> buttonColor = tab.createIntegerProperty("button_color").defaultValue(0xA0000000).asColorPicker().build();
    public final Property<Integer> buttonActiveColor = tab.createIntegerProperty("button_active_color").defaultValue(0xA0202020).asColorPicker().build();
    public final Property<Integer> favoriteColor = tab.createIntegerProperty("favorite_color").defaultValue(0xFFFFFF00).asColorPicker().build();
    public final Property<Integer> buttonTextColor = tab.createIntegerProperty("button_text_color").defaultValue(0xFFA0A0A0).asColorPicker().build();
    public final Property<Integer> buttonTextHoverColor = tab.createIntegerProperty("button_text_hover_color").defaultValue(0xFFFFFFFF).asColorPicker().build();

    private final Void unicodeTableHeadline = tab.createHeadline("unicode_table");
    public final Property<Boolean> unicodeTableShowBlocks = tab.createBooleanProperty("unicode_table_show_blocks").defaultValue(false).asCheckbox().build();
    public final Property<Boolean> unicodeTableTextShadow = tab.createBooleanProperty("unicode_table_text_shadow").defaultValue(true).asCheckbox().build();
    public final Property<Boolean> unicodeTableHideMissingGlyphs = tab.createBooleanProperty("unicode_table_missing_glyphs").defaultValue(false).asCheckbox().build();

    public final Property<String> selectedFont = tab.createStringProperty("selected_font").defaultValue("").buildWithoutWidget();

    public final Bindable<Pattern> chatSuggestionPattern = chatSuggestionRegex.map(Pattern::compile);
    public final Bindable<Pattern> fontConversionPattern = fontConversionRegex.map(Pattern::compile);

    public void save() {
        this.config.save();
    }

    public void scheduleSave() {
        this.config.scheduleSave(Duration.ofSeconds(5));
    }

    public void load() {
        this.config.load();
    }

    public Screen createScreen(Screen screen) {
        return this.config.createScreen(screen);
    }

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

    public enum HudSide {
        LEFT,
        RIGHT
    }
    public enum HudVerticalSide {
        TOP, BOTTOM
    }
    public enum HudCorner {
        TOP_LEFT(HudVerticalSide.TOP, HudSide.LEFT),
        TOP_RIGHT(HudVerticalSide.TOP, HudSide.RIGHT),
        BOTTOM_RIGHT(HudVerticalSide.BOTTOM, HudSide.RIGHT),
        BOTTOM_LEFT(HudVerticalSide.BOTTOM, HudSide.LEFT);

        private final HudVerticalSide vertical;
        private final HudSide horizontal;

        HudCorner(HudVerticalSide vertical, HudSide horizontal) {
            this.horizontal = horizontal;
            this.vertical = vertical;
        }

        public HudVerticalSide getVertical() {
            return vertical;
        }

        public HudSide getHorizontal() {
            return horizontal;
        }
    }
    
    private static void updateConfig(JsonObject object) {
        if(object.has("custom_symbols") && !object.has("favorite_symbols")) {
            object.add("favorite_symbols", object.get("custom_symbols"));
        }
    }
}
