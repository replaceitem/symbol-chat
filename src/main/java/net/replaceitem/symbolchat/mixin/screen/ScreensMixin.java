package net.replaceitem.symbolchat.mixin.screen;


import net.replaceitem.symbolchat.extensions.ScreenAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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

    protected ScreensMixin(Component title) {
        super(title);
    }
    
    @Unique
    private static final Identifier WRENCH_TEXTURE = Identifier.fromNamespaceAndPath(SymbolChat.NAMESPACE, "wrench");
    @Unique
    private static final Identifier TABLE_TEXTURE = Identifier.fromNamespaceAndPath(SymbolChat.NAMESPACE, "table");
    @Unique
    private static final Identifier SMILEY_TEXTURE = Identifier.fromNamespaceAndPath(SymbolChat.NAMESPACE, "smiley");
    
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
        this.addRenderableWidget(symbolSuggestor);

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
        this.addRenderableWidget(symbolSelectionPanel);

        symbolButtonWidget = new FlatIconButtonWidget(
                SymbolButtonWidget.SYMBOL_SIZE, SymbolButtonWidget.SYMBOL_SIZE,
                CommonComponents.EMPTY,
                SymbolButtonWidget.SYMBOL_SIZE, SymbolButtonWidget.SYMBOL_SIZE,
                new WidgetSprites(SMILEY_TEXTURE),
                button -> {
                    symbolSelectionPanel.toggleVisible();
                    button.setOutlined(!button.isOutlined());
                },
                null,
                textSupplier -> Component.translatable("symbolchat.symbol_panel.toggle_symbol_panel")
        );
        symbolButtonWidget.setPosition(symbolButtonX, symbolButtonY);
        symbolButtonWidget.setOutlined(symbolSelectionPanel.isVisible());
        this.addRenderableWidget(symbolButtonWidget);

        GridLayout gridWidget = new GridLayout(0, 0);
        gridWidget.columnSpacing(padding);
        GridLayout.RowHelper adder = gridWidget.createRowHelper(Integer.MAX_VALUE);

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
            adder.addChild(fontSelectionDropDown);
        }

        if(!SymbolChat.config.hideSettingsButton.get()) {
            settingsButtonWidget = new FlatIconButtonWidget(15,
                    hudButtonsHeight,
                    CommonComponents.EMPTY,
                    15,
                    hudButtonsHeight,
                    new WidgetSprites(WRENCH_TEXTURE),
                    button -> Minecraft.getInstance().setScreen(SymbolChat.config.createScreen(ScreensMixin.this)),
                    Component.translatable("reconfigure.title.symbol-chat"),
                    textSupplier -> Component.translatable("reconfigure.title.symbol-chat"));
            adder.addChild(settingsButtonWidget);
        }

        if(!SymbolChat.config.hideTableButton.get()) {
            tableButtonWidget = new FlatIconButtonWidget(15,
                    hudButtonsHeight,
                    CommonComponents.EMPTY,
                    15,
                    hudButtonsHeight,
                    new WidgetSprites(TABLE_TEXTURE),
                    button -> {
                        if(ScreensMixin.this.minecraft != null) ScreensMixin.this.minecraft.setScreen(new UnicodeTableScreen(ScreensMixin.this));
                    },
                    Component.translatable("symbolchat.unicode_table"),
                    textSupplier -> Component.translatable("symbolchat.unicode_table")
            );
            adder.addChild(tableButtonWidget);
        }

        gridWidget.arrangeElements();
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
        gridWidget.visitWidgets(this::addRenderableWidget);
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
    public boolean handleSuggestorKeyPressed(KeyEvent input) {
        return this.symbolSuggestor != null && this.symbolSuggestor.keyPressed(input);
    }

    @Override
    @Unique
    public boolean handlePanelKeyPressed(KeyEvent input) {
        return this.symbolSelectionPanel != null && this.symbolSelectionPanel.isFocused() && this.symbolSelectionPanel.keyPressed(input);
    }

    @Override
    @Unique
    public boolean handlePanelCharTyped(CharacterEvent input) {
        return this.symbolSelectionPanel != null && this.symbolSelectionPanel.charTyped(input);
    }

    @Unique
    private void onSymbolReplaced(String s) {
        ((SymbolSuggestable) this).replaceSuggestion(s);
    }
}
