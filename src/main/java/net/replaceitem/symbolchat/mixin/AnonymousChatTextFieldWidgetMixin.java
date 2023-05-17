package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.font.FontProcessor;
import net.replaceitem.symbolchat.font.Fonts;
import org.spongepowered.asm.mixin.Mixin;
import net.replaceitem.symbolchat.font.FontProcessorAccessor;

@Mixin(targets="net/minecraft/client/gui/screen/ChatScreen$1")
public class AnonymousChatTextFieldWidgetMixin extends TextFieldWidget {

    public AnonymousChatTextFieldWidgetMixin(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    @Override
    public void write(String text) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (!(screen instanceof FontProcessorAccessor fontProcessorAccessor)) {
            super.write(text);
            return;
        }
        FontProcessor processor = fontProcessorAccessor.getFontProcessor();
        if(processor != null) text = processor.convertString(text);
        super.write(text);
        if(processor == Fonts.INVERSE) {
            int pos = this.getCursor()-text.length();
            this.setSelectionStart(pos);
            this.setSelectionEnd(pos);
        }
    }
}
