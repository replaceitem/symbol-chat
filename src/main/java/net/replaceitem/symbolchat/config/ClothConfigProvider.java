package net.replaceitem.symbolchat.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ActionResult;
import net.replaceitem.symbolchat.SymbolStorage;

import java.util.List;

public class ClothConfigProvider extends ConfigProvider {
    
    private final ClothConfig config;
    
    public ClothConfigProvider() {
        AutoConfig.register(ClothConfig.class, GsonConfigSerializer::new);
        this.config = AutoConfig.getConfigHolder(ClothConfig.class).getConfig();
        AutoConfig.getConfigHolder(ClothConfig.class).registerSaveListener((configHolder, clothConfig) -> {
            SymbolStorage.reloadCustomLists();
            return ActionResult.SUCCESS;
        });
    }
    
    @Override
    public int getHudColor() {
        return config.hud_color;
    }

    @Override
    public int getButtonColor() {
        return config.button_color;
    }

    @Override
    public int getButtonHoverColor() {
        return config.button_active_color;
    }

    @Override
    public boolean getHideFontButton() {
        return config.hide_font_button;
    }

    @Override
    public boolean getHideSettingsButton() {
        return config.hide_settings_button;
    }

    @Override
    public boolean getHideTableButton() {
        return config.hide_table_button;
    }

    @Override
    public int getSymbolPanelHeight() {
        return config.symbol_panel_height;
    }

    @Override
    public int getMaxSymbolSuggestions() {
        return config.max_symbol_suggestions;
    }

    @Override
    public SymbolTooltipMode getSymbolTooltipMode() {return config.symbol_tooltip_mode;}

    @Override
    public HudPosition getHudPosition() {
        return config.hud_position;
    }

    @Override
    public String getFavoriteSymbols() {
        // TODO - change name to favorite_symbols on next big update, or migrate somehow
        return config.custom_symbols;
    }

    @Override
    public List<String> getCustomKaomojis() {
        return config.custom_kaomojis;
    }

    @Override
    public Screen getConfigScreen(Screen parent) {
        return AutoConfig.getConfigScreen(ClothConfig.class, parent).get();
    }

    @Override
    public void addFavoriteSymbol(String symbols) {
        this.config.custom_symbols += symbols;
        AutoConfig.getConfigHolder(ClothConfig.class).save();
    }
}
