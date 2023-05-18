package net.replaceitem.symbolchat.font;

import net.minecraft.client.resource.language.I18n;

import java.util.ArrayList;
import java.util.List;

public class Fonts {

    public static List<FontProcessor> fontProcessors;



    public static FontProcessor NORMAL = new FontProcessor("normal", s -> s);

    public static FontProcessor SUPERSCRIPT = new MappedFontProcessor("superscript",
            new FontMapBuilder()
                    .putAlphabetUpper("ᴬᴮᶜᴰᴱꟳᴳᴴᴵᴶᴷᴸᴹᴺᴼᴾꟴᴿˢᵀᵁⱽᵂˣʸᶻ")
                    .putAlphabetLower("ᵃᵇᶜᵈᵉᶠᵍʰ𞁌ʲᵏˡᵐ𞀽ᵒᵖ𐞥ʳˢᵗᵘᵛʷˣʸᶻ")
                    .putNumbers("⁰¹²³⁴⁵⁶⁷⁸⁹")
                    .put('+','⁺')
                    .put('-','⁻')
                    .put('=','⁼')
                    .put('(','⁽')
                    .put(')','⁾')
                    .build()
    );

    public static FontProcessor SUBSCRIPT = new MappedFontProcessor("subscript",
            new FontMapBuilder()
                    .putAlphabetUpper("ₐ𞁓𞁞DₑբGₕᵢⱼ𞁚ₗₘ𞁝𞁜ₚQᵣₛₜ𞁢ᵥ𞁤𞁡𞁟Z")
                    .putAlphabetLower("ₐ𞁥𞁞ₔₑբ₉ₕᵢⱼₖₗₘₙₒₚqᵣₛₜᵤᵥ𞁤ₓᵧ₂")
                    .putNumbers("₀₁₂₃₄₅₆₇₈₉")
                    .put('+','₊')
                    .put('-','₋')
                    .put('=','₌')
                    .put('(','₍')
                    .put(')','₎')
                    .build()
    );


    public static FontProcessor CIRCLED = new MappedFontProcessor("circled",
            new FontMapBuilder()
                    .shiftSequence('1', 0x2460, 9)
                    .put('0', 0x24EA)
                    .shiftAlphabetUpper(0x24B6)
                    .shiftAlphabetLower(0x24D0)
                    .put('+','⊕')
                    .put('-','⊝')
                    .put('=','⊜')
                    .put('*','⊛')
                    .put('/','⊘')
                    .build()
    );

    public static FontProcessor NEGATIVE_CIRCLED = new MappedFontProcessor("negative_circled",
            new FontMapBuilder()
                    .shiftAlphabetUpper(0x1F150)
                    .shiftAlphabetLower(0x1F150)
                    .shiftSequence('1', 0x278A, 9)
                    .put('0', "⓿")
                    .build()
    );

    public static FontProcessor SQUARED = new MappedFontProcessor("squared",
            new FontMapBuilder()
                    .shiftAlphabetUpper(0x1F130)
                    .shiftAlphabetLower(0x1F130)
                    .build()
    );

    public static FontProcessor NEGATIVE_SQUARED = new MappedFontProcessor("negative_squared",
            new FontMapBuilder()
                    .shiftAlphabetUpper(0x1F170)
                    .shiftAlphabetLower(0x1F170)
                    .build()
    );

    public static FontProcessor REGIONAL_INDICATOR = new MappedFontProcessor("regional_indicator",
            new FontMapBuilder()
                    .shiftAlphabetUpper(0x1F1E6)
                    .shiftAlphabetLower(0x1F1E6)
                    .build()
    );
    
    public static FontProcessor INVERSE = new MappedFontProcessor("inverse",
            new FontMapBuilder()
                    .putNumbers("0ƖՇƐ߈ϛ9ㄥ86")
                    .putAlphabetUpper("ⱯᗺƆᗡƎℲ⅁HIſꞰꞀWNOԀῸᴚS⟘∩ɅMX⅄Z")
                    .putAlphabetLower("ɐqɔpǝɟɓɥᴉſʞꞁɯuodbɹsʇnʌʍxʎz")
                    .put('!','¡')
                    .put(',','\'')
                    .put('.','˙')
                    .put('?','¿')
                    .putSeperated(
                            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя",
                            "Ɐ","9","ᗺ","ɺ","▽","Ǝ","Ӭ",")|(","Ɛ","И","Ņ","Ʞ","┃ʃ","W","H","O","U","Ԁ","Ɔ","⟘","ɦ","Ф","╳","'|Ꞁ","Һ","|ꞀꞀ","'|ꞀꞀ","ᶐ","ıq","q","Є","О┫","ʁ","ɐ","g","ʚ","ɹ","ɓ","ǝ","ӭ","ж","ε","и","ņ","ʞ","v","ɯ","н","о","u","d","ɔ","w","ʎ","ȸ","х","╹n","һ","ʍ","╹ʍ","ꟼ.","ıꟼ","ꟼ","є","Ꙕ","ʁ"
                    )
                    .build()
    ) {
        @Override
        public String getConvertedName() {
            return this.convertString(new StringBuilder(I18n.translate(this.nameKey)).reverse().toString());
        }
    };

    public static FontProcessor FULLWIDTH = new MappedFontProcessor("fullwidth",
            new FontMapBuilder()
                    .shiftSequence('!', 0xFF01, '~'-'!'+1)
                    .build()
    );

