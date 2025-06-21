package net.replaceitem.symbolchat.mixin.widget;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextFieldWidget.class)
public interface TextFieldWidgetAccessor {
    @Accessor
    int getFirstCharacterIndex();
    @Accessor
    int getSelectionStart();
    @Accessor
    int getSelectionEnd();
}
