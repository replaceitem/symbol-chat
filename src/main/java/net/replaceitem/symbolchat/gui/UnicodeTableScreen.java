package net.replaceitem.symbolchat.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.extensions.TextRendererAccess;
import net.replaceitem.symbolchat.UnicodeSearch;
import net.replaceitem.symbolchat.gui.widget.IntSpinnerWidget;
import net.replaceitem.symbolchat.gui.widget.UnicodeTable;
import net.replaceitem.symbolchat.mixin.FontManagerAccessor;
import net.replaceitem.symbolchat.mixin.MinecraftClientAccessor;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.OptionalInt;

public class UnicodeTableScreen extends Screen {
    public UnicodeTableScreen(Screen parent) {
        super(Text.of("Unicode Table"));
        this.parent = parent;
    }

    private static final Identifier COPY_TEXTURE = Identifier.of(SymbolChat.NAMESPACE, "copy");
    private static final Identifier FAVORITE_TEXTURE = Identifier.of(SymbolChat.NAMESPACE, "favorite");

    private final Screen parent;
    
    private UnicodeTable unicodeTable;

    private ButtonWidget copySelectedButton;
    private ButtonWidget favoriteSymbolButton;

    private TextFieldWidget searchTextField;
    private IntSpinnerWidget pageSpinner;
    private IntSpinnerWidget widthSpinner;
    private CheckboxWidget hideMissingGlyphsCheckbox;
    
    public static final int SIDEBAR_WIDTH = 128;
    public static final int SYMBOLS_START_X = SIDEBAR_WIDTH+1;

