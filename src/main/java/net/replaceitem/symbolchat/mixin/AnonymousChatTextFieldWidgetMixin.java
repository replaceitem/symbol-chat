package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.resource.FontProcessor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets="net/minecraft/client/gui/screen/ChatScreen$1")
public class AnonymousChatTextFieldWidgetMixin extends TextFieldWidget {

    public AnonymousChatTextFieldWidgetMixin(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    @Override
    public void write(String text) {
        FontProcessor fontProcessor = SymbolChat.fontManager.getCurrentScreenFontProcessor();
        text = fontProcessor.convertString(text);
        super.write(text);
        if(fontProcessor.isReverseDirection()) {
            int pos = this.getCursor()-text.length();
            this.setSelectionStart(pos);
            this.setSelectionEnd(pos);
        }
    }
}
