package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SearchUtil;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import net.replaceitem.symbolchat.resource.SymbolManager;
import net.replaceitem.symbolchat.resource.SymbolTab;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SymbolSuggestor extends AbstractParentElement implements Drawable, Selectable, PasteSymbolButtonWidget.Context {
    
    private final Screen screen;
    private final SymbolInsertable symbolInsertable;
    private final SymbolSuggestable suggestable;
    
    public static final int HEIGHT = SymbolButtonWidget.SYMBOL_SIZE + 2;


    private int x, y;
    private int width;
    private boolean visible;
    private int focusedElement;
    private int elementCount;
    
    private final List<PasteSymbolButtonWidget> symbolButtons = new ArrayList<>();

    public SymbolSuggestor(Screen screen, SymbolInsertable symbolInsertable, SymbolSuggestable suggestable) {
        this.screen = screen;
        this.symbolInsertable = symbolInsertable;
        this.suggestable = suggestable;
        this.elementCount = 0;
        this.focusedElement = -1;
        visible = false;
    }

    @Override
    public void onSymbolClicked(String symbol) {
        symbolInsertable.insertSymbol(symbol);
    }

    @Override
    public void refresh() {
        Vector2i cursorPosition = this.suggestable.getCursorPosition();
        this.x = cursorPosition.x;
        this.y = cursorPosition.y - HEIGHT - 3;
        
        String search = this.suggestable.getSuggestionTerm();

        
        int fittingSymbols = Math.floorDiv(this.screen.width, SymbolButtonWidget.SYMBOL_SIZE + 1);
        int shownSymbols = Math.min(fittingSymbols, SymbolChat.config.getMaxSymbolSuggestions());
        
        symbolButtons.clear();

        if(search == null) {
            elementCount = 0;
        } else {
            Stream<String> searchStream = search.isBlank() ?
                    SymbolChat.symbolManager.getFavoriteSymbols() :
                    SearchUtil.performSearch(SymbolChat.symbolManager.getTab(SymbolManager.ALL_IDENTIFIER).map(SymbolTab::streamSymbols).orElseGet(Stream::of), search);
            List<String> symbols = searchStream.limit(shownSymbols).toList();
            elementCount = symbols.size();
            this.width = 1 + (SymbolButtonWidget.SYMBOL_SIZE + 1) * elementCount;
            this.x = Math.max(Math.min(this.x, this.screen.width - this.width), 0);
            for (int i = 0; i < elementCount; i++) {
                symbolButtons.add(new PasteSymbolButtonWidget(this.x+1+i*(SymbolButtonWidget.SYMBOL_SIZE+1), this.y+1, this, symbols.get(i)));
            }
        }
        visible = elementCount != 0;
        setFocused(visible && isFocused());
    }
    
    private void setFocusedElement(int focused) {
        this.focusedElement = MathHelper.clamp(focused, -1, elementCount-1);
        PasteSymbolButtonWidget focus = focusedElement == -1 ? null : this.symbolButtons.get(focusedElement);
        this.setFocused(focus);
    }

    @Nullable
    @Override
    public Element getFocused() {
        if(this.focusedElement < 0 || this.focusedElement >= this.symbolButtons.size()) return null;
        return this.symbolButtons.get(this.focusedElement);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if(!visible) return;
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0.0, 0.0, 350.0);
        drawContext.fill(this.x, this.y, this.x+width, this.y+HEIGHT, SymbolChat.config.getHudColor());
        for (Drawable drawable : symbolButtons) {
            drawable.render(drawContext, mouseX, mouseY, delta);
        }
        drawContext.getMatrices().pop();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_TAB) {
            PasteSymbolButtonWidget focused;
            if(this.getFocused() instanceof PasteSymbolButtonWidget pasteSymbolButtonWidget) {
                focused = pasteSymbolButtonWidget;
            } else if(!this.symbolButtons.isEmpty()) {
                focused = this.symbolButtons.get(0);
            } else return false;
            focused.onClick(0);
            this.hide();
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_UP) {
            if(this.getFocused() == null && !this.symbolButtons.isEmpty()) this.setFocusedElement(0);
            return true;
        }
        
        if(keyCode == GLFW.GLFW_KEY_RIGHT && this.getFocused() != null) {
            this.setFocusedElement(this.focusedElement+1);
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_LEFT && this.getFocused() != null) {
            if(this.focusedElement != 0) this.setFocusedElement(this.focusedElement-1);
            return true;
        }
        if((keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_ESCAPE) && this.getFocused() != null) {
            this.setFocusedElement(-1);
            return true;
        }
        
        if(keyCode == GLFW.GLFW_KEY_ENTER && this.getFocused() != null && focusedElement >= 0) {
            int buttonIndex = MathHelper.clamp(focusedElement, 0, this.symbolButtons.size()-1);
            this.symbolButtons.get(buttonIndex).onClick(0);
            this.hide();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private void hide() {
        this.setFocusedElement(-1);
        this.visible = false;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        
    }

    @Override
    public List<? extends Element> children() {
        return symbolButtons;
    }

    public boolean isVisible() {
        return visible;
    }
}
