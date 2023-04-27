package net.replaceitem.symbolchat.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.font.FontProcessor;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UnicodeTableScreen extends Screen {
    public UnicodeTableScreen() {
        super(Text.of("Unicode Table"));
    }

    private TextFieldWidget pageTextField;

    @Override
    protected void init() {
        super.init();
        this.columns = this.width / SymbolButtonWidget.SYMBOL_SIZE+1;
        this.pageRows = MathHelper.ceilDiv(0xFFFF, columns);
        this.screenRows = this.height / SymbolButtonWidget.SYMBOL_SIZE+1;
        pageTextField = new TextFieldWidget(this.textRenderer, 2, 2, 64, 12, Text.of("0"));
        pageTextField.setText("0");
        this.addDrawableChild(pageTextField);
        pageTextField.setChangedListener(s -> this.reloadSymbols());
        this.reloadSymbols();
    }

    private int scroll;
    private int columns;
    private int pageRows;
    private int screenRows;
    private final List<PasteSymbolButtonWidget> widgets = new ArrayList<>();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        for (PasteSymbolButtonWidget pasteSymbolButtonWidget : widgets) {
            pasteSymbolButtonWidget.render(context, mouseX, mouseY, delta);
        }

    }

    private void reloadSymbols() {
        this.widgets.clear();
        int page;
        try {
            page = Integer.parseInt(this.pageTextField.getText());
        } catch (NumberFormatException e) {
            return;
        }
        if(page > 10) return;
        int codepoint = scroll * columns;
        int x = 0, y = 30;
        page <<= 16;
        while(codepoint < 0xFFFF && y < height-20) {
            this.widgets.add(new PasteSymbolButtonWidget(x, y, System.out::println, FontProcessor.stringFromCodePoint(page | codepoint)) {
                @Override
                protected String getTooltipText() {
                    return this.symbol.codePoints().mapToObj(Integer::toHexString).collect(Collectors.joining()) + '\n' + super.getTooltipText();
                }
            });
            x += SymbolButtonWidget.SYMBOL_SIZE+1;
            if(x > width-SymbolButtonWidget.SYMBOL_SIZE) {
                x = 0;
                y += SymbolButtonWidget.SYMBOL_SIZE+1;
            }
            codepoint++;
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
        scroll = MathHelper.clamp(scroll - ((int) amount * (ctrlPressed?16:1)), 0, pageRows-screenRows);
        reloadSymbols();
        return true;
    }

    @Override
    public void renderBackground(DrawContext context) {
        context.fill(0, 0, this.width, this.height, 0xFF000000);
    }
}
