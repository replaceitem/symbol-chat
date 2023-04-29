package net.replaceitem.symbolchat.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.Util;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UnicodeTableScreen extends Screen {
    public UnicodeTableScreen() {
        super(Text.of("Unicode Table"));
    }

    private TextFieldWidget pageTextField;
    private TextFieldWidget searchTextField;
    private CheckboxWidget showBlocksWidget;
    
    int page = 0;

    // leftmost byte shows the block color
    private final List<Integer> codePoints = new ArrayList<>();
    
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
        
        int widgetX = 2;

        TextWidget pageTextWidget = new TextWidget(Text.of("Page"), this.textRenderer);
        pageTextWidget.setX(widgetX);
        pageTextWidget.setY(4);
        this.addDrawableChild(pageTextWidget);
        widgetX += pageTextWidget.getWidth() + 2;
        
        pageTextField = new TextFieldWidget(this.textRenderer, widgetX, 2, 40, 12, Text.empty());
        pageTextField.setText("0");
        pageTextField.setChangedListener(s -> {
            try {
                page = Integer.parseInt(this.pageTextField.getText());
            } catch (NumberFormatException e) {
                page = 0;
            }
            this.reloadSymbols();
        });
        this.addDrawableChild(pageTextField);
        widgetX += pageTextField.getWidth() + 2;

        TextWidget searchTextWidget = new TextWidget(Text.of("Search"), this.textRenderer);
        searchTextWidget.setX(widgetX);
        searchTextWidget.setY(4);
        this.addDrawableChild(searchTextWidget);
        widgetX += searchTextWidget.getWidth() + 2;

        searchTextField = new TextFieldWidget(this.textRenderer, widgetX, 2, 150, 12, Text.of(""));
        searchTextField.setChangedListener(s -> {
            this.reloadSymbols();
        });
        this.addDrawableChild(searchTextField);
        widgetX += searchTextField.getWidth() + 2;

        showBlocksWidget = new CheckboxWidget(widgetX, 2, 100, 20, Text.of("Show Blocks"), false) {
            @Override
            public void onPress() {
                super.onPress();
                refreshButtons();
            }
        };
        this.addDrawableChild(showBlocksWidget);
        widgetX += showBlocksWidget.getWidth() + 2;
        
        this.reloadSymbols();
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
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        for (PasteSymbolButtonWidget widget : widgets) {
            widget.render(context, mouseX, mouseY, delta);
        }
        int scrollbarRows = Math.max(MathHelper.ceilDiv(codePoints.size(), columns), screenRows);
        double visibleRatio = (double) screenRows / scrollbarRows;
        int scrollbarHeight = (int) (visibleRatio * (height +30));
        int scrollbarY = 30 + (int) MathHelper.clampedMap(scroll, 0, scrollbarRows, 0, height);
        context.fill(width-2, scrollbarY, width-1, scrollbarY+scrollbarHeight, 0xFFA0A0A0);
    }

    private void reloadSymbols() {
        this.searchCodePoints();
        this.refreshButtons();
    }

    private void searchCodePoints() {
        codePoints.clear();
        Character.UnicodeBlock currentBlock = null;
        int blockCycleColorIndex = CYCLING_BLOCK_COLORS.length-1;
        String search = searchTextField.getText().toUpperCase(Locale.ROOT);
        if(search.isBlank()) {
            int pageMask = page << 16;
            for (int i = 0; i <= 0xFFFF; i++) {
                int codePoint = pageMask | i;
                if(!Character.isValidCodePoint(codePoint)) break;
                Character.UnicodeBlock newBlock = Character.UnicodeBlock.of(codePoint);
                if (newBlock != currentBlock) {
                    blockCycleColorIndex = (blockCycleColorIndex + 1) % CYCLING_BLOCK_COLORS.length;
                    currentBlock = newBlock;
                }
                codePoint |= blockCycleColorIndex << 24;
                codePoints.add(codePoint);
            }
        } else {
            int codePoint = 0;
            while(Character.isValidCodePoint(codePoint)) {
                String name = Character.getName(codePoint);
                if(name != null && name.contains(search)) {
                    Character.UnicodeBlock newBlock = Character.UnicodeBlock.of(codePoint);
                    if (newBlock != currentBlock) {
                        blockCycleColorIndex = (blockCycleColorIndex + 1) % CYCLING_BLOCK_COLORS.length;
                        currentBlock = newBlock;
                    }
                    int savedCodePoint = codePoint | (blockCycleColorIndex << 24);
                    codePoints.add(savedCodePoint);
                }
                codePoint++;
            }
        }
    } 

    private void refreshButtons() {
        this.columns = this.width / SymbolButtonWidget.GRID_SPCAING;
        this.screenRows = (this.height-30) / SymbolButtonWidget.GRID_SPCAING;
        scroll = MathHelper.clamp(scroll, 0, Math.max(MathHelper.ceilDiv(codePoints.size(), columns)-screenRows, 0));
        this.widgets.clear();
        int x = 0, y = 30;
        int index = scroll*columns;
        while(index < codePoints.size()) {
            int value = codePoints.get(index);
            int codePoint = value & 0x00FFFFFF;
            
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toHexString(codePoint)).append('\n');
            sb.append(Util.getCapitalizedSymbolName(codePoint)).append('\n');
            Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
            sb.append(block == null ? "UNKNOWN BLOCK" : block.toString());
            PasteSymbolButtonWidget button = new PasteSymbolButtonWidget(x, y, System.out::println, Util.stringFromCodePoint(codePoint), Tooltip.of(Text.of(sb.toString())));
            if(showBlocksWidget.isChecked()) {
                button.setBackgroundColors(CYCLING_BLOCK_COLORS[(value & 0xFF000000) >> 24]);
            }
            this.widgets.add(button);
            x += SymbolButtonWidget.GRID_SPCAING;
            if(x > width-SymbolButtonWidget.SYMBOL_SIZE) {
                x = 0;
                y += SymbolButtonWidget.GRID_SPCAING;
            }
            if(y >= height) break;
            index++;
        }
    }

    boolean ctrlPressed = false;

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_LEFT_CONTROL) ctrlPressed = true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_LEFT_CONTROL) ctrlPressed = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll -= ((int) amount * (ctrlPressed?16:1));
        this.refreshButtons();
        return true;
    }

    @Override
    public void renderBackground(DrawContext context) {
        context.fill(0, 0, this.width, this.height, 0xFF101010);
    }
}
