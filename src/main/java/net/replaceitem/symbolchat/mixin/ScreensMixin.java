package net.replaceitem.symbolchat.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.config.ConfigProvider;
import net.replaceitem.symbolchat.font.FontProcessor;
import net.replaceitem.symbolchat.ScreenAccess;
import net.replaceitem.symbolchat.font.Fonts;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.UnicodeTableScreen;
import net.replaceitem.symbolchat.gui.widget.DropDownWidget;
import net.replaceitem.symbolchat.gui.widget.SymbolSuggestor;
import net.replaceitem.symbolchat.gui.widget.symbolButton.OpenSymbolPanelButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin({ChatScreen.class, BookEditScreen.class, AnvilScreen.class, AbstractSignEditScreen.class})
public class ScreensMixin extends Screen implements ScreenAccess {

    protected ScreensMixin(Text title) {
        super(title);
    }
    
    @Nullable
    private SymbolSelectionPanel symbolSelectionPanel;
    @Nullable
    private SymbolButtonWidget symbolButtonWidget;
    @Nullable
    private SymbolSuggestor symbolSuggestor;
    @Nullable
    private DropDownWidget<FontProcessor> fontSelectionDropDown;

    public void addSymbolChatComponents() {
        int symbolButtonX = this.width - 2 - SymbolButtonWidget.SYMBOL_SIZE;
        int symbolButtonY = this.height - 2 - SymbolButtonWidget.SYMBOL_SIZE;
        this.symbolSelectionPanel = new SymbolSelectionPanel(this::insertSymbol,this.width-SymbolSelectionPanel.WIDTH - 2,symbolButtonY-2-SymbolSelectionPanel.HEIGHT);
        this.addDrawableChild(symbolSelectionPanel);

        symbolButtonWidget = new OpenSymbolPanelButtonWidget(symbolButtonX, symbolButtonY, this.symbolSelectionPanel);
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
                        MinecraftClient.getInstance().setScreen(SymbolChat.config.getConfigScreen(ScreensMixin.this));
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
                    if(ScreensMixin.this.client != null) ScreensMixin.this.client.setScreen(new UnicodeTableScreen(ScreensMixin.this));
                    return true;
                }
            };
            gridWidget.add(tableButtonWidget, 0, 0);
        }

        gridWidget.refreshPositions();
        gridWidget.setX(SymbolChat.config.getHudPosition() == ConfigProvider.HudPosition.LEFT ? 2 : width - 2 - gridWidget.getWidth());
        gridWidget.refreshPositions();
        gridWidget.forEachChild(this::addDrawableChild);

        this.symbolSuggestor = new SymbolSuggestor(this, this::onSymbolReplaced, (SymbolSuggestable) this);
        this.addDrawableChild(symbolSuggestor);
    }

    @Override
    public FontProcessor getFontProcessor() {
        if(this.fontSelectionDropDown == null) return null;
        return this.fontSelectionDropDown.getSelection();
    }

    @Override
    public void refreshSuggestions() {
        if(this.symbolSuggestor != null) {
            this.symbolSuggestor.refresh();
            if(this.symbolSuggestor.isFocused()) this.setFocused(this.symbolSuggestor);
        }
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        return this.symbolSelectionPanel != null && this.symbolSelectionPanel.keyPressed(keyCode, scanCode, modifiers) || this.symbolSuggestor != null && this.symbolSuggestor.isVisible() && this.symbolSuggestor.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        return this.symbolSelectionPanel != null && this.symbolSelectionPanel.charTyped(chr, modifiers);
    }

    @SuppressWarnings("unchecked")
    public void insertSymbol(String s) {
        ((Consumer<String>) this).accept(s);
    }

    private void onSymbolReplaced(String s) {
        ((SymbolSuggestable) this).replaceSuggestion(s);
    }
}
