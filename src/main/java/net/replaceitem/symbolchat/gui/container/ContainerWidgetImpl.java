package net.replaceitem.symbolchat.gui.container;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ContainerWidgetImpl extends ContainerWidget {
    private final List<Element> children = new ArrayList<>();
    
    public ContainerWidgetImpl(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }
    
    public void addChildren(Element element) {
        this.children.add(element);
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        for (Element child : this.children) {
            if(child instanceof Widget widget) widget.forEachChild(consumer);
        }
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        for (Element child : this.children) {
            if(child instanceof Drawable drawable) drawable.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.isMouseOver(mouseX, mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        Element focused = getFocused();
        if(focused instanceof Narratable narratable) narratable.appendNarrations(builder);
    }

    @Override
    public void setFocused(boolean focused) {
        Element focusedElement = this.getFocused();
        if(focusedElement != null) focusedElement.setFocused(focused);
    }

    @Override
    public List<? extends Element> children() {
        return this.children;
    }
    
    public void clearElements() {
        this.children.clear();
    }
}
