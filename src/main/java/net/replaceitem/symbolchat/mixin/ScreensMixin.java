package net.replaceitem.symbolchat.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.ScreenAccess;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.config.ConfigProvider;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.UnicodeTableScreen;
import net.replaceitem.symbolchat.gui.widget.DropDownWidget;
import net.replaceitem.symbolchat.gui.widget.SymbolSuggestor;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import net.replaceitem.symbolchat.resource.FontProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Consumer;

@Mixin({ChatScreen.class, BookEditScreen.class, AnvilScreen.class, AbstractSignEditScreen.class})
public class ScreensMixin extends Screen implements ScreenAccess {

    protected ScreensMixin(Text title) {
        super(title);
    }
    
    @Unique
    @Nullable
    private SymbolSelectionPanel symbolSelectionPanel;
    @Unique
    @Nullable
    private SymbolButtonWidget symbolButtonWidget;
    @Unique
    @Nullable
    private SymbolSuggestor symbolSuggestor;
    @Unique
    @Nullable
    private DropDownWidget<FontProcessor> fontSelectionDropDown;
    @Unique
    @Nullable
    private SymbolButtonWidget settingsButtonWidget;
    @Unique
    @Nullable
    private SymbolButtonWidget tableButtonWidget;


    public void addSymbolChatComponents() {
        int symbolButtonX = this.width - 2 - SymbolButtonWidget.SYMBOL_SIZE;
        int symbolButtonY = this.height - 2 - SymbolButtonWidget.SYMBOL_SIZE;
        int panelHeight = SymbolChat.config.getSymbolPanelHeight();
        int panelWidth = SymbolSelectionPanel.getWidthForTabs(SymbolChat.symbolManager.getTabs().size());
        this.symbolSelectionPanel = new SymbolSelectionPanel(this::insertSymbol, this.width-panelWidth - 2,symbolButtonY-2-panelHeight, panelHeight);
        this.addDrawableChild(symbolSelectionPanel);

        symbolButtonWidget = new SymbolButtonWidget(symbolButtonX, symbolButtonY, "☺") {
            @Override
            public boolean onClick(int button) {
                symbolSelectionPanel.toggleVisible();
                return true;
            }

            @Override
            protected boolean shouldDrawOutline() {
                return symbolSelectionPanel.isVisible();
            }
        };
        this.addDrawableChild(symbolButtonWidget);

        GridWidget gridWidget = new GridWidget(0, 2);
        gridWidget.setColumnSpacing(2);
        GridWidget.Adder adder = gridWidget.createAdder(Integer.MAX_VALUE);

        if(!SymbolChat.config.getHideFontButton()) {
            this.fontSelectionDropDown = new DropDownWidget<>(0, 0, 180, 15, SymbolChat.fontManager.getFontProcessors(), SymbolChat.selectedFont) {
                @Override
                public void onSelection(int index, FontProcessor element) {
                    SymbolChat.selectedFont = index;
                }
            };
            adder.add(fontSelectionDropDown);
        }

        if(!SymbolChat.config.getHideSettingsButton()) {
            settingsButtonWidget = new SymbolButtonWidget(0, 0, 15, 15, "⚙") {
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
            adder.add(settingsButtonWidget);
        }

        if(!SymbolChat.config.getHideTableButton()) {
            tableButtonWidget = new SymbolButtonWidget(0, 0, 15, 15, "⣿⣿") {
                @Override
                public boolean onClick(int button) {
                    if(ScreensMixin.this.client != null) ScreensMixin.this.client.setScreen(new UnicodeTableScreen(ScreensMixin.this));
                    return true;
                }
            };
            adder.add(tableButtonWidget);
        }

        gridWidget.refreshPositions();
        gridWidget.setX(SymbolChat.config.getHudPosition() == ConfigProvider.HudPosition.LEFT ? 2 : width - 2 - gridWidget.getWidth());
        gridWidget.refreshPositions();
        gridWidget.forEachChild(this::addDrawableChild);

        this.symbolSuggestor = new SymbolSuggestor(this, this::onSymbolReplaced, (SymbolSuggestable) this);
        this.addDrawableChild(symbolSuggestor);
    }

    @Override
    public boolean isSymbolChatWidget(Element element) {
        return element == symbolSelectionPanel ||
                element == symbolButtonWidget ||
                element == symbolSuggestor ||
                element == fontSelectionDropDown ||
                element == settingsButtonWidget ||
                element == tableButtonWidget;
    }

    @Override
    @NotNull
    public FontProcessor getFontProcessor() {
        if(this.fontSelectionDropDown == null) return SymbolChat.fontManager.getNormal();
        FontProcessor selection = this.fontSelectionDropDown.getSelection();
        return selection == null ? SymbolChat.fontManager.getNormal() : selection;
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

    @Unique
    @SuppressWarnings("unchecked")
    public void insertSymbol(String s) {
        ((Consumer<String>) this).accept(s);
    }

    @Unique
    private void onSymbolReplaced(String s) {
        ((SymbolSuggestable) this).replaceSuggestion(s);
    }
}
