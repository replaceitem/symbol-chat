package net.replaceitem.symbolchat.gui;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.TextRendererAccess;
import net.replaceitem.symbolchat.Util;
import net.replaceitem.symbolchat.gui.widget.IntSpinnerWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class UnicodeTableScreen extends Screen implements PasteSymbolButtonWidget.Context {
    public UnicodeTableScreen(Screen parent) {
        super(Text.of("Unicode Table"));
        this.parent = parent;
    }

    private static final Identifier COPY_TEXTURE = Identifier.of(SymbolChat.NAMESPACE, "copy");
    private static final Identifier FAVORITE_TEXTURE = Identifier.of(SymbolChat.NAMESPACE, "favorite");

    private final Screen parent;

    private ButtonWidget copySelectedButton;
    private ButtonWidget favoriteSymbolButton;
    
    private CheckboxWidget showBlocksWidget;
    private CheckboxWidget textShadow;

    private TextFieldWidget searchTextField;
    private IntSpinnerWidget pageSpinner;
    private IntSpinnerWidget widthSpinner;
    private CheckboxWidget hideMissingGlyphs;
    
    private int selectionStart = -1;
    private int selectionEnd = -1;
    
    public static final int SIDEBAR_WIDTH = 128;
    public static final int SYMBOLS_START_X = SIDEBAR_WIDTH+2;

    // leftmost byte shows the block color
    private IntList codepoints = new IntArrayList();
    
    private final int[] CYCLING_BLOCK_COLORS = new int[] {
            0xFF800000,
            0xFF808000,
            0xFF008000,
            0xFF008080,
            0xFF000080,
            0xFF800080
    };

    @Override
    protected void init() {
        super.init();
        this.columns = Math.floorDiv(this.width-SYMBOLS_START_X, SymbolButtonWidget.GRID_SPCAING);
        this.screenRows = Math.floorDiv(this.height, SymbolButtonWidget.GRID_SPCAING);
        
        int widgetWidth = SIDEBAR_WIDTH-4;

        DirectionalLayoutWidget adder = new DirectionalLayoutWidget(2,2, DirectionalLayoutWidget.DisplayAxis.VERTICAL);
        adder.spacing(4);

        {
            DirectionalLayoutWidget buttonRow = adder.add(new DirectionalLayoutWidget(0,0, DirectionalLayoutWidget.DisplayAxis.HORIZONTAL));
            copySelectedButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY, button -> copySelected(), true).texture(COPY_TEXTURE, 16, 16).dimension(20, 20).build();
            buttonRow.add(copySelectedButton);

            favoriteSymbolButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY, button -> favoriteSymbols(), true).texture(FAVORITE_TEXTURE, 16, 16).dimension(20, 20).build();
            buttonRow.add(favoriteSymbolButton);
            if (!SymbolChat.clothConfigEnabled) {
                favoriteSymbolButton.setTooltip(Tooltip.of(Text.translatable("symbolchat.no_clothconfig")));
                favoriteSymbolButton.active = false;
            }
        }


        adder.add(EmptyWidget.ofHeight(6));
        adder.add(new TextWidget(Text.translatable("symbolchat.view").styled(style -> style.withUnderline(true)), this.textRenderer));
        
        showBlocksWidget = CheckboxWidget.builder(Text.translatable("symbolchat.show_blocks"), textRenderer).callback((checkbox, checked) -> refreshButtons()).build();
        adder.add(showBlocksWidget);
        
        textShadow = CheckboxWidget.builder(Text.translatable("symbolchat.text_shadow"), textRenderer).checked(true).callback((checkbox, checked) -> refreshButtons()).build();
        adder.add(textShadow);
        
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
        
        hideMissingGlyphs = CheckboxWidget.builder(Text.translatable("symbolchat.hide_missing_glyphs"), textRenderer).callback((checkbox, checked) -> reloadSymbols()).build();
        adder.add(hideMissingGlyphs);

        adder.refreshPositions();
        adder.forEachChild(this::addDrawableChild);
        
        this.reloadSymbols();
    }

    private IntStream getSelectedSymbols() {
        IntStream.Builder intStreamBuilder = IntStream.builder();
        for(int i = selectionStart; i <= selectionEnd; i++) {
            int codepoint = this.codepoints.getInt(i);
            intStreamBuilder.add(codepoint & 0x00FFFFFF);
        }
        return intStreamBuilder.build();
    }
    
    private void favoriteSymbols() {
        if(selectionStart == -1) return;
        IntStream selectedSymbols = getSelectedSymbols();
        SymbolChat.config.toggleFavorite(selectedSymbols.mapToObj(Character::toString));
        refreshButtons();
    }

    private void copySelected() {
        if(selectionStart == -1) return;
        MinecraftClient.getInstance().keyboard.setClipboard(Util.stringFromCodePoints(getSelectedSymbols()));
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        this.refreshButtons();
    }

    private int scroll;
    private int columns;
    private int screenRows;
    private final List<PasteSymbolButtonWidget> widgets = new ArrayList<>();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        for (PasteSymbolButtonWidget widget : widgets) {
            widget.render(context, mouseX, mouseY, delta);
        }
        int scrollbarRows = Math.max(MathHelper.ceilDiv(codepoints.size(), columns), screenRows);
        double visibleRatio = (double) screenRows / scrollbarRows;
        int scrollbarHeight = (int) (visibleRatio * height);
        int scrollbarY = (int) MathHelper.clampedMap(scroll, 0, scrollbarRows-screenRows, 0, height - scrollbarHeight);
        context.fill(width-2, scrollbarY, width-1, scrollbarY+scrollbarHeight, 0xFFA0A0A0);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        int color = SymbolChat.config.getButtonColor();
        int alpha = ColorHelper.Argb.getAlpha(color);
        // remove alpha of color and apply it as fading to black instead
        color = ColorHelper.Argb.mixColor(color, ColorHelper.Argb.getArgb(255, alpha, alpha, alpha));
        color |= 0xFF000000;
        context.fill(0, 0, this.width, this.height, color);

        drawGridLines(context);
        context.fill(0, 0, SIDEBAR_WIDTH, height, 0xFF101010); // sidebar background
        context.drawVerticalLine(SIDEBAR_WIDTH, -1, height, 0xFFFFFFFF); // sidebar divider line
    }

    private void drawGridLines(DrawContext context) {
        for (int i = 0; i < columns+1; i++) {
            context.drawVerticalLine(SYMBOLS_START_X + i*SymbolButtonWidget.GRID_SPCAING - 1, -1, height, 0xFF101010);
        }
        for (int i = 0; i < screenRows; i++) {
            context.drawHorizontalLine(SYMBOLS_START_X, SYMBOLS_START_X + columns*SymbolButtonWidget.GRID_SPCAING, i*SymbolButtonWidget.GRID_SPCAING+SymbolButtonWidget.SYMBOL_SIZE, 0xFF101010);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (PasteSymbolButtonWidget widget : widgets) {
            if(widget.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void reloadSymbols() {
        this.searchCodePoints();
        this.refreshButtons();
    }

    private void searchCodePoints() {
        selectionStart = -1;
        selectionEnd = -1;
        String search = searchTextField.getText().toUpperCase(Locale.ROOT);
        boolean searching = search.isBlank();
        String[] searchTerms = search.trim().split("\\s+");
        CodePointCollector collector = new CodePointCollector();
        
        if(searching) {
            int pageMask = pageSpinner.getValue().orElse(0) << 16;
            for (int i = 0; i <= 0xFFFF; i++) {
                int codepoint = pageMask | i;
                if(!Character.isValidCodePoint(codepoint)) break;
                collector.accept(codepoint);
            }
        } else {
            int codepoint = 0;
            while(Character.isValidCodePoint(codepoint)) {
                String name = Character.getName(codepoint);
                if(name != null && isRelevant(name, searchTerms)) {
                    collector.accept(codepoint);
                }
                codepoint++;
            }
        }
        
        this.codepoints = collector.getCodepoints();
    }

    @Override
    public void onSymbolClicked(String symbol) {
        this.refresh();
    }

    @Override
    public void refresh() {
        refreshButtons();
    }

    private class CodePointCollector {
        IntList codepoints = new IntArrayList();
        int blockCycleColorIndex = CYCLING_BLOCK_COLORS.length-1;
        Character.UnicodeBlock currentBlock = null;
        
        void accept(int codepoint) {
            if(hideMissingGlyphs.isChecked() && ((TextRendererAccess) textRenderer).isMissingGlyph(codepoint, Style.EMPTY)) return;
            Character.UnicodeBlock newBlock = Character.UnicodeBlock.of(codepoint);
            if (newBlock != currentBlock) {
                blockCycleColorIndex = (blockCycleColorIndex + 1) % CYCLING_BLOCK_COLORS.length;
                currentBlock = newBlock;
            }
            codepoint |= (blockCycleColorIndex << 24);
            codepoints.add(codepoint);
        }

        IntList getCodepoints() {
            return codepoints;
        }
    }
    
    private static boolean isRelevant(String name, String[] searchTerms) {
        for (String s : searchTerms) {
            if(!name.contains(s)) return false;
        }
        return true;
    }
    
    public class TableButton extends PasteSymbolButtonWidget {
        private final int index;
        private final boolean marked;
        private final int backgroundColor;
        private final int hoverBackgroundColor;
        
        public TableButton(int x, int y, Context context, String symbol, Tooltip tooltip, int index, int backgroundColor, int hoverBackgroundColor) {
            super(x, y, context, symbol, tooltip);
            this.index = index;
            this.marked = index >= selectionStart && index <= selectionEnd;
            this.backgroundColor = backgroundColor;
            this.hoverBackgroundColor = hoverBackgroundColor;
        }
        
        public TableButton(int x, int y, Context context, String symbol, Tooltip tooltip, int index) {
            this(x, y, context, symbol, tooltip, index, SymbolChat.config.getButtonColor(), SymbolChat.config.getButtonHoverColor());
        }

        @Override
        protected int getBackgroundColor() {
            return this.isHovered() ? hoverBackgroundColor : backgroundColor;
        }

        @Override
        protected boolean shouldDrawOutline() {
            return marked;
        }

        @Override
        protected boolean shouldRenderTextWithShadow() {
            return textShadow.isChecked();
        }

        @Override
        protected boolean shouldRenderBackground() {
            return showBlocksWidget.isChecked() || this.isHovered();
        }

        @Override
        public boolean onClick(int button) {
            if(button == GLFW.GLFW_MOUSE_BUTTON_1) {
                if (Screen.hasShiftDown() && selectionStart != -1) {
                    if (selectionStart > index) {
                        selectionEnd = selectionStart;
                        selectionStart = index;
                    } else {
                        selectionEnd = index;
                    }
                } else {
                    selectionStart = index;
                    selectionEnd = index;
                }
            }
            super.onClick(button);
            return true;
        }
    }

    private void refreshButtons() {
        this.scroll = MathHelper.clamp(scroll, 0, Math.max(MathHelper.ceilDiv(codepoints.size(), columns)-screenRows, 0));
        this.widgets.clear();
        int x = SYMBOLS_START_X, y = 0;
        int index = scroll*columns;
        while(index < codepoints.size()) {
            int value = codepoints.getInt(index);
            int codePoint = value & 0x00FFFFFF;
            int blockColor = CYCLING_BLOCK_COLORS[(value & 0xFF000000) >> 24];
            String symbol = Util.stringFromCodePoint(codePoint);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
            
            Tooltip tooltip = Tooltip.of(Text.empty()
                    .append(Text.literal(Integer.toHexString(codePoint)))
                    .append("\n\n" + Util.getCapitalizedSymbolName(codePoint) + "\n")
                    .append("Width: " + textRenderer.getWidth(symbol) + "\n")
                    .append(block == null ? Text.literal("UNKNOWN BLOCK").formatted(Formatting.GRAY) : Text.literal(block.toString()).styled(style -> style.withColor(blockColor))));

            TableButton button = showBlocksWidget.isChecked() ?
                    new TableButton(x, y, this, symbol, tooltip, index, ColorHelper.Argb.mixColor(blockColor, 0xFF808080), blockColor) :
                    new TableButton(x, y, this, symbol, tooltip, index);
            
            this.widgets.add(button);
            x += SymbolButtonWidget.GRID_SPCAING;
            if(x > width-SymbolButtonWidget.SYMBOL_SIZE) {
                x = SYMBOLS_START_X;
                y += SymbolButtonWidget.GRID_SPCAING;
            }
            if(y >= height) break;
            index++;
        }
        
        boolean buttonsActive = this.selectionStart != -1;
        this.copySelectedButton.active = buttonsActive;
        this.favoriteSymbolButton.active = buttonsActive;
    }


    @Override
    public void close() {
        if(this.client != null) this.client.setScreen(this.parent);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(Screen.isCopy(keyCode) && selectionStart != -1) {
            copySelected();
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_ESCAPE && selectionStart != -1) {
            selectionStart = -1;
            selectionEnd = -1;
            refreshButtons();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll -= ((int) verticalAmount * (Screen.hasControlDown() ? screenRows : 1));
        this.refreshButtons();
        return true;
    }
}
