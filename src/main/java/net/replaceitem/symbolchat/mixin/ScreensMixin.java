package net.replaceitem.symbolchat.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.replaceitem.symbolchat.ScreenAccess;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.config.ConfigProvider;
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

@Mixin({ChatScreen.class, BookEditScreen.class, AnvilScreen.class, AbstractSignEditScreen.class})
public abstract class ScreensMixin extends Screen implements ScreenAccess, SymbolInsertable {

    protected ScreensMixin(Text title) {
        super(title);
    }
    
    private static final Identifier WRENCH_TEXTURE = Identifier.of(SymbolChat.NAMESPACE, "wrench");
    private static final Identifier TABLE_TEXTURE = Identifier.of(SymbolChat.NAMESPACE, "table");
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
        int symbolButtonX = this.width - 2 - SymbolButtonWidget.SYMBOL_SIZE;
        int symbolButtonY = this.height - 2 - SymbolButtonWidget.SYMBOL_SIZE;
        int panelHeight = SymbolChat.config.getSymbolPanelHeight();
        int panelWidth = SymbolSelectionPanel.getWidthForTabs(SymbolChat.symbolManager.getTabs().size());
        this.symbolSelectionPanel = new SymbolSelectionPanel(this.width-panelWidth - 2, symbolButtonY-2-panelHeight, panelHeight, this);
        this.addDrawableChild(symbolSelectionPanel);

        symbolButtonWidget = new FlatIconButtonWidget(
                SymbolButtonWidget.SYMBOL_SIZE, SymbolButtonWidget.SYMBOL_SIZE,
                ScreenTexts.EMPTY,
                SymbolButtonWidget.SYMBOL_SIZE, SymbolButtonWidget.SYMBOL_SIZE,
                SMILEY_TEXTURE,
                button -> {
                    symbolSelectionPanel.toggleVisible();
                    button.setOutlined(!button.isOutlined());
                },
                textSupplier -> Text.translatable("symbolchat.symbol_panel.toggle_symbol_panel")
        );
        symbolButtonWidget.setPosition(symbolButtonX, symbolButtonY);
        symbolButtonWidget.setOutlined(symbolSelectionPanel.isVisible());
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
            settingsButtonWidget = new FlatIconButtonWidget(15, 15, ScreenTexts.EMPTY, 15, 15, WRENCH_TEXTURE, button -> {
                if(SymbolChat.clothConfigEnabled) {
                    MinecraftClient.getInstance().setScreen(SymbolChat.config.getConfigScreen(ScreensMixin.this));
                }
            }, textSupplier -> Text.translatable("text.autoconfig.symbol-chat.title"));
            settingsButtonWidget.setTooltip(Tooltip.of(Text.translatable(SymbolChat.clothConfigEnabled ? "text.autoconfig.symbol-chat.title" : "symbolchat.no_clothconfig")));
            adder.add(settingsButtonWidget);
        }

        if(!SymbolChat.config.getHideTableButton()) {
            tableButtonWidget = new FlatIconButtonWidget(15, 15, ScreenTexts.EMPTY, 15, 15, TABLE_TEXTURE, button -> {
                if(ScreensMixin.this.client != null) ScreensMixin.this.client.setScreen(new UnicodeTableScreen(ScreensMixin.this));
            }, textSupplier -> Text.translatable("symbolchat.unicode_table"));
            tableButtonWidget.setTooltip(Tooltip.of(Text.translatable("symbolchat.unicode_table")));
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
    public boolean handleSuggestorKeyPressed(int keyCode, int scanCode, int modifiers) {
        return this.symbolSuggestor != null && this.symbolSuggestor.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    @Unique
    public boolean handlePanelKeyPressed(int keyCode, int scanCode, int modifiers) {
        return this.symbolSelectionPanel != null && this.symbolSelectionPanel.isFocused() && this.symbolSelectionPanel.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    @Unique
    public boolean handlePanelCharTyped(char chr, int modifiers) {
        return this.symbolSelectionPanel != null && this.symbolSelectionPanel.charTyped(chr, modifiers);
    }

    @Unique
    private void onSymbolReplaced(String s) {
        ((SymbolSuggestable) this).replaceSuggestion(s);
    }
}
