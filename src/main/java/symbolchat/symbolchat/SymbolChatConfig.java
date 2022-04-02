package symbolchat.symbolchat;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@SuppressWarnings("unused")
@Config(name = "symbol-chat")
public class SymbolChatConfig implements ConfigData {
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int hud_color = 0x80000000;

    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int button_color = 0xA0000000;
    
    @ConfigEntry.Gui.Tooltip
    public String custom_symbols = "";
}
