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
import net.replaceitem.symbolchat.TextRendererAccess;
import net.replaceitem.symbolchat.UnicodeSearch;
import net.replaceitem.symbolchat.gui.widget.IntSpinnerWidget;
import net.replaceitem.symbolchat.gui.widget.UnicodeTable;

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
    
    private CheckboxWidget showBlocksCheckbox;
    private CheckboxWidget textShadowCheckbox;

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
                copySelectedButton.active = hasSelection;
                favoriteSymbolButton.active = hasSelection;
            }
        };
        addDrawableChild(unicodeTable);
        
        int widgetWidth = SIDEBAR_WIDTH-4;

        DirectionalLayoutWidget adder = new DirectionalLayoutWidget(2,2, DirectionalLayoutWidget.DisplayAxis.VERTICAL);
        adder.spacing(4);

        // TODO add tooltips to these
        {
            DirectionalLayoutWidget buttonRow = adder.add(new DirectionalLayoutWidget(0,0, DirectionalLayoutWidget.DisplayAxis.HORIZONTAL));
            copySelectedButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY, button -> unicodeTable.copySelected(), true).texture(COPY_TEXTURE, 16, 16).dimension(20, 20).build();
            buttonRow.add(copySelectedButton);

            favoriteSymbolButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY, button -> unicodeTable.favoriteSymbols(), true).texture(FAVORITE_TEXTURE, 16, 16).dimension(20, 20).build();
            buttonRow.add(favoriteSymbolButton);
            if (!SymbolChat.clothConfigEnabled) {
                favoriteSymbolButton.setTooltip(Tooltip.of(Text.translatable("symbolchat.no_clothconfig")));
                favoriteSymbolButton.active = false;
            }
        }


        adder.add(EmptyWidget.ofHeight(6));
        adder.add(new TextWidget(Text.translatable("symbolchat.view").styled(style -> style.withUnderline(true)), this.textRenderer));
        
        showBlocksCheckbox = CheckboxWidget.builder(Text.translatable("symbolchat.show_blocks"), textRenderer).callback((checkbox, checked) -> unicodeTable.setShowBlocks(checked)).build();
        unicodeTable.setShowBlocks(showBlocksCheckbox.isChecked());
        adder.add(showBlocksCheckbox);
        
        textShadowCheckbox = CheckboxWidget.builder(Text.translatable("symbolchat.text_shadow"), textRenderer).checked(true).callback((checkbox, checked) -> unicodeTable.setRenderTextShadow(checked)).build();
        unicodeTable.setRenderTextShadow(textShadowCheckbox.isChecked());
        adder.add(textShadowCheckbox);
        
        adder.add(EmptyWidget.ofHeight(6));
        adder.add(new TextWidget(Text.translatable("symbolchat.filter").styled(style -> style.withUnderline(true)), this.textRenderer));
        
        {
            TextWidget searchLabel = new TextWidget(Text.translatable("symbolchat.search"), this.textRenderer);
            searchTextField = new TextFieldWidget(this.textRenderer, widgetWidth - searchLabel.getWidth(), 12, Text.empty());
            searchTextField.setChangedListener(s -> this.reloadSymbols());

            SimplePositioningWidget searchRow = adder.add(new SimplePositioningWidget(widgetWidth, 0));
            searchRow.add(searchLabel, Positioner::alignLeft);
            searchRow.add(searchTextField, Positioner::alignRight);
        }
        {
            pageSpinner = IntSpinnerWidget.builder(this.textRenderer).value(0).min(0).changedListener(optionalInt -> reloadSymbols()).build();

            SimplePositioningWidget pageRow = adder.add(new SimplePositioningWidget(widgetWidth, 0));
            pageRow.add(new TextWidget(Text.translatable("symbolchat.page"), this.textRenderer), Positioner::alignLeft);
            pageRow.add(pageSpinner, Positioner::alignRight);
        }
        {
            widthSpinner = IntSpinnerWidget.builder(this.textRenderer).value("").changedListener(optionalInt -> reloadSymbols()).build();

            SimplePositioningWidget pageRow = adder.add(new SimplePositioningWidget(widgetWidth, 0));
            pageRow.add(new TextWidget(Text.translatable("symbolchat.symbol_width"), this.textRenderer), Positioner::alignLeft);
            pageRow.add(widthSpinner, Positioner::alignRight);
        }
        
        hideMissingGlyphsCheckbox = CheckboxWidget.builder(Text.translatable("symbolchat.hide_missing_glyphs"), textRenderer).callback((checkbox, checked) -> reloadSymbols()).build();
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
        if(width.isPresent()) search = search.filterWidth(width.getAsInt(), ((TextRendererAccess) textRenderer).getCodepointWidthGetter(Style.EMPTY));
        
        if(hideMissingGlyphsCheckbox.isChecked()) search = search.filter(((TextRendererAccess) textRenderer).getMissingGlyphPredicate(Style.EMPTY).negate());
        
        unicodeTable.setCodepoints(search.collect());
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xFF101010);
        context.drawVerticalLine(SIDEBAR_WIDTH, -1, height, 0xFFFFFFFF); // sidebar divider line
    }
    
    @Override
    public void close() {
        if(this.client != null) this.client.setScreen(this.parent);
    }
}
