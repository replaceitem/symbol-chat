package net.replaceitem.symbolchat.gui.container;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.replaceitem.symbolchat.DrawContextExtension;

import java.util.ArrayList;
import java.util.List;

public class ScrollableGridContainer extends ScrollableLayoutContainer<GridWidget> {
    private GridWidget.Adder adder;
    private final int columns;
    private int backgroundColor = 0;
    private final List<ClickableWidget> children = new ArrayList<>();

    public ScrollableGridContainer(int x, int y, int w, int h, int columns) {
        super(x, y, w, h, new GridWidget(x, y));
        this.columns = columns;
        this.clearElements();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void clearElements() {
        this.children.clear();
        this.layout = new GridWidget(getX(), getY());
        this.layout.setSpacing(1);
        this.adder = layout.createAdder(columns);
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        for (ClickableWidget child : children) {
            if(((DrawContextExtension) context).scissorOverlaps(child.getNavigationFocus())) {
                child.render(context, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return Screen.hasControlDown() ? 50 : 7;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        Element focused = getFocused();
        if(focused instanceof Narratable narratable) narratable.appendNarrations(builder);
    }

    public void add(ClickableWidget widget) {
        this.adder.add(widget);
        this.children.add(widget);
    }
}
