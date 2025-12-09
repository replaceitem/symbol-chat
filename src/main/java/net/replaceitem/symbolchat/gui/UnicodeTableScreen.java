package net.replaceitem.symbolchat.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.extensions.FontAccess;
import net.replaceitem.symbolchat.UnicodeSearch;
import net.replaceitem.symbolchat.gui.widget.IntSpinnerWidget;
import net.replaceitem.symbolchat.gui.widget.UnicodeTable;
import net.replaceitem.symbolchat.mixin.FontManagerAccessor;
import net.replaceitem.symbolchat.mixin.MinecraftAccessor;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntUnaryOperator;

public class UnicodeTableScreen extends Screen {
    public UnicodeTableScreen(Screen parent) {
        super(Component.nullToEmpty("Unicode Table"));
        this.parent = parent;
    }

    private static final Identifier COPY_TEXTURE = Identifier.fromNamespaceAndPath(SymbolChat.NAMESPACE, "copy");
    private static final Identifier FAVORITE_TEXTURE = Identifier.fromNamespaceAndPath(SymbolChat.NAMESPACE, "favorite");

    private final Screen parent;
    
    private UnicodeTable unicodeTable;

    private Button copySelectedButton;
    private Button favoriteSymbolButton;

    private EditBox searchTextField;
    private IntSpinnerWidget pageSpinner;
    private IntSpinnerWidget widthSpinner;
    private Checkbox hideMissingGlyphsCheckbox;
    
    public static final int SIDEBAR_WIDTH = 128;
    public static final int SYMBOLS_START_X = SIDEBAR_WIDTH+1;

