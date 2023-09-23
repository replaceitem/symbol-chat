package net.replaceitem.symbolchat.gui;

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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.Util;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class UnicodeTableScreen extends Screen {
    public UnicodeTableScreen(Screen parent) {
        super(Text.of("Unicode Table"));
        this.parent = parent;
    }

    private final Screen parent;

    private TextFieldWidget pageTextField;
    private TextFieldWidget searchTextField;
    private CheckboxWidget showBlocksWidget;
    
    private int selectionStart = -1;
    private int selectionEnd = -1;
    
    int page = 0;
    
    public static final int TOOLBAR_HEIGHT = 40;

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

        GridWidget gridWidget1 = new GridWidget(2,2);
        gridWidget1.setColumnSpacing(2);
        GridWidget.Adder adder1 = gridWidget1.createAdder(Integer.MAX_VALUE);

        TextWidget pageTextWidget = new TextWidget(Text.translatable("symbolchat.page"), this.textRenderer);
        pageTextWidget.setX(0);
        pageTextWidget.setY(0);
        adder1.add(pageTextWidget);
        
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
        adder1.add(pageTextField);
        
        adder1.add(EmptyWidget.ofWidth(40));

        TextWidget searchTextWidget = new TextWidget(Text.translatable("symbolchat.search"), this.textRenderer);
        searchTextWidget.setX(0);
        searchTextWidget.setY(0);
        adder1.add(searchTextWidget);

        searchTextField = new TextFieldWidget(this.textRenderer, 0, 0, 150, 12, Text.of(""));
        searchTextField.setChangedListener(s -> this.reloadSymbols());
        adder1.add(searchTextField);
        
        adder1.add(EmptyWidget.ofWidth(40));

        showBlocksWidget = new CheckboxWidget(0, 0, 60, 20, Text.translatable("symbolchat.show_blocks"), false) {
            @Override
            public void onPress() {
                super.onPress();
                refreshButtons();
            }
        };
        adder1.add(showBlocksWidget);

        gridWidget1.refreshPositions();
        gridWidget1.forEachChild(this::addDrawableChild);


        GridWidget gridWidget2 = new GridWidget(2,18);
        GridWidget.Adder adder2 = gridWidget2.createAdder(Integer.MAX_VALUE);

        ButtonWidget copySelectedButton = ButtonWidget.builder(Text.literal("📋 ").append(Text.translatable("symbolchat.copy_selected")), button -> copySelected()).dimensions(0, 0, 100, 20).build();
        adder2.add(copySelectedButton);

        ButtonWidget favoriteSymbolButton = ButtonWidget.builder(Text.literal("✩ ").append(Text.translatable("symbolchat.favorite_symbol")), button -> favoriteSymbols()).dimensions(0, 0, 140, 20).build();
        adder2.add(favoriteSymbolButton);
        if(!SymbolChat.clothConfigEnabled) {
            favoriteSymbolButton.active = false;
            favoriteSymbolButton.setTooltip(Tooltip.of(Text.translatable("symbolchat.no_clothconfig")));
        }
        
        gridWidget2.refreshPositions();
        gridWidget2.forEachChild(this::addDrawableChild);
        
        this.reloadSymbols();
    }

    private IntStream getSelectedSymbols() {
        IntStream.Builder intStreamBuilder = IntStream.builder();
        for(int i = selectionStart; i <= selectionEnd; i++) {
            Integer codepoint = this.codePoints.get(i);
            if(codepoint == null) break;
            intStreamBuilder.add(codepoint & 0x00FFFFFF);
        }
        return intStreamBuilder.build();
    }
    
    private void favoriteSymbols() {
        if(selectionStart == -1) return;
        IntStream selectedSymbols = getSelectedSymbols();
        SymbolChat.config.toggleFavorite(selectedSymbols);
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
        int scrollbarRows = Math.max(MathHelper.ceilDiv(codePoints.size(), columns), screenRows);
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
        this.searchCodePoints();
        this.refreshButtons();
    }

    private void searchCodePoints() {
        selectionStart = -1;
        selectionEnd = -1;
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
                if(name != null && isRelevant(name, search)) {
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
    
    private static boolean isRelevant(String name, String search) {
        for (String s : search.split(" ")) {
            if(!name.contains(s)) return false;
        }
        return true;
    }

    private void refreshButtons() {
        this.columns = this.width / SymbolButtonWidget.GRID_SPCAING;
        this.screenRows = (this.height-TOOLBAR_HEIGHT) / SymbolButtonWidget.GRID_SPCAING;
        scroll = MathHelper.clamp(scroll, 0, Math.max(MathHelper.ceilDiv(codePoints.size(), columns)-screenRows, 0));
        this.widgets.clear();
        int x = 1, y = TOOLBAR_HEIGHT;
        int index = scroll*columns;
        while(index < codePoints.size()) {
            int value = codePoints.get(index);
            int codePoint = value & 0x00FFFFFF;
            int blockColor = CYCLING_BLOCK_COLORS[(value & 0xFF000000) >> 24];

            Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
            
            Text tooltip = Text.empty()
                    .append(Text.literal(Integer.toHexString(codePoint)))
                    .append("\n\n" + Util.getCapitalizedSymbolName(codePoint) + "\n")
                    .append(block == null ? Text.literal("UNKNOWN BLOCK").formatted(Formatting.GRAY) : Text.literal(block.toString()).styled(style -> style.withColor(blockColor)));
                    
            int finalIndex = index;
            PasteSymbolButtonWidget button = new PasteSymbolButtonWidget(x, y, null, Util.stringFromCodePoint(codePoint), Tooltip.of(tooltip)) {
                
                @Override
                public boolean onClick(int button) {
                    if(Screen.hasShiftDown() && selectionStart != -1) {
                        if(selectionStart > finalIndex) {
                            selectionEnd = selectionStart;
                            selectionStart = finalIndex;
                        } else {
                            selectionEnd = finalIndex;
                        }
                    } else {
                        selectionStart = finalIndex;
                        selectionEnd = finalIndex;
                    }
                    refreshButtons();
                    return true;
                }
            };
            if(showBlocksWidget.isChecked()) {
                button.setBackgroundColors(blockColor);
            }
            if(index >= selectionStart && index <= selectionEnd) {
                button.setSelected(true);
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
    }


    @Override
    public void close() {
        this.client.setScreen(this.parent);
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
