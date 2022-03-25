package symbolchat.symbolchat.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolchat.symbolchat.FontProcessor;
import symbolchat.symbolchat.FontProcessorAccessor;
import symbolchat.symbolchat.SymbolChat;
import symbolchat.symbolchat.widget.DropDownWidget;
import symbolchat.symbolchat.widget.FontSelectionDropDownWidget;
import symbolchat.symbolchat.widget.symbolButton.OpenSymbolPanelButtonWidget;
import symbolchat.symbolchat.widget.symbolButton.SymbolButtonWidget;
import symbolchat.symbolchat.SymbolInsertable;
import symbolchat.symbolchat.SymbolSelectionPanel;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements SymbolInsertable, FontProcessorAccessor {
    @Shadow protected TextFieldWidget chatField;

    private SymbolButtonWidget symbolButtonWidget;
    private SymbolSelectionPanel symbolSelectionPanel;
    private DropDownWidget<FontProcessor> fontSelectionDropDown;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void addSymbolChatComponents(CallbackInfo ci) {
        int symbolButtonX = this.width-2-SymbolButtonWidget.symbolSize;
        int symbolButtonY = this.height-2-SymbolButtonWidget.symbolSize;
        this.symbolSelectionPanel = new SymbolSelectionPanel(this,this.width-SymbolSelectionPanel.width-2,symbolButtonY-2-SymbolSelectionPanel.height);
        this.symbolButtonWidget = new OpenSymbolPanelButtonWidget(this, symbolButtonX, symbolButtonY, this.symbolSelectionPanel);
        this.fontSelectionDropDown = new FontSelectionDropDownWidget(this.width-82, 2, 80, 15, FontProcessor.fontProcessors, SymbolChat.selectedFont);
    }

    @ModifyConstant(method = "init",constant = @Constant(intValue = 4, ordinal = 1),require = 1)
    private int changeTextBoxWidth(int original) {
        return original + SymbolButtonWidget.symbolSize + 8;
    }
    @ModifyConstant(method = "render",constant = @Constant(intValue = 2, ordinal = 1),require = 1)
    private int changeChatBoxFillWidth(int original) {
        return original + SymbolButtonWidget.symbolSize + 2;
    }

    @Inject(method = "mouseClicked", at = @At(value = "HEAD"), cancellable = true)
    private void symbolChatMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if(symbolSelectionPanel.mouseClicked(mouseX,mouseY,button)) cir.setReturnValue(true);
        if(symbolButtonWidget.mouseClicked(mouseX,mouseY,button)) cir.setReturnValue(true);
        if(fontSelectionDropDown.mouseClicked(mouseX,mouseY,button)) cir.setReturnValue(true);
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void renderSymbolChat(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        symbolButtonWidget.render(matrices,mouseX,mouseY,delta);
        symbolSelectionPanel.render(matrices, mouseX, mouseY, delta);
        fontSelectionDropDown.render(matrices,mouseX,mouseY,delta);
    }

    @Override
    public void insertSymbol(String symbol) {
        this.chatField.write(symbol);
    }

    @Override
    public FontProcessor getFontProcessor() {
        return this.fontSelectionDropDown.getSelection();
    }
}
