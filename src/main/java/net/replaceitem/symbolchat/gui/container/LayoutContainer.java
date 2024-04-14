package net.replaceitem.symbolchat.gui.container;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.WrapperWidget;

public abstract class LayoutContainer<T extends WrapperWidget> extends ContainerWidgetImpl {
    protected T layout;
    
    public LayoutContainer(int x, int y, int width, int height, T layout) {
        super(x, y, width, height);
        this.layout = layout;
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        layout.setX(x);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        layout.setY(y);
    }

    @Override
    public void addChildren(Element element) {
        super.addChildren(element);
        if(element instanceof Widget widget) addChildrenToLayout(widget);
    }
    
    public void refreshPositions() {
        layout.refreshPositions();
        this.width = layout.getWidth();
        this.height = layout.getHeight();
    }

    protected abstract void addChildrenToLayout(Widget widget);
}
