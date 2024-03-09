package net.replaceitem.symbolchat.gui;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.TextRendererAccess;
import net.replaceitem.symbolchat.Util;
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

    private final Screen parent;

    private TextFieldWidget pageTextField;
    private TextFieldWidget searchTextField;
    private CheckboxWidget showBlocksWidget;
    private CheckboxWidget hideMissingGlyphs;
    private ButtonWidget copySelectedButton;
    private ButtonWidget favoriteSymbolButton;
    
    private int selectionStart = -1;
    private int selectionEnd = -1;
    
    int page = 0;
    
    public static final int TOOLBAR_HEIGHT = 40;

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
        this.columns = this.width / SymbolButtonWidget.GRID_SPCAING;
        this.screenRows = this.height / SymbolButtonWidget.GRID_SPCAING;

        GridWidget topRowGridWidget = new GridWidget(2,2);
        topRowGridWidget.setColumnSpacing(2);
        GridWidget.Adder topRowAdder = topRowGridWidget.createAdder(Integer.MAX_VALUE);

        TextWidget pageTextWidget = new TextWidget(Text.translatable("symbolchat.page"), this.textRenderer);
        pageTextWidget.setX(0);
        pageTextWidget.setY(0);
        topRowAdder.add(pageTextWidget);
        
        pageTextField = new TextFieldWidget(this.textRenderer, 0, 0, 20, 12, Text.empty());
        pageTextField.setText("0");
        pageTextField.setMaxLength(2);
        pageTextField.setChangedListener(s -> {
            try {
                page = Integer.parseInt(this.pageTextField.getText());
            } catch (NumberFormatException e) {
                page = 0;
            }
            this.reloadSymbols();
        });
        topRowAdder.add(pageTextField);
        
        topRowAdder.add(EmptyWidget.ofWidth(10));

        TextWidget searchTextWidget = new TextWidget(Text.translatable("symbolchat.search"), this.textRenderer);
        searchTextWidget.setX(0);
        searchTextWidget.setY(0);
        topRowAdder.add(searchTextWidget);

        searchTextField = new TextFieldWidget(this.textRenderer, 0, 0, 150, 12, Text.of(""));
        searchTextField.setChangedListener(s -> this.reloadSymbols());
        topRowAdder.add(searchTextField);

        topRowGridWidget.refreshPositions();
        topRowGridWidget.forEachChild(this::addDrawableChild);


        GridWidget bottomRowGridWidget = new GridWidget(2,18);
        GridWidget.Adder bottomRowAdder = bottomRowGridWidget.createAdder(Integer.MAX_VALUE);

        copySelectedButton = ButtonWidget.builder(Text.literal("📋 ").append(Text.translatable("symbolchat.copy_selected")), button -> copySelected()).dimensions(0, 0, 100, 20).build();
        bottomRowAdder.add(copySelectedButton);

        favoriteSymbolButton = ButtonWidget.builder(Text.literal("✩ ").append(Text.translatable("symbolchat.favorite_symbol")), button -> favoriteSymbols()).dimensions(0, 0, 140, 20).build();
        bottomRowAdder.add(favoriteSymbolButton);
        if(!SymbolChat.clothConfigEnabled) {
            favoriteSymbolButton.active = false;
            favoriteSymbolButton.setTooltip(Tooltip.of(Text.translatable("symbolchat.no_clothconfig")));
        }


        bottomRowAdder.add(EmptyWidget.ofWidth(5));
        
        MutableText showBlocksText = Text.translatable("symbolchat.show_blocks");
        showBlocksWidget = CheckboxWidget.builder(showBlocksText, textRenderer).callback((checkbox, checked) -> refreshButtons()).build();
        bottomRowAdder.add(showBlocksWidget);

        bottomRowAdder.add(EmptyWidget.ofWidth(5));
        
        hideMissingGlyphs = CheckboxWidget.builder(Text.translatable("symbolchat.hide_missing_glyphs"), textRenderer).callback((checkbox, checked) -> reloadSymbols()).build();
        bottomRowAdder.add(hideMissingGlyphs);
        
        bottomRowGridWidget.refreshPositions();
        bottomRowGridWidget.forEachChild(this::addDrawableChild);
        
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
        int scrollbarHeight = (int) (visibleRatio * (height - TOOLBAR_HEIGHT));
        int scrollbarY = (int) MathHelper.clampedMap(scroll, 0, scrollbarRows-screenRows, TOOLBAR_HEIGHT, height - scrollbarHeight);
        context.fill(width-2, scrollbarY, width-1, scrollbarY+scrollbarHeight, 0xFFA0A0A0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (PasteSymbolButtonWidget widget : widgets) {
            if(widget.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void reloadSymbols() {
        this.pageTextField.active = searchTextField.getText().isBlank();
        this.searchCodePoints();
        this.refreshButtons();
    }

    private void searchCodePoints() {
        selectionStart = -1;
        selectionEnd = -1;
        String search = searchTextField.getText().toUpperCase(Locale.ROOT);
        boolean searching = search.isBlank();
        CodePointCollector collector = new CodePointCollector();
        
        if(searching) {
            int pageMask = page << 16;
            for (int i = 0; i <= 0xFFFF; i++) {
                int codepoint = pageMask | i;
                if(!Character.isValidCodePoint(codepoint)) break;
                collector.accept(codepoint);
            }
        } else {
            int codepoint = 0;
            while(Character.isValidCodePoint(codepoint)) {
                String name = Character.getName(codepoint);
                if(name != null && isRelevant(name, search)) {
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
    
    private static boolean isRelevant(String name, String search) {
        for (String s : search.split(" ")) {
            if(!name.contains(s)) return false;
        }
        return true;
    }
    
    public class TableButton extends PasteSymbolButtonWidget {
        private final int index;
        private final boolean marked;
        private int backgroundColor;
        private int hoverBackgroundColor;
        
        public TableButton(int x, int y, Context context, String symbol, Tooltip tooltip, int index) {
            super(x, y, context, symbol, tooltip);
            this.index = index;
            this.marked = index >= selectionStart && index <= selectionEnd;
        }

        public void setBackgroundColors(int hoverColor) {
            this.hoverBackgroundColor = hoverColor;
            int alpha = hoverColor & 0xFF000000;
            int color = (
                    ((((hoverColor >> 16) & 0xFF) / 2) << 16) |
                            ((((hoverColor >> 8 ) & 0xFF) / 2) << 8 ) |
                            ((((hoverColor      ) & 0xFF) / 2)      )
            );
            this.backgroundColor = alpha | color;
        }

        @Override
        protected int getTextColor() {
            return this.isSelected() ? hoverBackgroundColor : backgroundColor;
        }

        @Override
        protected boolean shouldDrawOutline() {
            return marked;
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
        this.columns = this.width / SymbolButtonWidget.GRID_SPCAING;
        this.screenRows = (this.height-TOOLBAR_HEIGHT) / SymbolButtonWidget.GRID_SPCAING;
        this.scroll = MathHelper.clamp(scroll, 0, Math.max(MathHelper.ceilDiv(codepoints.size(), columns)-screenRows, 0));
        this.widgets.clear();
        int x = 1, y = TOOLBAR_HEIGHT;
        int index = scroll*columns;
        while(index < codepoints.size()) {
            int value = codepoints.getInt(index);
            int codePoint = value & 0x00FFFFFF;
            int blockColor = CYCLING_BLOCK_COLORS[(value & 0xFF000000) >> 24];
            String symbol = Util.stringFromCodePoint(codePoint);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
            
            Text tooltip = Text.empty()
                    .append(Text.literal(Integer.toHexString(codePoint)))
                    .append("\n\n" + Util.getCapitalizedSymbolName(codePoint) + "\n")
                    .append("Width: " + textRenderer.getWidth(symbol) + "\n")
                    .append(block == null ? Text.literal("UNKNOWN BLOCK").formatted(Formatting.GRAY) : Text.literal(block.toString()).styled(style -> style.withColor(blockColor)));

            TableButton button = new TableButton(x, y, this, symbol, Tooltip.of(tooltip), index);
            if(showBlocksWidget.isChecked()) {
                button.setBackgroundColors(blockColor);
            }
            this.widgets.add(button);
            x += SymbolButtonWidget.GRID_SPCAING;
            if(x > width-SymbolButtonWidget.SYMBOL_SIZE) {
                x = 1;
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

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xFF101010);
    }
}
