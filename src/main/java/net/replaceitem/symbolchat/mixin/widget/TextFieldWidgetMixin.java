package net.replaceitem.symbolchat.mixin.widget;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.replaceitem.symbolchat.extensions.SymbolEditableWidget;
import net.replaceitem.symbolchat.resource.FontProcessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin implements SymbolEditableWidget {

    @Unique @Nullable private Supplier<FontProcessor> fontProcessorSupplier;
    @Unique @Nullable private Runnable refreshSuggestions;

    @Inject(method = "write", at = @At(value = "HEAD"))
    private void beforeWrite(String text, CallbackInfo ci, @Local(argsOnly = true) LocalRef<String> textRef) {
        FontProcessor fontProcessor = fontProcessorSupplier == null ? null : fontProcessorSupplier.get();
        if(fontProcessor != null) {
            textRef.set(fontProcessor.convertString(text));
        }
    }

    @WrapWithCondition(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setSelectionStart(I)V"))
    private boolean modifySelectionStart(TextFieldWidget instance, int cursor) {
        FontProcessor fontProcessor = fontProcessorSupplier == null ? null : fontProcessorSupplier.get();
        return fontProcessor == null || !fontProcessor.isReverseDirection();
    }

    @Inject(method = "setSelectionEnd", at = @At("RETURN"))
    private void afterSetSelectionEnd(int cursor, CallbackInfo ci) {
        if (this.refreshSuggestions != null) {
            this.refreshSuggestions.run();
        }
    }

    @Override
    public void setFontProcessorSupplier(@Nullable Supplier<FontProcessor> fontProcessorSupplier) {
        this.fontProcessorSupplier = fontProcessorSupplier;
    }

    @Override
    public void setRefreshSuggestions(@Nullable Runnable refreshSuggestions) {
        this.refreshSuggestions = refreshSuggestions;
    }
}
