package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.replaceitem.symbolchat.ScreenAccess;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.font.FontProcessor;
import net.replaceitem.symbolchat.font.Fonts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> implements Consumer<String>, SymbolSuggestable.TextFieldWidgetSymbolSuggestable {
    @Shadow private TextFieldWidget nameField;

    public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }

    @Inject(method = "setup", at = @At("RETURN"))
    private void onSetup(CallbackInfo ci) {
        ((ScreenAccess) this).addSymbolChatComponents();
    }
    
    
    @Redirect(method = "setup", at = @At(value = "NEW", target = "net/minecraft/client/gui/widget/TextFieldWidget"))
    private TextFieldWidget constructTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        return new TextFieldWidget(textRenderer, x, y, width, height, text) {
            @Override
            public void write(String text) {
                FontProcessor fontProcessor = FontProcessor.getCurrentScreenFontProcessor();
                text = fontProcessor.convertString(text);
                super.write(text);
                if(fontProcessor == Fonts.INVERSE) {
                    int pos = this.getCursor()-text.length();
                    this.setSelectionStart(pos);
                    this.setSelectionEnd(pos);
                }
            }
        };
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(((ScreenAccess) this).onKeyPressed(keyCode, scanCode, modifiers)) cir.setReturnValue(true);
        
    }

    @Inject(method = "onRenamed", at = @At("HEAD"))
    private void updateSuggestions(String chatText, CallbackInfo ci) {
        ((ScreenAccess) this).refreshSuggestions();
    }

    @Override
    public void accept(String s) {
        if(nameField == null) return;
        if(this.nameField.isActive())
            this.nameField.write(s);
    }

    @Override
    public TextFieldWidget getTextField() {
        return this.nameField;
    }
}
