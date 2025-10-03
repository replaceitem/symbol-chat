package net.replaceitem.symbolchat.mixin.screen;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.replaceitem.symbolchat.extensions.ScreenAccess;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.config.Config;
import net.replaceitem.symbolchat.gui.SymbolSelectionPanel;
import net.replaceitem.symbolchat.gui.UnicodeTableScreen;
import net.replaceitem.symbolchat.gui.widget.DropDownWidget;
import net.replaceitem.symbolchat.gui.widget.FlatIconButtonWidget;
import net.replaceitem.symbolchat.gui.widget.SymbolSuggestor;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import net.replaceitem.symbolchat.resource.FontProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin({ChatScreen.class, BookEditScreen.class, AnvilScreen.class, AbstractSignEditScreen.class})
public abstract class ScreensMixin extends Screen implements ScreenAccess, SymbolInsertable {

    protected ScreensMixin(Text title) {
        super(title);
    }
    
    @Unique
    private static final Identifier WRENCH_TEXTURE = Identifier.of(SymbolChat.NAMESPACE, "wrench");
    @Unique
    private static final Identifier TABLE_TEXTURE = Identifier.of(SymbolChat.NAMESPACE, "table");
    @Unique
    private static final Identifier SMILEY_TEXTURE = Identifier.of(SymbolChat.NAMESPACE, "smiley");
    
    @Unique
    @Nullable
    private SymbolSelectionPanel symbolSelectionPanel;
    @Unique
    @Nullable
    private FlatIconButtonWidget symbolButtonWidget;
    @Unique
    @Nullable
    private SymbolSuggestor symbolSuggestor;
    @Unique
    @Nullable
    private DropDownWidget<FontProcessor> fontSelectionDropDown;
    @Unique
    @Nullable
    private FlatIconButtonWidget settingsButtonWidget;
    @Unique
    @Nullable
    private FlatIconButtonWidget tableButtonWidget;


