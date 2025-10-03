package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.replaceitem.symbolchat.SymbolChat;
import net.replaceitem.symbolchat.gui.container.NonScrollableContainerWidget;
import net.replaceitem.symbolchat.gui.container.ScrollableGridContainer;
import net.replaceitem.symbolchat.gui.container.SmoothScrollableContainerWidget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DropDownWidget<T> extends NonScrollableContainerWidget implements Drawable, Element, Narratable {
    public static final int DROPDOWN_HEIGHT = 150;
    public final List<DropDownElementWidget> elements;
    private final Button buttonWidget;
    private final ScrollableGridContainer scrollableGridWidget;
    public int selected;
    public boolean expanded;

    public DropDownWidget(int x, int y, int width, int height, List<T> elementList, @Nullable T defaultSelection, boolean upward) {
        super(x, y, width, height);
        this.expanded = false;
        this.elements = new ArrayList<>();
        this.buttonWidget = new Button(x, y, width, height);
        this.scrollableGridWidget = new ScrollableGridContainer(
                this.getX() + 1 ,
                upward ? this.getY() - 1 - DROPDOWN_HEIGHT : this.getBottom() + 1, 
                this.width - 2, DROPDOWN_HEIGHT, 1
        ) {
            @Override
            protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                super.renderWidget(context, mouseX, mouseY, delta);
            }
        };
        this.scrollableGridWidget.setScrollbarStyle(SmoothScrollableContainerWidget.ScrollbarStyle.SLIM);
        this.scrollableGridWidget.setSmoothScrolling(true);
        this.scrollableGridWidget.visible = this.expanded;
        for(int i = 0; i < elementList.size(); i++) {
            DropDownElementWidget element = new DropDownElementWidget(0, 0, this.width - 2 - this.scrollableGridWidget.getScrollbarThumbWidth(), this.height, elementList.get(i), i);
            this.elements.add(element);
            this.scrollableGridWidget.add(element);
        }
        this.scrollableGridWidget.refreshPositions();
        this.addChildren(buttonWidget);
        this.addChildren(scrollableGridWidget);
        this.selected = defaultSelection == null ? 0 : Math.max(elementList.indexOf(defaultSelection), 0);
    }

    private void toggleVisible() {
        this.expanded = !this.expanded;
        this.scrollableGridWidget.visible = this.expanded;
    }
    
    private ScreenRect getExpandedArea() {
        return new ScreenRect(scrollableGridWidget.getX()-1, scrollableGridWidget.getY()-1, scrollableGridWidget.getWidth()+2, scrollableGridWidget.getHeight()+2);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        ScreenRect expandedArea = getExpandedArea();
        if(expanded) context.fill(expandedArea.getLeft(), expandedArea.getTop(), expandedArea.getRight(), expandedArea.getBottom(), SymbolChat.config.buttonColor.get());
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || (expanded && getExpandedArea().contains((int) mouseX, (int) mouseY));
    }

    public void changeSelected(int index) {
        this.selected = index;
        if(this.expanded) toggleVisible();
        this.onSelection(index, elements.get(index).getElement());
    }

    public void onSelection(int index, T element) {}

    public T getSelection() {
        return this.elements.get(selected).getElement();
    }
    
    class Button extends ButtonWidget {

        protected Button(int x, int y, int width, int height) {
            super(x, y, width, height, Text.empty(), button -> DropDownWidget.this.toggleVisible(), DEFAULT_NARRATION_SUPPLIER);
        }

        private int getBackgroundColor() {
            return this.isHovered() ? SymbolChat.config.buttonActiveColor.get() : SymbolChat.config.buttonColor.get();
        }

        @Override
        public Text getMessage() {
            return DropDownWidget.this.elements.get(DropDownWidget.this.selected).getMessage();
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            int color = this.isHovered() ? 16777215 : 10526880;
            context.fill(getX(), getY(), getRight(), getBottom(), getBackgroundColor());
            this.drawMessage(context, minecraftClient.textRenderer, color | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }

    public class DropDownElementWidget extends ClickableWidget implements Drawable, Element, Narratable {
        private final T element;
        private final int index;
    
        public DropDownElementWidget(int x, int y, int width, int height, T element, int index) {
            super(x, y, width, height, Text.literal(element.toString()));
            this.element = element;
            this.index = index;
        }
        
        private int getBackgroundColor() {
            return this.isHovered() ? SymbolChat.config.buttonActiveColor.get() : SymbolChat.config.buttonColor.get();
        }
    
        @Override
        public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
            if (this.visible) {
                drawContext.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, getBackgroundColor());
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                int z = this.isHovered() ? 16777215 : 10526880;
                drawContext.drawCenteredTextWithShadow(textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, z | MathHelper.ceil(this.alpha * 255.0F) << 24);
            }
        }

        @Override
        public void onClick(Click click, boolean doubled) {
            DropDownWidget.this.changeSelected(this.index);
        }

        @Override
        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
            builder.put(NarrationPart.HINT, "Dropdown element: " + element.toString());
        }
    
        public T getElement() {
            return element;
        }
    }
}