    @Override
    protected void init() {
        super.init();
        
        unicodeTable = new UnicodeTable(textRenderer, SYMBOLS_START_X, 0, this.width - SYMBOLS_START_X, height) {
            @Override
            protected void onRefreshed() {
                boolean hasSelection = hasSelection();
                if(copySelectedButton != null) copySelectedButton.active = hasSelection;
                if(favoriteSymbolButton != null) favoriteSymbolButton.active = hasSelection;
            }
        };
        addDrawableChild(unicodeTable);
        
        int widgetWidth = SIDEBAR_WIDTH-4;

        DirectionalLayoutWidget adder = new DirectionalLayoutWidget(2,2, DirectionalLayoutWidget.DisplayAxis.VERTICAL);
        adder.spacing(4);

        {
            DirectionalLayoutWidget buttonRow = adder.add(new DirectionalLayoutWidget(0,0, DirectionalLayoutWidget.DisplayAxis.HORIZONTAL));
            copySelectedButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY, button -> unicodeTable.copySelected(), true).texture(COPY_TEXTURE, 16, 16).dimension(20, 20).build();
            copySelectedButton.setTooltip(Tooltip.of(Text.translatable("symbolchat.unicode_table.copy_to_clipboard")));
            buttonRow.add(copySelectedButton);

            favoriteSymbolButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY, button -> unicodeTable.favoriteSymbols(), true).texture(FAVORITE_TEXTURE, 16, 16).dimension(20, 20).build();
            buttonRow.add(favoriteSymbolButton);
            favoriteSymbolButton.setTooltip(Tooltip.of(Text.translatable("symbolchat.unicode_table.favorite_symbol")));
        }


        adder.add(EmptyWidget.ofHeight(6));
        adder.add(new TextWidget(Text.translatable("symbolchat.unicode_table.view").styled(style -> style.withUnderline(true)), this.textRenderer));

        {
            adder.add(new TextWidget(Text.translatable("symbolchat.unicode_table.font"), this.textRenderer));

            assert client != null;
            List<Identifier> fonts = ((FontManagerAccessor) ((MinecraftClientAccessor) client).getFontManager()).getFontStorages().keySet().stream().sorted().toList();
            CyclingButtonWidget<Identifier> fontButton = CyclingButtonWidget.<Identifier>builder(Text::of).values(fonts).omitKeyText().build(0, 0, widgetWidth, 20, Text.empty(), (button, value) -> {
                unicodeTable.setFont(value);
                reloadSymbols();
            });
            fontButton.setValue(Style.DEFAULT_FONT_ID);
            adder.add(fontButton);
        }

        CheckboxWidget showBlocksCheckbox = CheckboxWidget.builder(Text.translatable("symbolchat.unicode_table.show_blocks"), textRenderer)
                .checked(SymbolChat.config.unicodeTableShowBlocks.get())
                .callback((checkbox, checked) -> {
                    SymbolChat.config.unicodeTableShowBlocks.setIfValid(checked);
                    unicodeTable.setShowBlocks(checked);
                }).build();
        unicodeTable.setShowBlocks(showBlocksCheckbox.isChecked());
        adder.add(showBlocksCheckbox);

        CheckboxWidget textShadowCheckbox = CheckboxWidget.builder(Text.translatable("symbolchat.unicode_table.text_shadow"), textRenderer)
                .checked(SymbolChat.config.unicodeTableTextShadow.get())
                .callback((checkbox, checked) -> {
                    SymbolChat.config.unicodeTableTextShadow.setIfValid(checked);
                    unicodeTable.setRenderTextShadow(checked);
                }).build();
        unicodeTable.setRenderTextShadow(textShadowCheckbox.isChecked());
        adder.add(textShadowCheckbox);

        {
            TextWidget jumpToLabel = new TextWidget(Text.translatable("symbolchat.unicode_table.jump_to"), this.textRenderer);
            TextFieldWidget jumpToTextField = new TextFieldWidget(this.textRenderer, widgetWidth - jumpToLabel.getWidth(), 12, Text.empty()) {
                @Override
                public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                    if(keyCode == GLFW.GLFW_KEY_ENTER) {
                        int codepoint;
                        try {
                            codepoint = Integer.parseInt(this.getText(), 16);
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
                    return super.keyPressed(keyCode, scanCode, modifiers);
                }
            };
            jumpToTextField.setChangedListener(s -> this.reloadSymbols());

            SimplePositioningWidget jumpToRow = adder.add(new SimplePositioningWidget(widgetWidth, 0));
            jumpToRow.add(jumpToLabel, Positioner::alignLeft);
            jumpToRow.add(jumpToTextField, Positioner::alignRight);
        }
        
        adder.add(EmptyWidget.ofHeight(6));
        adder.add(new TextWidget(Text.translatable("symbolchat.unicode_table.filter").styled(style -> style.withUnderline(true)), this.textRenderer));
        
        {
            TextWidget searchLabel = new TextWidget(Text.translatable("symbolchat.unicode_table.search"), this.textRenderer);
            searchTextField = new TextFieldWidget(this.textRenderer, widgetWidth - searchLabel.getWidth(), 12, Text.empty());
            searchTextField.setChangedListener(s -> this.reloadSymbols());

            SimplePositioningWidget searchRow = adder.add(new SimplePositioningWidget(widgetWidth, 0));
            searchRow.add(searchLabel, Positioner::alignLeft);
            searchRow.add(searchTextField, Positioner::alignRight);
        }
        {
            pageSpinner = IntSpinnerWidget.builder(this.textRenderer).value(0).min(0).changedListener(optionalInt -> reloadSymbols()).build();

            SimplePositioningWidget pageRow = adder.add(new SimplePositioningWidget(widgetWidth, 0));
            pageRow.add(new TextWidget(Text.translatable("symbolchat.unicode_table.page"), this.textRenderer), Positioner::alignLeft);
            pageRow.add(pageSpinner, Positioner::alignRight);
        }
        {
            widthSpinner = IntSpinnerWidget.builder(this.textRenderer).value("").changedListener(optionalInt -> reloadSymbols()).build();

            SimplePositioningWidget pageRow = adder.add(new SimplePositioningWidget(widgetWidth, 0));
            pageRow.add(new TextWidget(Text.translatable("symbolchat.unicode_table.symbol_width"), this.textRenderer), Positioner::alignLeft);
            pageRow.add(widthSpinner, Positioner::alignRight);
        }
        
        hideMissingGlyphsCheckbox = CheckboxWidget.builder(Text.translatable("symbolchat.unicode_table.hide_missing_glyphs"), textRenderer)
                .checked(SymbolChat.config.unicodeTableHideMissingGlyphs.get())
                .callback((checkbox, checked) -> {
                    SymbolChat.config.unicodeTableHideMissingGlyphs.setIfValid(checked);
                    reloadSymbols();
                }).build();
        adder.add(hideMissingGlyphsCheckbox);

        adder.refreshPositions();
        adder.forEachChild(this::addDrawableChild);
        
        this.reloadSymbols();
    }

    private void reloadSymbols() {
        OptionalInt page = pageSpinner.getValue();
        UnicodeSearch search = page.isPresent() ? UnicodeSearch.ofPage(page.getAsInt()) : UnicodeSearch.ofAll();
        
        search = search.search(searchTextField.getText());

        OptionalInt width = widthSpinner.getValue();
        if(width.isPresent()) search = search.filterWidth(width.getAsInt(), ((TextRendererAccess) textRenderer).getCodepointWidthGetter(unicodeTable.getStyle()));
        
        if(hideMissingGlyphsCheckbox.isChecked()) search = search.filter(((TextRendererAccess) textRenderer).getMissingGlyphPredicate(unicodeTable.getStyle()).negate());
        
        unicodeTable.setCodepoints(search.collect());
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xFF303030);
        context.drawVerticalLine(SIDEBAR_WIDTH, -1, height, 0xFFFFFFFF); // sidebar divider line
    }
    
    @Override
    public void close() {
        if(this.client != null) this.client.setScreen(this.parent);
    }
}
