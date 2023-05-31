package net.replaceitem.symbolchat.font;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.replaceitem.symbolchat.ScreenAccess;
import net.replaceitem.symbolchat.Util;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.stream.Collectors;

public class FontProcessor {
    protected final String nameKey;
    protected final Function<String, String> codePointConverter;
    protected String convertedName;
    public FontProcessor(String nameKey, Function<String, String> codePointConverter) {
        this.nameKey = "symbolchat.font." + nameKey;
        this.codePointConverter = codePointConverter;
    }

    public String convertString(String string) {
        return string.codePoints().mapToObj(Util::stringFromCodePoint).map(codePointConverter).collect(Collectors.joining());
    }

    public String getConvertedName() {
        return this.convertString(I18n.translate(this.nameKey));
    }

    @Override
    public String toString() {
        if(convertedName == null) convertedName = getConvertedName();
        return convertedName;
    }
    
    @NotNull
    public static FontProcessor getCurrentScreenFontProcessor() {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (!(screen instanceof ScreenAccess screenAccess)) {
            return Fonts.NORMAL;
        }
        return screenAccess.getFontProcessor();
    }
}
