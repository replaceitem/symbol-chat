package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.FontProcessor;
import net.replaceitem.symbolchat.FontProcessorAccessor;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.config.ConfigProvider;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.widget.DropDownWidget;
import net.replaceitem.symbolchat.gui.widget.FontSelectionDropDownWidget;
import net.replaceitem.symbolchat.gui.widget.SymbolSuggestor;
import net.replaceitem.symbolchat.gui.widget.symbolButton.OpenSymbolPanelButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SettingsButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements SymbolInsertable, FontProcessorAccessor, SymbolSuggestable.TextFieldWidgetSymbolSuggestable {
    @Shadow protected TextFieldWidget chatField;

    private SymbolSelectionPanel symbolSelectionPanel;
    private DropDownWidget<FontProcessor> fontSelectionDropDown;
    private SymbolSuggestor symbolSuggestor;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void addSymbolChatComponents(CallbackInfo ci) {
        int symbolButtonX = this.width-2- SymbolButtonWidget.SYMBOL_SIZE;
        int symbolButtonY = this.height-2-SymbolButtonWidget.SYMBOL_SIZE;
        this.symbolSelectionPanel = new SymbolSelectionPanel(this,this.width-SymbolSelectionPanel.WIDTH -2,symbolButtonY-2-SymbolSelectionPanel.HEIGHT);
        this.addDrawableChild(symbolSelectionPanel);

        SymbolButtonWidget symbolButtonWidget = new OpenSymbolPanelButtonWidget(symbolButtonX, symbolButtonY, this.symbolSelectionPanel);
        this.addDrawableChild(symbolButtonWidget);

        int hudX = SymbolChat.config.getHudPosition().getX(this.width);
        int hiddenOffset = (SymbolChat.config.getHideFontButton() && SymbolChat.config.getHudPosition().equals(ConfigProvider.HudPosition.RIGHT) ? 140 + 2 : 0);

        this.fontSelectionDropDown = new FontSelectionDropDownWidget(hudX + ((SymbolChat.config.getHideSettingsButton() && SymbolChat.config.getHudPosition().equals(ConfigProvider.HudPosition.LEFT)) ? 0 : 15) + 2, 2, 140, 15, FontProcessor.fontProcessors, SymbolChat.selectedFont);
        this.addDrawableChild(fontSelectionDropDown);

        if(!SymbolChat.config.getHideSettingsButton()) {
            SettingsButtonWidget settingsButtonWidget = new SettingsButtonWidget(this, hudX + hiddenOffset, 2);
            this.addDrawableChild(settingsButtonWidget);
        }
        
        this.symbolSuggestor = new SymbolSuggestor(this, this::replaceSuggestion, this);
        this.addDrawableChild(symbolSuggestor);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if(symbolSelectionPanel != null && symbolSelectionPanel.mouseClicked(mouseX, mouseY, button)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(symbolSuggestor != null && this.symbolSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
        }
        if(symbolSelectionPanel != null && this.symbolSelectionPanel.keyPressed(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(symbolSelectionPanel != null && this.symbolSelectionPanel.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        if(!(focused instanceof TextFieldWidget)) return;
        super.setFocused(focused);
    }

    @ModifyConstant(method = "init",constant = @Constant(intValue = 4, ordinal = 1),require = 1)
    private int changeTextBoxWidth(int original) {
        return original + SymbolButtonWidget.SYMBOL_SIZE + 8;
    }
    @ModifyConstant(method = "render",constant = @Constant(intValue = 2, ordinal = 1),require = 1)
    private int changeChatBoxFillWidth(int original) {
        return original + SymbolButtonWidget.SYMBOL_SIZE + 2;
    }

    @Inject(method = "mouseScrolled", at = @At(value = "HEAD"), cancellable = true)
    public void mouseScrolled(double mouseX, double mouseY, double amount, CallbackInfoReturnable<Boolean> cir) {
        if(symbolSelectionPanel != null && symbolSelectionPanel.mouseScrolled(mouseX,mouseY,amount)) {
            cir.setReturnValue(true);
        }
    }
    
    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"))
    private void updateSuggestions(String chatText, CallbackInfo ci) {
        if(symbolSuggestor != null) this.symbolSuggestor.refresh();
    }
    
    @Override
    public void insertSymbol(String symbol) {
        this.chatField.write(symbol);
    }

    @Override
    public FontProcessor getFontProcessor() {
        if(this.fontSelectionDropDown == null) return null;
        return this.fontSelectionDropDown.getSelection();
    }

    @Override
    public boolean disabled() {
        String text = this.chatField.getText();
        return text.startsWith("/") && !(isSuggestingCommand(text));
    }

    @Override
    public TextFieldWidget getTextField() {
        return chatField;
    }
    
    
    private static final List<String> messageCommands = List.of(
            "msg", "tell", "w",
            "say", "me",
            "teammsg", "tm"
    );
    private static boolean isSuggestingCommand(String text) {
        return messageCommands.stream().anyMatch(s -> text.startsWith("/" + s + " "));
    }
}
