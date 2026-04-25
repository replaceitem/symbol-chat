package net.replaceitem.symbolchat.gui.container;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public class NonScrollableContainerWidget extends AbstractContainerWidget {
    private final List<AbstractWidget> children = new ArrayList<>();
    
    public NonScrollableContainerWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty(), AbstractScrollArea.defaultSettings(0));
    }
    
    public void addChildren(AbstractWidget element) {
        this.children.add(element);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        for (GuiEventListener child : this.children) {
            if(child instanceof Renderable drawable) drawable.extractRenderState(graphics, mouseX, mouseY, a);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        GuiEventListener focused = getFocused();
        if(focused instanceof NarrationSupplier narratable) narratable.updateNarration(builder);
    }

    @Override
    public List<AbstractWidget> children() {
        return this.children;
    }
    
    public void clearElements() {
        this.children.clear();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return this.getChildAt(mouseX, mouseY).filter(element -> element.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)).isPresent();
    }

    @Override
    public void setX(int x) {
        if(x == this.getX()) return;
        for (AbstractWidget child : children) {
            child.setX(child.getX() - this.getX() + x);
        }
        super.setX(x);
    }

    @Override
    public void setY(int y) {
        if(y == this.getY()) return;
        for (AbstractWidget child : children) {
            child.setY(child.getY() - this.getY() + y);
        }
        super.setY(y);
    }

    @Override
    protected int contentHeight() {
        return height;
    }

    @Override
    protected double scrollRate() {
        return 0;
    }

    @Override
    protected boolean scrollable() {
        return false;
    }
}
