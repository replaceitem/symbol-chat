package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.TextRendererAccess;
import net.replaceitem.symbolchat.Util;
import net.replaceitem.symbolchat.gui.container.ContainerWidgetImpl;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import org.lwjgl.glfw.GLFW;

import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

import static net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget.GRID_SPCAING;

public class UnicodeTable extends ContainerWidgetImpl implements PasteSymbolButtonWidget.Context {

    private final TextRenderer textRenderer;
    
    private boolean renderTextShadow;
    private boolean showBlocks;

    private int scroll;
    private final int columns;
    private final int visibleRows;
    
    private int selectionStart = -1;
    private int selectionEnd = -1;

    // leftmost byte shows the block color
    private int[] codepoints = new int[0];

    private static final int[] CYCLING_BLOCK_COLORS = new int[] {0xFF800000,0xFF808000,0xFF008000,0xFF008080,0xFF000080,0xFF800080};

    public UnicodeTable(TextRenderer textRenderer, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.textRenderer = textRenderer;
        this.columns = Math.floorDiv(this.width-1, GRID_SPCAING);
        this.visibleRows = Math.floorDiv(this.height-1, GRID_SPCAING);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawBackground(context);
        super.renderWidget(context, mouseX, mouseY, delta);
        int scrollbarRows = Math.max(MathHelper.ceilDiv(codepoints.length, columns), visibleRows);
        double visibleRatio = (double) visibleRows / scrollbarRows;
        int scrollbarHeight = (int) (visibleRatio * height);
        int scrollbarY = (int) MathHelper.clampedMap(scroll, 0, scrollbarRows-visibleRows, 0, height - scrollbarHeight);
        context.fill(getX()+width-2, getY()+scrollbarY, getX()+width-1, getY()+scrollbarY+scrollbarHeight, 0xFFA0A0A0);
    }

    private void drawBackground(DrawContext context) {
        int color = SymbolChat.config.getButtonColor();
        int alpha = ColorHelper.Argb.getAlpha(color);
        // remove alpha of color and apply it as fading to black instead
        color = ColorHelper.Argb.mixColor(color, ColorHelper.Argb.getArgb(255, alpha, alpha, alpha));
        color |= 0xFF000000;
        context.fill(getX(), getY(), getX()+this.width, getY()+this.height, color);
        
        int visibleSymbols = codepoints.length-(scroll*columns);
        
        for (int i = 0; i <= columns; i++) {
            int leftSymbols = visibleSymbols - i;
            if(leftSymbols < 0) break;
            int lineHeight = MathHelper.clamp(MathHelper.ceilDiv(leftSymbols+1, columns) * GRID_SPCAING + 1, 0, height);
            context.drawVerticalLine(getX() + i* GRID_SPCAING, getY()-1, getY()+lineHeight, 0xFF101010);
        }
        for (int i = 0; i <= visibleRows; i++) {
            int leftSymbols = visibleSymbols - (i-1)*columns;
            int lineWidth = Math.min(leftSymbols, columns) * GRID_SPCAING;
            if(lineWidth <= 0) break;
            context.drawHorizontalLine(getX(), getX() + lineWidth - 1, getY() + i * GRID_SPCAING, 0xFF101010);
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
        refreshButtons();
    }

    public void setCodepoints(int[] codepoints) {
        this.codepoints = codepoints;

        // TODO optimize by finding the end of current codepoint and skipping there, instead of getting block for each
        // TODO use UCharacter for more power
        int blockCycleColorIndex = CYCLING_BLOCK_COLORS.length-1;
        Character.UnicodeBlock currentBlock = null;
        for (int i = 0; i < codepoints.length; i++) {
            Character.UnicodeBlock newBlock = Character.UnicodeBlock.of(codepoints[i]);
            if (newBlock != currentBlock) {
                blockCycleColorIndex = (blockCycleColorIndex + 1) % CYCLING_BLOCK_COLORS.length;
                currentBlock = newBlock;
            }
            codepoints[i] |= (blockCycleColorIndex << 24);
        }
        
        this.refreshButtons();
    }

    public void refreshButtons() {
        // TODO use scrollbar widget
        this.clearElements();
        int codepointIndex = scroll*columns;
        int widgetIndex = 0;
        IntUnaryOperator widthGetter = ((TextRendererAccess) textRenderer).getCodepointWidthGetter(Style.EMPTY);
        while(codepointIndex < codepoints.length && widgetIndex < columns*visibleRows) {
            int value = codepoints[codepointIndex];
            int codePoint = value & 0x00FFFFFF;
            int blockColor = CYCLING_BLOCK_COLORS[(value & 0xFF000000) >> 24];
            String symbol = Util.stringFromCodePoint(codePoint);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);

            // TODO make this translatable
            Tooltip tooltip = Tooltip.of(Text.empty()
                    .append(Text.literal(Integer.toHexString(codePoint)))
                    .append("\n\n" + Util.getCapitalizedSymbolName(codePoint) + "\n")
                    .append("Width: " + widthGetter.applyAsInt(codePoint) + "\n")
                    .append(block == null ? Text.literal("UNKNOWN BLOCK").formatted(Formatting.GRAY) : Text.literal(block.toString()).styled(style -> style.withColor(blockColor))));

            int x = widgetIndex % columns * GRID_SPCAING + 1;
            int y = widgetIndex / columns * GRID_SPCAING + 1;
            TableButton button = new TableButton(getX()+x, getY()+y, this, symbol, tooltip, codepointIndex, blockColor);

            this.addChildren(button);
            
            codepointIndex++;
            widgetIndex++;
        }
        
        this.onRefreshed();
    }

    protected void onRefreshed() {}

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll -= ((int) verticalAmount * (Screen.hasControlDown() ? visibleRows : 1));
        this.scroll = MathHelper.clamp(scroll, 0, Math.max(MathHelper.ceilDiv(codepoints.length, columns)-visibleRows, 0));
        this.refreshButtons();
        return true;
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

    public void copySelected() {
        if(!hasSelection()) return;
        MinecraftClient.getInstance().keyboard.setClipboard(Util.stringFromCodePoints(getSelectedSymbols()));
    }

    public void favoriteSymbols() {
        if(!hasSelection()) return;
        IntStream selectedSymbols = getSelectedSymbols();
        SymbolChat.config.toggleFavorite(selectedSymbols.mapToObj(Character::toString));
        refreshButtons();
    }

    // TODO does copy and fav work?

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

    private class TableButton extends PasteSymbolButtonWidget {
        private final int index;
        private final boolean marked;
        private final int blockColor;

        public TableButton(int x, int y, Context context, String symbol, Tooltip tooltip, int index, int blockColor) {
            super(x, y, context, symbol, tooltip);
            this.index = index;
            this.marked = index >= selectionStart && index <= selectionEnd;
            this.blockColor = blockColor;
        }

        @Override
        protected int getBackgroundColor() {
            // when not hovered, use halved RGB values
            return this.isHovered() ? (showBlocks ? blockColor : SymbolChat.config.getButtonHoverColor()) : ColorHelper.Argb.mixColor(blockColor, 0xFF808080);
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
}







