package symbolchat.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;

public class ClothConfigProvider extends ConfigProvider {
    
    private final ClothConfig config;
    
    public ClothConfigProvider() {
        AutoConfig.register(ClothConfig.class, GsonConfigSerializer::new);
        this.config = AutoConfig.getConfigHolder(ClothConfig.class).getConfig();
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
    public int getOutlineColor() {
        return config.outline_color;
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
    public HudPosition getHudPosition() {
        return config.hud_position;
    }

    @Override
    public String getCustomSymbols() {
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
}