    @Override
    protected void init() {
        super.init();
        
        unicodeTable = new UnicodeTable(font, SYMBOLS_START_X, 0, this.width - SYMBOLS_START_X, height) {
            @Override
            protected void onRefreshed() {
                boolean hasSelection = hasSelection();
                if(copySelectedButton != null) copySelectedButton.active = hasSelection;
                if(favoriteSymbolButton != null) favoriteSymbolButton.active = hasSelection;
            }
        };
        addRenderableWidget(unicodeTable);
        
        int widgetWidth = SIDEBAR_WIDTH-4;

        LinearLayout adder = new LinearLayout(2,2, LinearLayout.Orientation.VERTICAL);
        adder.spacing(4);

        {
            LinearLayout buttonRow = adder.addChild(new LinearLayout(0,0, LinearLayout.Orientation.HORIZONTAL));
            copySelectedButton = SpriteIconButton.builder(CommonComponents.EMPTY, button -> unicodeTable.copySelected(), true).sprite(COPY_TEXTURE, 16, 16).size(20, 20).build();
            copySelectedButton.setTooltip(Tooltip.create(Component.translatable("symbolchat.unicode_table.copy_to_clipboard")));
            buttonRow.addChild(copySelectedButton);

            favoriteSymbolButton = SpriteIconButton.builder(CommonComponents.EMPTY, button -> unicodeTable.favoriteSymbols(), true).sprite(FAVORITE_TEXTURE, 16, 16).size(20, 20).build();
            buttonRow.addChild(favoriteSymbolButton);
            favoriteSymbolButton.setTooltip(Tooltip.create(Component.translatable("symbolchat.unicode_table.favorite_symbol")));
        }


        adder.addChild(SpacerElement.height(6));
        adder.addChild(new StringWidget(Component.translatable("symbolchat.unicode_table.view").withStyle(style -> style.withUnderlined(true)), this.font));

        {
            adder.addChild(new StringWidget(Component.translatable("symbolchat.unicode_table.font"), this.font));

            assert minecraft != null;
            List<Identifier> fonts = ((FontManagerAccessor) ((MinecraftAccessor) minecraft).getFontManager()).getFontSets().keySet().stream().sorted().toList();
            CycleButton<Identifier> fontButton = CycleButton.<Identifier>builder(Component::translationArg, fonts.getFirst()).withValues(fonts).displayOnlyValue().create(0, 0, widgetWidth, 20, Component.empty(), (button, value) -> {
                unicodeTable.setFont(value);
                reloadSymbols();
            });
            fontButton.setValue(Minecraft.DEFAULT_FONT);
            adder.addChild(fontButton);
        }

        Checkbox showBlocksCheckbox = Checkbox.builder(Component.translatable("symbolchat.unicode_table.show_blocks"), font)
                .selected(SymbolChat.config.unicodeTableShowBlocks.get())
                .onValueChange((checkbox, checked) -> {
                    SymbolChat.config.unicodeTableShowBlocks.setIfValid(checked);
                    unicodeTable.setShowBlocks(checked);
                }).build();
        unicodeTable.setShowBlocks(showBlocksCheckbox.selected());
        adder.addChild(showBlocksCheckbox);

        Checkbox textShadowCheckbox = Checkbox.builder(Component.translatable("symbolchat.unicode_table.text_shadow"), font)
                .selected(SymbolChat.config.unicodeTableTextShadow.get())
                .onValueChange((checkbox, checked) -> {
                    SymbolChat.config.unicodeTableTextShadow.setIfValid(checked);
                    unicodeTable.setRenderTextShadow(checked);
                }).build();
        unicodeTable.setRenderTextShadow(textShadowCheckbox.selected());
        adder.addChild(textShadowCheckbox);

        {
            StringWidget jumpToLabel = new StringWidget(Component.translatable("symbolchat.unicode_table.jump_to"), this.font);
            EditBox jumpToTextField = new EditBox(this.font, widgetWidth - jumpToLabel.getWidth(), 12, Component.empty()) {
                @Override
                public boolean keyPressed(KeyEvent input) {
                    if(input.isConfirmation()) {
                        int codepoint;
                        try {
                            codepoint = Integer.parseInt(this.getValue(), 16);
                        } catch (NumberFormatException e) {
                            return true;
                        }
                        int page = (codepoint & 0xFF0000) >> 16;
                        if(pageSpinner.getValue().isEmpty() || pageSpinner.getValue().getAsInt() != page) {
                            pageSpinner.setValue(page);
                        }
                        unicodeTable.jumpTo(codepoint);
                        return true;
                    }
                    return super.keyPressed(input);
                }
            };
            jumpToTextField.setResponder(s -> this.reloadSymbols());

            FrameLayout jumpToRow = adder.addChild(new FrameLayout(widgetWidth, 0));
            jumpToRow.addChild(jumpToLabel, LayoutSettings::alignHorizontallyLeft);
            jumpToRow.addChild(jumpToTextField, LayoutSettings::alignHorizontallyRight);
        }
        
        adder.addChild(SpacerElement.height(6));
        adder.addChild(new StringWidget(Component.translatable("symbolchat.unicode_table.filter").withStyle(style -> style.withUnderlined(true)), this.font));
        
        {
            StringWidget searchLabel = new StringWidget(Component.translatable("symbolchat.unicode_table.search"), this.font);
            searchTextField = new EditBox(this.font, widgetWidth - searchLabel.getWidth(), 12, Component.empty());
            searchTextField.setResponder(s -> this.reloadSymbols());

            FrameLayout searchRow = adder.addChild(new FrameLayout(widgetWidth, 0));
            searchRow.addChild(searchLabel, LayoutSettings::alignHorizontallyLeft);
            searchRow.addChild(searchTextField, LayoutSettings::alignHorizontallyRight);
        }
        {
            pageSpinner = IntSpinnerWidget.builder(this.font).value(0).min(0).changedListener(optionalInt -> reloadSymbols()).build();

            FrameLayout pageRow = adder.addChild(new FrameLayout(widgetWidth, 0));
            pageRow.addChild(new StringWidget(Component.translatable("symbolchat.unicode_table.page"), this.font), LayoutSettings::alignHorizontallyLeft);
            pageRow.addChild(pageSpinner, LayoutSettings::alignHorizontallyRight);
        }
        {
            widthSpinner = IntSpinnerWidget.builder(this.font).value("").changedListener(optionalInt -> reloadSymbols()).build();

            FrameLayout pageRow = adder.addChild(new FrameLayout(widgetWidth, 0));
            pageRow.addChild(new StringWidget(Component.translatable("symbolchat.unicode_table.symbol_width"), this.font), LayoutSettings::alignHorizontallyLeft);
            pageRow.addChild(widthSpinner, LayoutSettings::alignHorizontallyRight);
        }
        
        hideMissingGlyphsCheckbox = Checkbox.builder(Component.translatable("symbolchat.unicode_table.hide_missing_glyphs"), font)
                .selected(SymbolChat.config.unicodeTableHideMissingGlyphs.get())
                .onValueChange((checkbox, checked) -> {
                    SymbolChat.config.unicodeTableHideMissingGlyphs.setIfValid(checked);
                    reloadSymbols();
                }).build();
        adder.addChild(hideMissingGlyphsCheckbox);

        adder.arrangeElements();
        adder.visitWidgets(this::addRenderableWidget);
        
        this.reloadSymbols();
    }

    private void reloadSymbols() {
        OptionalInt page = pageSpinner.getValue();
        UnicodeSearch search = page.isPresent() ? UnicodeSearch.ofPage(page.getAsInt()) : UnicodeSearch.ofAll();
        
        search = search.search(searchTextField.getValue());

        OptionalInt width = widthSpinner.getValue();
        if(width.isPresent()) {
            IntUnaryOperator codepointWidthGetter = ((FontAccess) font).getCodepointWidthGetter(unicodeTable.getStyle());
            search = search.filterWidth(width.getAsInt(), codepointWidthGetter);
        }
        if(hideMissingGlyphsCheckbox.selected()) {
            search = search.filter(((FontAccess) font).getMissingGlyphPredicate(unicodeTable.getStyle()).negate());
        }
        
        unicodeTable.setCodepoints(search.collect());
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xFF303030);
        context.vLine(SIDEBAR_WIDTH, -1, height, 0xFFFFFFFF); // sidebar divider line
    }
    
    @Override
    public void onClose() {
        if(this.minecraft != null) this.minecraft.setScreen(this.parent);
    }
}
