package net.replaceitem.symbolchat.gui.widget;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UProperty;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.extensions.TextRendererAccess;
import net.replaceitem.symbolchat.Util;
import net.replaceitem.symbolchat.gui.container.NonScrollableContainerWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget.GRID_SPCAING;

public class UnicodeTable extends NonScrollableContainerWidget implements PasteSymbolButtonWidget.Context {

    private final TextRenderer textRenderer;
    
    private Style style = Style.EMPTY;

    private boolean renderTextShadow;
    private boolean showBlocks;

    private static final int SCROLLBAR_WIDTH = 6;
    private double scroll;
    private boolean scrolling;
    private int maxScroll;
    private final int scrollbarX;
    private int scrollbarY;
    private int scrollbarHeight;
    
    private final int columns;
    private final int visibleRows;
    private int totalRows;
    
    private int selectionStart = -1;
    private int selectionEnd = -1;

    // leftmost byte shows the block color
    private int[] codepoints;


    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("widget/scroller");
    private static final Identifier SCROLLER_BACKGROUND_TEXTURE = Identifier.ofVanilla("widget/scroller_background");
    private static final int[] CYCLING_BLOCK_COLORS = new int[] {0xFF800000,0xFF808000,0xFF008000,0xFF008080,0xFF000080,0xFF800080};

    public UnicodeTable(TextRenderer textRenderer, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.textRenderer = textRenderer;
        this.scrollbarX = getX()+width-SCROLLBAR_WIDTH;
        this.columns = Math.floorDiv(this.width-1-SCROLLBAR_WIDTH, GRID_SPCAING);
        this.visibleRows = Math.floorDiv(this.height-1, GRID_SPCAING);
        setCodepoints(new int[0]);
        refresh();
    }
    
