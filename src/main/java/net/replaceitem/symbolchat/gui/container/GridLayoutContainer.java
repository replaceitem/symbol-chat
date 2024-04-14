package net.replaceitem.symbolchat.gui.container;

import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Widget;

public class GridLayoutContainer extends LayoutContainer<GridWidget> {
    private GridWidget.Adder adder;
    private int spacing = 0;
    private final int columns;

    public GridLayoutContainer(int x, int y, int width, int height, int columns) {
        super(x, y, width, height, new GridWidget());
        this.columns = columns;
        this.adder = layout.createAdder(columns);
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
        this.layout.setSpacing(spacing);
    }

    @Override
    protected void addChildrenToLayout(Widget widget) {
        adder.add(widget);
    }

    @Override
    public void clearElements() {
        super.clearElements();
        this.layout = new GridWidget(getX(), getY());
        this.layout.setSpacing(spacing);
        this.adder = layout.createAdder(columns);
    }
}
