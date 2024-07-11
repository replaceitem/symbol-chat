package net.replaceitem.symbolchat.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import java.util.function.Consumer;

public class IntSpinnerWidget extends GridWidget {
    private final int min, max;
    private final TextFieldWidget textField;
    private final ButtonWidget decreaseButton;
    private final ButtonWidget increaseButton;
    @Nullable
    private Consumer<OptionalInt> changedListener;

    private IntSpinnerWidget(TextRenderer textRenderer, int value, int min, int max) {
        this.min = min;
        this.max = max;
        
        GridWidget.Adder adder = this.createAdder(Integer.MAX_VALUE);

        decreaseButton = ButtonWidget.builder(Text.of("<"), createEvent(-1)).size(12, 12).build();
        increaseButton = ButtonWidget.builder(Text.of(">"), createEvent(1)).size(12, 12).build();

        textField = new TextFieldWidget(textRenderer, 0, 0, 20, 12, Text.empty());
        textField.setMaxLength(Math.max(String.valueOf(min).length(), String.valueOf(max).length()));
        textField.setChangedListener(string -> {
            OptionalInt val = getValue();
            decreaseButton.active = val.isEmpty() || val.getAsInt() != min;
            increaseButton.active = val.isEmpty() || val.getAsInt() != max;
            if(changedListener != null) changedListener.accept(getValue());
        });
        this.setValue(value);
        
        adder.add(decreaseButton);
        adder.add(textField);
        adder.add(increaseButton);
    }
    
    public void setChangeListener(@Nullable Consumer<OptionalInt> changedListener) {
        this.changedListener = changedListener;
    }
    
    public void setValue(int value) {
        textField.setText(String.valueOf(MathHelper.clamp(value, min, max)));
    }
    
    public void setValue(@NotNull String value) {
        textField.setText(value);
    }
    
    public OptionalInt getValue() {
        try {
            return OptionalInt.of(Integer.parseInt(textField.getText()));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
    
    
    private ButtonWidget.PressAction createEvent(int delta) {
        return button -> setValue(getValue().orElse(0) + delta);
    }
    
    public static Builder builder(@NotNull TextRenderer textRenderer) {
        return new Builder(textRenderer);
    }

    public static class Builder {
        @NotNull
        private final TextRenderer textRenderer;
        @Nullable
        private Consumer<OptionalInt> changedListener;
        private Integer value = 0;
        @Nullable
        private String stringValue = null;
        private int min = Integer.MIN_VALUE;
        private int max = Integer.MAX_VALUE;

        public Builder(@NotNull TextRenderer textRenderer) {
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