    public static FontProcessor SMALL = new MappedFontProcessor("small",
            new FontMapBuilder()
                    .putAlphabetUpper("ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ")
                    .putAlphabetLower("ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ")
                    .build()
    );


    public static FontProcessor BRACKETS = new MappedFontProcessor("brackets",
            new FontMapBuilder()
                    .shiftSequence('1', 0x2474, 9)
                    .shiftAlphabetUpper(0x1F110)
                    .shiftAlphabetLower(0x249C)
                    .build()
    );

    public static FontProcessor MATHEMATICAL = new MappedFontProcessor("mathematical",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDDA0")
                    .shiftAlphabetLower("\uD835\uDDBA")
                    .shiftNumbers("\uD835\uDFE2")
                    .build()
    );

    public static FontProcessor MATHEMATICAL_BOLD = new MappedFontProcessor("mathematical_bold",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDDD4")
                    .shiftAlphabetLower("\uD835\uDDEE")
                    .shiftNumbers("\uD835\uDFEC")
                    .build()
    );

    public static FontProcessor MATHEMATICAL_ITALIC = new MappedFontProcessor("mathematical_italic",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDE08")
                    .shiftAlphabetLower("\uD835\uDE22")
                    .shiftNumbers("\uD835\uDFE2")
                    .build()
    );

    public static FontProcessor MATHEMATICAL_BOLD_ITALIC = new MappedFontProcessor("mathematical_bold_italic",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDE3C")
                    .shiftAlphabetLower("\uD835\uDE56")
                    .shiftNumbers("\uD835\uDFE2")
                    .build()
    );

    public static FontProcessor MATHEMATICAL_BOLD_SCRIPT = new MappedFontProcessor("mathematical_bold_script",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDCD0")
                    .shiftAlphabetLower("\uD835\uDCEA")
                    .shiftNumbers("\uD835\uDFE2")
                    .build()
    );

    public static FontProcessor MATHEMATICAL_DOUBLE_STRUCK = new MappedFontProcessor("mathematical_double_struck",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDD52") // identical to lower, since upper has some missing unifont chars
                    .shiftAlphabetLower("\uD835\uDD52")
                    .shiftNumbers("\uD835\uDFD8")
                    .build()
    );

    public static FontProcessor MATHEMATICAL_BOLD_FRAKTUR = new MappedFontProcessor("mathematical_bold_fraktur",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDD6C")
                    .shiftAlphabetLower("\uD835\uDD86")
                    .shiftNumbers("\uD835\uDFE2")
                    .build()
    );

    public static FontProcessor MATHEMATICAL_MONOSPACE = new MappedFontProcessor("mathematical_monospace",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDE70")
                    .shiftAlphabetLower("\uD835\uDE8A")
                    .shiftNumbers("\uD835\uDFF6")
                    .build()
    );


    public static FontProcessor SCRIBBLE = new MappedFontProcessor("scribble",
            new FontMapBuilder()
                    .putAlphabetUpper("ᗩᗷᑢᗫᘿᖴᏩᕼᓰℐᏦᒪℳℕℴᕵℚᖇᏕτᑘᐺᘺ᙭ᖻℤ")
                    .putAlphabetLower("αϬᏨȡℯƒℊℎᎥℑҡℓᗰℵℴᕵᕴℜᏕᖶ∪Ꮙѡ᙭௶Ꮓ")
                    .build()
    );

    public static FontProcessor BIG_SCRIBBLE = new MappedFontProcessor("big_scribble",
            new FontMapBuilder()
                    .putAlphabetUpper("卂乃匚ᗪ乇千Ꮆ卄丨ﾌҜㄥ爪几ㄖ卩Ɋ尺丂ㄒㄩᐯ山乂ㄚ乙")
                    .putAlphabetLower("卂乃匚ᗪ乇千Ꮆ卄丨ﾌҜㄥ爪几ㄖ卩Ɋ尺丂ㄒㄩᐯ山乂ㄚ乙")
                    .build()
    );


    public static void registerFonts() {
        fontProcessors = new ArrayList<>();
        fontProcessors.add(NORMAL);
        fontProcessors.add(SUPERSCRIPT);
        fontProcessors.add(SUBSCRIPT);
        fontProcessors.add(CIRCLED);
        fontProcessors.add(NEGATIVE_CIRCLED);
        fontProcessors.add(SQUARED);
        fontProcessors.add(NEGATIVE_SQUARED);
        fontProcessors.add(REGIONAL_INDICATOR);
        fontProcessors.add(INVERSE);
        fontProcessors.add(FULLWIDTH);
        fontProcessors.add(SMALL);
        fontProcessors.add(BRACKETS);
        fontProcessors.add(MATHEMATICAL);
        fontProcessors.add(MATHEMATICAL_BOLD);
        fontProcessors.add(MATHEMATICAL_ITALIC);
        fontProcessors.add(MATHEMATICAL_BOLD_ITALIC);
        fontProcessors.add(MATHEMATICAL_BOLD_SCRIPT);
        fontProcessors.add(MATHEMATICAL_DOUBLE_STRUCK);
        fontProcessors.add(MATHEMATICAL_BOLD_FRAKTUR);
        fontProcessors.add(MATHEMATICAL_MONOSPACE);
        fontProcessors.add(SCRIBBLE);
        fontProcessors.add(BIG_SCRIBBLE);
    }

}
