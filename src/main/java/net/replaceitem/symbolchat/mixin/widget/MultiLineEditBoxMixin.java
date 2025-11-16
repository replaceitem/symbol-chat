package net.replaceitem.symbolchat.mixin.widget;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.replaceitem.symbolchat.extensions.MultilineEditBoxExtension;
import net.replaceitem.symbolchat.extensions.SymbolEditableWidget;
import net.replaceitem.symbolchat.resource.FontProcessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@Mixin(MultiLineEditBox.class)
public abstract class MultiLineEditBoxMixin extends AbstractTextAreaWidget implements MultilineEditBoxExtension, SymbolEditableWidget {
    public MultiLineEditBoxMixin(int i, int j, int k, int l, Component text) {
        super(i, j, k, l, text);
    }


    @Shadow @Final private MultilineTextField textField;
    @Shadow @Final private Font font;

    @Shadow public abstract String getValue();

    @Unique @Nullable private Supplier<FontProcessor> fontProcessorSupplier;
    @Unique @Nullable private BiFunction<String, @Nullable String, Boolean> convertFontsPredicate;
    @Unique @Nullable private Runnable refreshSuggestions;

    @Unique
    @Override
    public void setFontProcessorSupplier(@Nullable Supplier<FontProcessor> fontProcessorSupplier) {
        this.fontProcessorSupplier = fontProcessorSupplier;
    }

    @Override
    public void setConvertFontsPredicate(@Nullable BiFunction<String, @Nullable String, Boolean> convertFontsPredicate) {
        this.convertFontsPredicate = convertFontsPredicate;
    }

    @Unique
    @Override
    public void setRefreshSuggestions(@Nullable Runnable refreshSuggestions) {
        this.refreshSuggestions = refreshSuggestions;
    }

    @Unique
    @Override
    public void insert(String text) {
        this.textField.insertText(text);
    }

    @Unique
    @Override
    public ScreenPosition getCursorPosition() {
        String string = this.textField.value();
        int cursor = this.textField.cursor();
        int lineY = this.getInnerTop();
        for(var substring : this.textField.iterateLines()) {
            int textX = this.getInnerLeft();
            if (cursor >= substring.beginIndex() && cursor <= substring.endIndex()) {
                String textBeforeCursor = string.substring(substring.beginIndex(), cursor);
                int cursorX = textX + this.font.width(textBeforeCursor);
                return new ScreenPosition(cursorX, lineY - 1);
            }
            lineY += 9;
        }
        return new ScreenPosition(getInnerLeft(), lineY);
    }

    @WrapOperation(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/MultilineTextField;insertText(Ljava/lang/String;)V"))
    private void wrapInsertText(MultilineTextField instance, String string, Operation<Void> original) {
        boolean convertFont = convertFontsPredicate == null || convertFontsPredicate.apply(this.getValue(), string);
        FontProcessor fontProcessor = !convertFont || fontProcessorSupplier == null ? null : fontProcessorSupplier.get();
        if(fontProcessor != null) {
            string = fontProcessor.convertString(string);
        }
        original.call(instance, string);
        if(fontProcessor != null && fontProcessor.isReverseDirection()) {
            instance.setSelecting(false);
            instance.seekCursor(Whence.RELATIVE, -string.length());
        }
    }

    @Inject(method = "scrollToCursor", at = @At("TAIL"))
    private void refreshSuggestionsAfterCursorChange(CallbackInfo ci) {
        if (refreshSuggestions != null) {
            refreshSuggestions.run();
        }
    }

    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void afterKeyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (refreshSuggestions != null) {
            refreshSuggestions.run();
        }
    }
}
