package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.util.Mth;
import net.replaceitem.symbolchat.SearchUtil;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.SymbolInsertable;
import net.replaceitem.symbolchat.SymbolSuggestable;
import net.replaceitem.symbolchat.gui.container.NonScrollableContainerWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.PasteSymbolButtonWidget;
import net.replaceitem.symbolchat.gui.widget.symbolButton.SymbolButtonWidget;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SymbolSuggestor extends NonScrollableContainerWidget implements PasteSymbolButtonWidget.Context {
    
    private final Screen screen;
    private final SymbolInsertable symbolInsertable;
    private final SymbolSuggestable suggestable;

    @Nullable
    private String previousSearchTerm = null;
    
    public static final int HEIGHT = SymbolButtonWidget.SYMBOL_SIZE + 2;

    public SymbolSuggestor(Screen screen, SymbolInsertable symbolInsertable, SymbolSuggestable suggestable) {
        super(0, 0, 0, HEIGHT);
        this.screen = screen;
        this.symbolInsertable = symbolInsertable;
        this.suggestable = suggestable;
        visible = false;
    }

    @Override
    public void onSymbolClicked(String symbol) {
        symbolInsertable.insertSymbol(symbol);
    }

    @Override
    public void refresh() {
        ScreenPosition cursorPosition = this.suggestable.getCursorPosition();
        this.setY(cursorPosition.y() - HEIGHT - 3);
        
        String search = this.suggestable.getSuggestionTerm();

        
        int fittingSymbols = Math.floorDiv(this.screen.width, SymbolButtonWidget.SYMBOL_SIZE + 1);
        int shownSymbols = Math.min(fittingSymbols, SymbolChat.config.maxSymbolSuggestions.get());
        
        if (!Objects.equals(previousSearchTerm, search)) {
            previousSearchTerm = search;
            children().clear();

            if(search != null) {
                Stream<String> searchStream = search.isBlank() ?
                        SymbolChat.symbolManager.getFavoriteSymbols() :
                        SearchUtil.performSearch(SymbolChat.symbolManager.streamAllSymbols(), search);
                List<String> symbols = searchStream.limit(shownSymbols).toList();
                this.width = 1 + SymbolButtonWidget.GRID_SPCAING * symbols.size();
                this.setX(Mth.clamp(this.getX(), 0, this.screen.width - this.width));
                for (int i = 0; i < symbols.size(); i++) {
                    children().add(new PasteSymbolButtonWidget(this.getX() + 1 + i * (SymbolButtonWidget.GRID_SPCAING), this.getY() + 1, this, symbols.get(i)) {
                        @Override
                        protected boolean isHighlighted() {
                            return this.isHoveredOrFocused();
                        }
                    });
                }
            }
        }
        
        if(!visible && !children().isEmpty()) {
            this.setX(cursorPosition.x());
        }
        
        visible = !children().isEmpty();
    }
    
//    private void setFocusedElement(int focused) {
//        if(focused >= this.elementCount) focused = -1;
//        this.focusedElement = MathHelper.clamp(focused, -1, elementCount-1);
//        PasteSymbolButtonWidget focus = focusedElement == -1 ? null : this.symbolButtons.get(focusedElement);
//        this.setFocused(focus);
//    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), SymbolChat.config.hudColor.get());
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if(!visible) return false;
        if(input.isCycleFocus()) {
            PasteSymbolButtonWidget focused;
            if(this.getFocused() instanceof PasteSymbolButtonWidget pasteSymbolButtonWidget) {
                focused = pasteSymbolButtonWidget;
            } else if(!this.children().isEmpty() && this.children().getFirst() instanceof PasteSymbolButtonWidget symbolButtonWidget) {
                focused = symbolButtonWidget;
            } else return false;
            focused.onSymbolClicked();
            this.hide();
            return true;
        }
        if(input.isUp()) {
            if(this.getFocused() == null && !this.children().isEmpty()) this.setFocused(this.children().getFirst());
            return true;
        }

        if(input.isRight() && this.getFocused() instanceof PasteSymbolButtonWidget pasteSymbolButtonWidget) {
            this.setFocused(children().get(Math.min(children().indexOf(pasteSymbolButtonWidget) + 1, children().size()-1)));
            return true;
        }
        if(input.isLeft() && this.getFocused() instanceof PasteSymbolButtonWidget pasteSymbolButtonWidget) {
            this.setFocused(children().get(Math.max(children().indexOf(pasteSymbolButtonWidget) - 1, 0)));
            return true;
        }
        if((input.isDown() || input.isEscape()) && this.getFocused() != null) {
            this.setFocused(null);
            return true;
        }

        if(input.isConfirmation() && this.getFocused() != null) {
            if(getFocused() instanceof PasteSymbolButtonWidget symbolButtonWidget) symbolButtonWidget.onSymbolClicked();
            this.hide();
            return true;
        }

        return false;
    }

    private void hide() {
        this.setFocused(null);
        this.visible = false;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        if (getFocused() instanceof NarrationSupplier narratable) {
            narratable.updateNarration(builder);
        }
    }
}