    @Override
    public void addSymbolChatComponents() {
        this.symbolSuggestor = new SymbolSuggestor(this, this::onSymbolReplaced, (SymbolSuggestable) this);
        this.addDrawableChild(symbolSuggestor);

        Config.HudCorner hudPosition = SymbolChat.config.hudPosition.get();
        Config.HudCorner symbolButtonPosition = SymbolChat.config.symbolButtonPosition.get();
        
        int padding = 2;
        int hudButtonsHeight = 15;
        
        int symbolButtonX = switch (symbolButtonPosition.getHorizontal()) {
            case LEFT -> padding;
            case RIGHT -> this.width - padding - SymbolButtonWidget.SYMBOL_SIZE;
        };
        int symbolButtonY = switch (symbolButtonPosition.getVertical()) {
            case TOP -> padding + (symbolButtonPosition.getHorizontal() == hudPosition.getHorizontal() ? hudButtonsHeight + padding : 0);
            case BOTTOM -> this.height - padding - SymbolButtonWidget.SYMBOL_SIZE;
        };
        int panelHeight = SymbolChat.config.symbolPanelHeight.get();
        int panelWidth = SymbolSelectionPanel.getWidthForTabs(SymbolChat.symbolManager.getTabs().size());
        int panelX = switch (symbolButtonPosition.getHorizontal()) {
            case LEFT -> padding;
            case RIGHT -> this.width - panelWidth - padding;
        };
        int panelY = switch (symbolButtonPosition.getVertical()) {
            case TOP -> symbolButtonY + SymbolButtonWidget.SYMBOL_SIZE + padding;
            case BOTTOM -> symbolButtonY - padding - panelHeight;
        };
        this.symbolSelectionPanel = new SymbolSelectionPanel(panelX, panelY, panelHeight, this);
        this.addDrawableChild(symbolSelectionPanel);

        symbolButtonWidget = new FlatIconButtonWidget(
                SymbolButtonWidget.SYMBOL_SIZE, SymbolButtonWidget.SYMBOL_SIZE,
                ScreenTexts.EMPTY,
                SymbolButtonWidget.SYMBOL_SIZE, SymbolButtonWidget.SYMBOL_SIZE,
                new ButtonTextures(SMILEY_TEXTURE),
                button -> {
                    symbolSelectionPanel.toggleVisible();
                    button.setOutlined(!button.isOutlined());
                },
                null,
                textSupplier -> Text.translatable("symbolchat.symbol_panel.toggle_symbol_panel")
        );
        symbolButtonWidget.setPosition(symbolButtonX, symbolButtonY);
        symbolButtonWidget.setOutlined(symbolSelectionPanel.isVisible());
        this.addDrawableChild(symbolButtonWidget);

        GridWidget gridWidget = new GridWidget(0, 0);
        gridWidget.setColumnSpacing(padding);
        GridWidget.Adder adder = gridWidget.createAdder(Integer.MAX_VALUE);

        if(!SymbolChat.config.hideFontButton.get()) {
            List<FontProcessor> fonts = SymbolChat.fontManager.getFontProcessors();
            FontProcessor selectedFont = null;
            if(SymbolChat.config.keepFontSelected.get()) {
                String fontString = SymbolChat.config.selectedFont.get();
                Identifier selectedFontId = fontString.isBlank() ? null : Identifier.tryParse(fontString);
                selectedFont = fonts.stream().filter(
                        fontProcessor -> fontProcessor.getId().equals(selectedFontId)
                ).findFirst().orElse(null);
            }
            this.fontSelectionDropDown = new DropDownWidget<>(
                    0, 0, 180, hudButtonsHeight,
                    fonts, selectedFont,
                    SymbolChat.config.hudPosition.get().getVertical() == Config.HudVerticalSide.BOTTOM
            ) {
                @Override
                public void onSelection(int index, FontProcessor element) {
                    if(SymbolChat.config.keepFontSelected.get()) {
                        SymbolChat.config.selectedFont.set(element.getId().toString());
                        SymbolChat.config.scheduleSave();
                    }

                    focusTextbox();
                }
            };
            adder.add(fontSelectionDropDown);
        }

        if(!SymbolChat.config.hideSettingsButton.get()) {
            settingsButtonWidget = new FlatIconButtonWidget(15,
                    hudButtonsHeight,
                    ScreenTexts.EMPTY,
                    15,
                    hudButtonsHeight,
                    new ButtonTextures(WRENCH_TEXTURE),
                    button -> MinecraftClient.getInstance().setScreen(SymbolChat.config.createScreen(ScreensMixin.this)),
                    Text.translatable("reconfigure.title.symbol-chat"),
                    textSupplier -> Text.translatable("reconfigure.title.symbol-chat"));
            adder.add(settingsButtonWidget);
        }

        if(!SymbolChat.config.hideTableButton.get()) {
            tableButtonWidget = new FlatIconButtonWidget(15,
                    hudButtonsHeight,
                    ScreenTexts.EMPTY,
                    15,
                    hudButtonsHeight,
                    new ButtonTextures(TABLE_TEXTURE),
                    button -> {
                        if(ScreensMixin.this.client != null) ScreensMixin.this.client.setScreen(new UnicodeTableScreen(ScreensMixin.this));
                    },
                    Text.translatable("symbolchat.unicode_table"),
                    textSupplier -> Text.translatable("symbolchat.unicode_table")
            );
            adder.add(tableButtonWidget);
        }

        gridWidget.refreshPositions();
        int hudX = switch(hudPosition.getHorizontal()) {
            case LEFT -> padding;
            case RIGHT -> width - padding - gridWidget.getWidth();
        };
        gridWidget.setX(hudX);
        int hudY = switch(hudPosition.getVertical()) {
            case TOP -> padding;
            case BOTTOM -> height - padding - SymbolButtonWidget.SYMBOL_SIZE - padding - gridWidget.getHeight();
        };
        gridWidget.setY(hudY);
        gridWidget.forEachChild(this::addDrawableChild);
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
        }
    }
    
    @Override
    @Unique
    public boolean handleSuggestorKeyPressed(KeyInput input) {
        return this.symbolSuggestor != null && this.symbolSuggestor.keyPressed(input);
    }

    @Override
    @Unique
    public boolean handlePanelKeyPressed(KeyInput input) {
        return this.symbolSelectionPanel != null && this.symbolSelectionPanel.isFocused() && this.symbolSelectionPanel.keyPressed(input);
    }

    @Override
    @Unique
    public boolean handlePanelCharTyped(CharInput input) {
        return this.symbolSelectionPanel != null && this.symbolSelectionPanel.charTyped(input);
    }

    @Unique
    private void onSymbolReplaced(String s) {
        ((SymbolSuggestable) this).replaceSuggestion(s);
    }
}
