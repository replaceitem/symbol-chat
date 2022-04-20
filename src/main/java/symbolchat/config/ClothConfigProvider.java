package symbolchat.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.gui.screen.Screen;

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
    public String getCustomSymbols() {
        return config.custom_symbols;
    }

    @Override
    public Screen getConfigScreen(Screen parent) {
        return AutoConfig.getConfigScreen(ClothConfig.class, parent).get();
    }
}
