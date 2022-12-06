package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.FontProcessor;
import net.replaceitem.symbolchat.config.ConfigProvider;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.widget.FontSelectionDropDownWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.replaceitem.symbolchat.FontProcessorAccessor;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.gui.widget.DropDownWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.OpenSymbolPanelButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SettingsButtonWidget;

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
        int symbolButtonX = this.width-2- SymbolButtonWidget.symbolSize;
        int symbolButtonY = this.height-2-SymbolButtonWidget.symbolSize;
        this.symbolSelectionPanel = new SymbolSelectionPanel(this,this.width-SymbolSelectionPanel.WIDTH -2,symbolButtonY-2-SymbolSelectionPanel.HEIGHT);
        this.addDrawableChild(symbolSelectionPanel);

        SymbolButtonWidget symbolButtonWidget = new OpenSymbolPanelButtonWidget(this, symbolButtonX, symbolButtonY, this.symbolSelectionPanel);
        this.addDrawableChild(symbolButtonWidget);

        int hudX = SymbolChat.config.getHudPosition().getX(this.width);
        int hiddenOffset = (SymbolChat.config.getHideFontButton() && SymbolChat.config.getHudPosition().equals(ConfigProvider.HudPosition.RIGHT) ? 140 + 2 : 0);

        this.fontSelectionDropDown = new FontSelectionDropDownWidget(hudX + ((SymbolChat.config.getHideSettingsButton() && SymbolChat.config.getHudPosition().equals(ConfigProvider.HudPosition.LEFT)) ? 0 : 15) + 2, 2, 140, 15, FontProcessor.fontProcessors, SymbolChat.selectedFont);
        this.addDrawableChild(fontSelectionDropDown);

        if(!SymbolChat.config.getHideSettingsButton()) {
            SettingsButtonWidget settingsButtonWidget = new SettingsButtonWidget(this, hudX + hiddenOffset, 2);
            this.addDrawableChild(settingsButtonWidget);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(symbolSelectionPanel.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(this.symbolSelectionPanel.keyPressed(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(this.symbolSelectionPanel.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
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
