package symbolchat.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolchat.FontProcessor;
import symbolchat.FontProcessorAccessor;
import symbolchat.SymbolChat;
import symbolchat.SymbolInsertable;
import symbolchat.config.ConfigProvider;
import symbolchat.gui.SymbolSelectionPanel;
import symbolchat.gui.widget.DropDownWidget;
import symbolchat.gui.widget.FontSelectionDropDownWidget;
import symbolchat.gui.widget.symbolButton.OpenSymbolPanelButtonWidget;
import symbolchat.gui.widget.symbolButton.SettingsButtonWidget;
import symbolchat.gui.widget.symbolButton.SymbolButtonWidget;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements SymbolInsertable, FontProcessorAccessor {
    @Shadow protected TextFieldWidget chatField;

    private SymbolSelectionPanel symbolSelectionPanel;
    private DropDownWidget<FontProcessor> fontSelectionDropDown;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void addSymbolChatComponents(CallbackInfo ci) {
        int symbolButtonX = this.width-2-SymbolButtonWidget.symbolSize;
        int symbolButtonY = this.height-2-SymbolButtonWidget.symbolSize;
        this.symbolSelectionPanel = new SymbolSelectionPanel(this,this.width-SymbolSelectionPanel.width-2,symbolButtonY-2-SymbolSelectionPanel.height);
        SymbolButtonWidget symbolButtonWidget = new OpenSymbolPanelButtonWidget(this, symbolButtonX, symbolButtonY, this.symbolSelectionPanel);
        int hudX = SymbolChat.config.getHudPosition().getX(this.width);
        int hiddenOffset = (SymbolChat.config.getHideFontButton() && SymbolChat.config.getHudPosition().equals(ConfigProvider.HudPosition.RIGHT) ? 140 + 2 : 0);
        SettingsButtonWidget settingsButtonWidget = new SettingsButtonWidget(this, hudX + hiddenOffset, 2);
        this.fontSelectionDropDown = new FontSelectionDropDownWidget(hudX + 15 + 2, 2, 140, 15, FontProcessor.fontProcessors, SymbolChat.selectedFont);
        this.addDrawableChild(symbolSelectionPanel);
        this.addDrawableChild(symbolButtonWidget);
        this.addDrawableChild(fontSelectionDropDown);
        this.addDrawableChild(settingsButtonWidget);
    }

    @ModifyConstant(method = "init",constant = @Constant(intValue = 4, ordinal = 1),require = 1)
    private int changeTextBoxWidth(int original) {
        return original + SymbolButtonWidget.symbolSize + 8;
    }
    @ModifyConstant(method = "render",constant = @Constant(intValue = 2, ordinal = 1),require = 1)
    private int changeChatBoxFillWidth(int original) {
        return original + SymbolButtonWidget.symbolSize + 2;
    }

    @Inject(method = "mouseScrolled", at = @At(value = "HEAD"), cancellable = true)
    public void mouseScrolled(double mouseX, double mouseY, double amount, CallbackInfoReturnable<Boolean> cir) {
        if(symbolSelectionPanel.mouseScrolled(mouseX,mouseY,amount)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
    
    @Override
    public void insertSymbol(String symbol) {
        this.chatField.write(symbol);
    }

    @Override
    public FontProcessor getFontProcessor() {
        return this.fontSelectionDropDown.getSelection();
    }
}
