package net.replaceitem.symbolchat.mixin.widget;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.ScrollableTextFieldWidget;
import net.minecraft.client.input.CursorMovement;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.replaceitem.symbolchat.extensions.EditBoxWidgetExtension;
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

@Mixin(EditBoxWidget.class)
public abstract class EditBoxWidgetMixin extends ScrollableTextFieldWidget implements EditBoxWidgetExtension, SymbolEditableWidget {
    public EditBoxWidgetMixin(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }


    @Shadow @Final private EditBox editBox;
    @Shadow @Final private TextRenderer textRenderer;

    @Shadow public abstract String getText();

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
        this.editBox.replaceSelection(text);
    }

    @Unique
    @Override
    public ScreenPos getCursorPosition() {
        String string = this.editBox.getText();
        int cursor = this.editBox.getCursor();
        int lineY = this.getTextY();
        for(EditBox.Substring substring : this.editBox.getLines()) {
            int textX = this.getTextX();
            if (cursor >= substring.beginIndex() && cursor <= substring.endIndex()) {
                String textBeforeCursor = string.substring(substring.beginIndex(), cursor);
                int cursorX = textX + this.textRenderer.getWidth(textBeforeCursor);
                return new ScreenPos(cursorX, lineY - 1);
            }
            lineY += 9;
        }
        return new ScreenPos(getTextX(), lineY);
    }

    @WrapOperation(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/EditBox;replaceSelection(Ljava/lang/String;)V"))
    private void wrapReplaceSelection(EditBox instance, String string, Operation<Void> original) {
        boolean convertFont = convertFontsPredicate == null || convertFontsPredicate.apply(this.getText(), string);
        FontProcessor fontProcessor = !convertFont || fontProcessorSupplier == null ? null : fontProcessorSupplier.get();
        if(fontProcessor != null) {
            string = fontProcessor.convertString(string);
        }
        original.call(instance, string);
        if(fontProcessor != null && fontProcessor.isReverseDirection()) {
            instance.setSelecting(false);
            instance.moveCursor(CursorMovement.RELATIVE, -string.length());
        }
    }

    @Inject(method = "onCursorChange", at = @At("TAIL"))
    private void refreshSuggestionsAfterCursorChange(CallbackInfo ci) {
        if (refreshSuggestions != null) {
            refreshSuggestions.run();
        }
    }

    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void afterKeyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        if (refreshSuggestions != null) {
            refreshSuggestions.run();
        }
    }
}
