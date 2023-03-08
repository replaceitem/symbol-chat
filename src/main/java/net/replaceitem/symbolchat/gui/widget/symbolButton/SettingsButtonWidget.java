package net.replaceitem.symbolchat.gui.widget.symbolButton;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolChat;

public class SettingsButtonWidget extends SymbolButtonWidget implements Drawable, Element, Narratable {


    protected Screen screen;
    
    public SettingsButtonWidget(Screen screen, int x, int y) {
        super(x, y, "⚙");
        this.screen = screen;
        this.width = 15;
        this.height = 15;
        this.setTooltip(Tooltip.of(Text.translatable(SymbolChat.clothConfigEnabled ? "text.autoconfig.symbol-chat.title" : "symbolchat.no_clothconfig")));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if(SymbolChat.clothConfigEnabled) {
            MinecraftClient.getInstance().setScreen(SymbolChat.config.getConfigScreen(this.screen));
        }
    }

    @Override
    protected MutableText getNarrationMessage() {
        return Text.translatable("text.autoconfig.symbol-chat.title");
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
        builder.put(NarrationPart.HINT, "Symbol chat settings");
    }
}