    private void calculateScrollbarPos() {
        maxScroll = Math.max(totalRows, visibleRows)-visibleRows;
        double visibleRatio = totalRows > 0 ? Math.min((double) visibleRows / totalRows, 1) : 1;
        scrollbarHeight = Math.max((int) (visibleRatio * height), 16);
        scrollbarY = (int) MathHelper.clampedMap(scroll, 0, maxScroll, 0, height - scrollbarHeight);
        setScroll(scroll);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawBackground(context);
        super.renderWidget(context, mouseX, mouseY, delta);
        
        if(scrollbarHeight != getHeight()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_BACKGROUND_TEXTURE, scrollbarX, getY(), SCROLLBAR_WIDTH, getHeight());
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_TEXTURE, scrollbarX, scrollbarY, SCROLLBAR_WIDTH, scrollbarHeight);
        }
    }

    private void drawBackground(DrawContext context) {
        int color = SymbolChat.config.buttonColor.get();
        int alpha = ColorHelper.getAlpha(color);
        // remove alpha of color and apply it as fading to black instead
        color = ColorHelper.mix(color, ColorHelper.getArgb(255, alpha, alpha, alpha));
        color = ColorHelper.fullAlpha(color);

        int visibleSymbols = codepoints.length-(getScrolledRows()*columns);

        int fullRowsCount = Math.floorDiv(visibleSymbols, columns);
        int partialRowsCount = Math.ceilDiv(visibleSymbols, columns);
        int firstRowCount = Math.min(visibleSymbols, columns);
        if(fullRowsCount != 0 && firstRowCount != 0) {
            context.fill(getX(), getY(), getX() + firstRowCount*GRID_SPCAING, getY()+fullRowsCount*GRID_SPCAING, color);
        }
        if(partialRowsCount != fullRowsCount) {
            int partialRowSymbolCount = visibleSymbols % columns;
            if(partialRowSymbolCount != 0 && partialRowsCount != 0) {
                context.fill(getX(), getY(), getX() + partialRowSymbolCount * GRID_SPCAING, getY() + partialRowsCount * GRID_SPCAING, color);
            }
        }
        
        for (int i = 0; i <= columns; i++) {
            int leftSymbols = visibleSymbols - i;
            if(leftSymbols < 0) break;
            int lineHeight = MathHelper.clamp(MathHelper.ceilDiv(leftSymbols+1, columns) * GRID_SPCAING + 1, 0, height);
            context.drawVerticalLine(getX() + i* GRID_SPCAING, getY()-1, getY()+lineHeight, 0xFF303030);
        }
        for (int i = 0; i <= visibleRows; i++) {
            int leftSymbols = visibleSymbols - Math.max(i-1, 0)*columns;
            int lineWidth = Math.min(leftSymbols, columns) * GRID_SPCAING;
            if(lineWidth <= 0) break;
            context.drawHorizontalLine(getX(), getX() + lineWidth - 1, getY() + i * GRID_SPCAING, 0xFF303030);
        }
    }

    public void setRenderTextShadow(boolean renderTextShadow) {
        this.renderTextShadow = renderTextShadow;
    }

    public void setShowBlocks(boolean showBlocks) {
        this.showBlocks = showBlocks;
    }

    @Override
    public void onSymbolClicked(String symbol) {
        this.refresh();
    }

    @Override
    public void refresh() {
        calculateScrollbarPos();
        refreshButtons();
    }

    public void setFont(Identifier value) {
        this.style = Style.EMPTY.withFont(value);
    }

    public Style getStyle() {
        return style;
    }

    public void setCodepoints(int[] codepoints) {
        this.codepoints = codepoints;

        int blockCycleColorIndex = CYCLING_BLOCK_COLORS.length-1;
        int currentBlockIndex = -1;
        for (int i = 0; i < codepoints.length; i++) {
            int newBlockIndex = UCharacter.getIntPropertyValue(codepoints[i], UProperty.BLOCK);
            if (newBlockIndex != currentBlockIndex) {
                blockCycleColorIndex = (blockCycleColorIndex + 1) % CYCLING_BLOCK_COLORS.length;
                currentBlockIndex = newBlockIndex;
            }
            codepoints[i] |= (blockCycleColorIndex << 24);
        }
        
        this.totalRows = MathHelper.ceilDiv(codepoints.length, columns);
        this.refresh();
    }

    private void refreshButtons() {
        this.clearElements();
        int codepointIndex = getScrolledRows()*columns;
        int widgetIndex = 0;
        IntUnaryOperator widthGetter = ((TextRendererAccess) textRenderer).getCodepointWidthGetter(style);
        IntPredicate missingGlyphPredicate = ((TextRendererAccess) textRenderer).getMissingGlyphPredicate(style);

        while(codepointIndex < codepoints.length && widgetIndex < columns*visibleRows) {
            int value = codepoints[codepointIndex];
            int codePoint = value & 0x00FFFFFF;
            int blockColor = CYCLING_BLOCK_COLORS[(value & 0xFF000000) >> 24];
            String symbol = Util.stringFromCodePoint(codePoint);
            UCharacter.UnicodeBlock block = UCharacter.UnicodeBlock.of(codePoint);

            MutableText tooltipText = Text.empty()
                    .append(Text.literal(Integer.toHexString(codePoint)))
                    .append("\n\n" + Util.getPrettySymbolName(codePoint) + "\n")
                    .append(Text.translatable("symbolchat.unicode_table.symbol_tooltip.width", widthGetter.applyAsInt(codePoint)))
                    .append("\n")
                    .append(block == null ?
                            Text.translatable("symbolchat.unicode_table.symbol_tooltip.unknown_block").styled(style -> style.withColor(Formatting.GRAY).withItalic(true)) :
                            Text.literal(block.toString()).styled(style -> style.withColor(blockColor))
                    );

            Tooltip tooltip = Tooltip.of(tooltipText);

            int x = widgetIndex % columns * GRID_SPCAING + 1;
            int y = widgetIndex / columns * GRID_SPCAING + 1;
            TableButton button = new TableButton(getX()+x, getY()+y, this, symbol, tooltip, codepointIndex, blockColor, missingGlyphPredicate.test(codePoint));

            this.addChildren(button);
            
            codepointIndex++;
            widgetIndex++;
        }
        
        this.onRefreshed();
    }

    protected void onRefreshed() {}
    
    private void setScroll(double scroll) {
        this.scroll = MathHelper.clamp(scroll, 0, maxScroll);
    }
    
    private int getScrolledRows() {
        return (int) scroll;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        setScroll(scroll - (verticalAmount * (Screen.hasControlDown() ? visibleRows : 1)));
        this.refresh();
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(super.mouseClicked(mouseX, mouseY, button)) return true;
        if(mouseX >= scrollbarX && mouseX < scrollbarX+SCROLLBAR_WIDTH && mouseY >= scrollbarY && mouseY < scrollbarY+scrollbarHeight) {
            scrolling = button == 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(super.mouseReleased(mouseX, mouseY, button)) return true;
        if(button == 0) {
            scrolling = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (button == 0 && this.scrolling) {
            if (mouseY < (double)this.getY()) {
                scroll = 0;
            } else if (mouseY > (double)this.getBottom()) {
                scroll = maxScroll;
            } else {
                double scrolledRows = (int) MathHelper.map(deltaY, 0, height-scrollbarHeight, 0, maxScroll);
                setScroll(scroll + scrolledRows);
            }
            
            refresh();
            return true;
        }
        return false;
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
            refresh();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void copySelected() {
        if(!hasSelection()) return;
        MinecraftClient.getInstance().keyboard.setClipboard(Util.stringFromCodePoints(getSelectedSymbols()));
    }

    public void favoriteSymbols() {
        if(!hasSelection()) return;
        IntStream selectedSymbols = getSelectedSymbols();
        Stream<String> current = SymbolChat.config.favoriteSymbols.get().codePoints().mapToObj(UCharacter::toString);
        List<String> toToggle = selectedSymbols.mapToObj(UCharacter::toString).toList();
        Set<String> forRemoval = toToggle.stream().filter(value -> SymbolChat.symbolManager.isFavorite(value)).collect(Collectors.toUnmodifiableSet());
        current = Stream.concat(current.filter(k -> !forRemoval.contains(k)), toToggle.stream().filter(value -> !SymbolChat.symbolManager.isFavorite(value)));
        SymbolChat.config.favoriteSymbols.set(current.collect(Collectors.joining()));
        refresh();
    }

    public IntStream getSelectedSymbols() {
        IntStream.Builder intStreamBuilder = IntStream.builder();
        for(int i = selectionStart; i <= selectionEnd; i++) {
            int codepoint = this.codepoints[i];
            intStreamBuilder.add(codepoint & 0x00FFFFFF);
        }
        return intStreamBuilder.build();
    }
    public boolean hasSelection() {
        return selectionStart != -1;
    }

    public void jumpTo(int codepoint) {
        int index = binarySearchCodepoints(codepoints, codepoint);
        if(index < 0) return;
        setScroll(Math.floorDiv(index, columns) - visibleRows / 2.0);
        selectionEnd = index;
        selectionStart = index;
        refresh();
    }

    private class TableButton extends PasteSymbolButtonWidget {
        private final int index;
        private final boolean marked;
        private final int blockColor;
        private final boolean missing;

        public TableButton(int x, int y, Context context, String symbol, Tooltip tooltip, int index, int blockColor, boolean missing) {
            super(x, y, context, symbol, tooltip);
            this.index = index;
            this.marked = index >= selectionStart && index <= selectionEnd;
            this.blockColor = blockColor;
            this.missing = missing;
            this.setMessage(missing ? Text.empty() : Text.literal(symbol).setStyle(style));
        }

        @Override
        protected int getBackgroundColor() {
            // when not hovered, use halved RGB values
            return this.isHovered() ? (showBlocks ? blockColor : SymbolChat.config.buttonActiveColor.get()) : ColorHelper.scaleRgb(blockColor, 0.5f);
        }

        @Override
        protected void renderOverlay(DrawContext drawContext) {
            if(this.missing) drawContext.drawHorizontalLine(this.getX()+4, this.getRight()-5, this.getY() + getHeight()/2, 0xFFFF0000);
            super.renderOverlay(drawContext);
        }

        @Override
        protected boolean shouldDrawOutline() {
            return marked;
        }

        @Override
        protected boolean shouldRenderTextWithShadow() {
            return renderTextShadow;
        }

        @Override
        protected boolean shouldRenderBackground() {
            return showBlocks || this.isHovered();
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

    private static int binarySearchCodepoints(int[] arr, int key) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = arr[mid] & 0xFFFFFF;

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }
}







