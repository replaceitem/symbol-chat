package net.replaceitem.symbolchat.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.font.FontProcessorAccessor;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.config.ConfigProvider;
import net.replaceitem.symbolchat.font.FontProcessor;
import net.replaceitem.symbolchat.font.Fonts;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.UnicodeTableScreen;
import net.replaceitem.symbolchat.gui.widget.DropDownWidget;
import net.replaceitem.symbolchat.gui.widget.SymbolSuggestor;
import net.replaceitem.symbolchat.gui.widget.symbolButton.OpenSymbolPanelButtonWidget;
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

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements Consumer<String>, FontProcessorAccessor, SymbolSuggestable.TextFieldWidgetSymbolSuggestable {
    @Shadow protected TextFieldWidget chatField;

    private SymbolSelectionPanel symbolSelectionPanel;
    private DropDownWidget<FontProcessor> fontSelectionDropDown;
    private SymbolSuggestor symbolSuggestor;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void addSymbolChatComponents(CallbackInfo ci) {
        int symbolButtonX = this.width - 2 - SymbolButtonWidget.SYMBOL_SIZE;
        int symbolButtonY = this.height - 2 - SymbolButtonWidget.SYMBOL_SIZE;
        this.symbolSelectionPanel = new SymbolSelectionPanel(this,this.width-SymbolSelectionPanel.WIDTH -2,symbolButtonY-2-SymbolSelectionPanel.HEIGHT);
        this.addDrawableChild(symbolSelectionPanel);

        SymbolButtonWidget symbolButtonWidget = new OpenSymbolPanelButtonWidget(symbolButtonX, symbolButtonY, this.symbolSelectionPanel);
        this.addDrawableChild(symbolButtonWidget);

        GridWidget gridWidget = new GridWidget(0, 2);

        gridWidget.setColumnSpacing(2);
        
        if(!SymbolChat.config.getHideFontButton()) {
            this.fontSelectionDropDown = new DropDownWidget<>(0, 0, 180, 15, Fonts.fontProcessors, SymbolChat.selectedFont) {
                @Override
                public void onSelection(int index, FontProcessor element) {
                    SymbolChat.selectedFont = index;
                }
            };
            gridWidget.add(fontSelectionDropDown, 0, 2);
        }

        if(!SymbolChat.config.getHideSettingsButton()) {
            SymbolButtonWidget settingsButtonWidget = new SymbolButtonWidget(0, 0, 15, 15, "⚙") {
                @Override
                public boolean onClick(int button) {
                    if(SymbolChat.clothConfigEnabled) {
                        MinecraftClient.getInstance().setScreen(SymbolChat.config.getConfigScreen(ChatScreenMixin.this));
                        return true;
                    }
                    return false;
                }

                @Override
                protected MutableText getNarrationMessage() {
                    return Text.translatable("text.autoconfig.symbol-chat.title");
                }
            };
            settingsButtonWidget.setTooltip(Tooltip.of(Text.translatable(SymbolChat.clothConfigEnabled ? "text.autoconfig.symbol-chat.title" : "symbolchat.no_clothconfig")));
            gridWidget.add(settingsButtonWidget, 0, 1);
        }
        
        if(!SymbolChat.config.getHideTableButton()) {
            SymbolButtonWidget tableButtonWidget = new SymbolButtonWidget(0, 0, 15, 15, "⣿⣿") {
                @Override
                public boolean onClick(int button) {
                    if(SymbolChat.clothConfigEnabled) {
                        ChatScreenMixin.this.client.setScreen(new UnicodeTableScreen(ChatScreenMixin.this));
                        return true;
                    }
                    return false;
                }
            };
            gridWidget.add(tableButtonWidget, 0, 0);
        }
        
        gridWidget.refreshPositions();
        gridWidget.setX(SymbolChat.config.getHudPosition() == ConfigProvider.HudPosition.LEFT ? 2 : width - 2 - gridWidget.getWidth());
        gridWidget.refreshPositions();
        gridWidget.forEachChild(this::addDrawableChild);
        
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
        if(symbolSuggestor != null && this.symbolSuggestor.isVisible() && this.symbolSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
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
        if(fontSelectionDropDown != null && fontSelectionDropDown.mouseScrolled(mouseX,mouseY,amount)) {
            cir.setReturnValue(true);
        }
    }
    
    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"))
    private void updateSuggestions(String chatText, CallbackInfo ci) {
        if(symbolSuggestor != null) this.symbolSuggestor.refresh();
    }
    
    @Override
    public void accept(String s) {
        this.chatField.write(s);
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
    
    
    private static final Set<String> messageCommands = Set.of(
            "msg", "tell", "w",
            "say", "me",
            "teammsg", "tm"
    ).stream().map(s -> "/" + s + " ").collect(Collectors.toSet());
    
    private static boolean isSuggestingCommand(String text) {
        return messageCommands.contains(text);
    }
}
