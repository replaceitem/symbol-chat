package symbolchat.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.List;

@SuppressWarnings("unused")
@Config(name = "symbol-chat")
public class ClothConfig implements ConfigData {
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int hud_color = 0x80000000;

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int button_color = 0xA0000000;

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int outline_color = 0xFFFFFFFF;
    
    public boolean hide_font_button = false;
    
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public ConfigProvider.HudPosition hud_position = ConfigProvider.HudPosition.RIGHT;
    
    @ConfigEntry.Gui.Tooltip
    public String custom_symbols = "";
    
    public List<String> custom_kaomojis = List.of();
}
