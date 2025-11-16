package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import java.util.function.Consumer;

public class IntSpinnerWidget extends GridLayout {
    private final int min, max;
    private final EditBox textField;
    private final Button decreaseButton;
    private final Button increaseButton;
    @Nullable
    private Consumer<OptionalInt> changedListener;

    private IntSpinnerWidget(Font textRenderer, int value, int min, int max) {
        this.min = min;
        this.max = max;
        
        GridLayout.RowHelper adder = this.createRowHelper(Integer.MAX_VALUE);

        decreaseButton = Button.builder(Component.nullToEmpty("<"), createEvent(-1)).size(12, 12).build();
        increaseButton = Button.builder(Component.nullToEmpty(">"), createEvent(1)).size(12, 12).build();

        textField = new EditBox(textRenderer, 0, 0, 20, 12, Component.empty());
        textField.setMaxLength(Math.max(String.valueOf(min).length(), String.valueOf(max).length()));
        textField.setResponder(string -> {
            OptionalInt val = getValue();
            decreaseButton.active = val.isEmpty() || val.getAsInt() != min;
            increaseButton.active = val.isEmpty() || val.getAsInt() != max;
            if(changedListener != null) changedListener.accept(getValue());
        });
        this.setValue(value);
        
        adder.addChild(decreaseButton);
        adder.addChild(textField);
        adder.addChild(increaseButton);
    }
    
    public void setChangeListener(@Nullable Consumer<OptionalInt> changedListener) {
        this.changedListener = changedListener;
    }
    
    public void setValue(int value) {
        textField.setValue(String.valueOf(Mth.clamp(value, min, max)));
    }
    
    public void setValue(@NotNull String value) {
        textField.setValue(value);
    }
    
    public OptionalInt getValue() {
        try {
            return OptionalInt.of(Integer.parseInt(textField.getValue()));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
    
    
    private Button.OnPress createEvent(int delta) {
        return button -> setValue(getValue().orElse(0) + delta);
    }
    
    public static Builder builder(@NotNull Font textRenderer) {
        return new Builder(textRenderer);
    }

    public static class Builder {
        @NotNull
        private final Font textRenderer;
        @Nullable
        private Consumer<OptionalInt> changedListener;
        private Integer value = 0;
        @Nullable
        private String stringValue = null;
        private int min = Integer.MIN_VALUE;
        private int max = Integer.MAX_VALUE;

        public Builder(@NotNull Font textRenderer) {
            this.textRenderer = textRenderer;
        }
        
        public Builder min(int min) {
            this.min = min;
            return this;
        }

        public Builder max(int max) {
            this.max = max;
            return this;
        }
        
        public Builder value(int value) {
            this.value = value;
            return this;
        }

        public Builder value(@Nullable String value) {
            this.stringValue = value;
            return this;
        }

        public Builder changedListener(@Nullable Consumer<OptionalInt> changedListener) {
            this.changedListener = changedListener;
            return this;
        }

        public IntSpinnerWidget build() {
            IntSpinnerWidget intSpinnerWidget = new IntSpinnerWidget(textRenderer, value, min, max);
            if(stringValue != null) intSpinnerWidget.setValue(stringValue);
            else intSpinnerWidget.setValue(value);
            intSpinnerWidget.setChangeListener(changedListener);
            return intSpinnerWidget;
        }
    }
}
