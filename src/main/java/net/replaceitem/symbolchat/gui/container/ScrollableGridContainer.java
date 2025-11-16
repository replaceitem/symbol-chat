package net.replaceitem.symbolchat.gui.container;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;

public class ScrollableGridContainer extends ScrollableLayoutContainer<GridLayout> {
    private GridLayout.RowHelper adder;
    private final int columns;
    private final List<AbstractWidget> children = new ArrayList<>();

    public ScrollableGridContainer(int x, int y, int w, int h, int columns) {
        super(x, y, w, h, new GridLayout(x, y));
        this.columns = columns;
        this.clearElements();
    }

    public void clearElements() {
        this.children.clear();
        this.layout = new GridLayout(getX(), getY());
        this.layout.spacing(1);
        this.adder = layout.createRowHelper(columns);
    }

    @Override
    protected void renderContents(GuiGraphics context, int mouseX, int mouseY, float delta) {
        for (AbstractWidget child : children) {
            child.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        GuiEventListener focused = getFocused();
        if(focused instanceof NarrationSupplier narratable) narratable.updateNarration(builder);
    }

    public void add(AbstractWidget widget) {
        this.adder.addChild(widget);
        this.children.add(widget);
    }
}
