package net.replaceitem.symbolchat.font;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Fonts {

    public static List<FontProcessor> fontProcessors;



    public static FontProcessor NORMAL = new FontProcessor("normal", s -> s);

    public static FontProcessor CAPITALIZED = new FontProcessor("capitalized", s -> s.toUpperCase(Locale.ROOT));

    public static FontProcessor SUPERSCRIPT = new MappedFontProcessor("superscript",
            new FontMapBuilder()
                    .putAlphabetUpper("ᴬᴮᶜᴰᴱᶠᴳᴴᴵᴶᴷᴸᴹᴺᴼᴾQᴿˢᵀᵁⱽᵂˣʸᶻ")
                    .putAlphabetLower("ᵃᵇᶜᵈᵉᶠᵍʰᶦʲᵏˡᵐⁿᵒᵖᑫʳˢᵗᵘᵛʷˣʸᶻ")
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
                    .putAlphabetUpper("ₐ₈CDₑբGhᵢⱼKLMNₒPQᵣSTᵤᵥWₓᵧZ")
                    .putAlphabetLower("ₐ₆꜀ₔₑբ₉hᵢⱼklmnₒpqᵣstᵤᵥwₓᵧ₂")
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


    // "А","Б","В","Г","Д","Е","Ё","Ж","З","И","Й","К","Л","М","Н","О","П","Р","С","Т","У","Ф","Х","Ц","Ч","Ш","Щ","Ъ","Ы","Ь","Э","Ю","Я","а","б","в","г","д","е","ё","ж","з","и","й","к","л","м","н","о","п","р","с","т","у","ф","х","ц","ч","ш","щ","ъ","ы","ь","э","ю","я"
    // "Ɐ","9","ᗺ","ɺ","▽","Ǝ","Ӭ",")|(","Ɛ","И","Ņ","Ʞ","┃ʃ","W","H","O","U","Ԁ","Ɔ","⟘","ɦ","Ф","╳","'|Ꞁ","Һ","|ꞀꞀ","'|ꞀꞀ","ᶐ","ıq","q","Є","О┫","ʁ","ɐ","g","ʚ","ɹ","ɓ","ǝ","ӭ","ж","ε","и","ņ","ʞ","v","ɯ","н","о","u","d","ɔ","w","ʎ","ȸ","х","╹n","һ","ʍ","╹ʍ","ꟼ.","ıꟼ","ꟼ","є","Ꙕ","ʁ"

    public static FontProcessor INVERSE = new MappedFontProcessor("inverse",
            new FontMapBuilder()
                    .putNumbers("0ƖՇƐ߈ϛ9ㄥ86")
                    .putAlphabetUpper("ⱯᗺƆᗡƎℲ⅁HIſꞰꞀWuOԀῸᴚS⟘∩ɅMX⅄Z")
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
            return new StringBuilder(super.getConvertedName()).reverse().toString();
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
                    .shiftAlphabetUpper(0x249C)
                    .shiftAlphabetLower(0x249C)
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
        fontProcessors.add(CAPITALIZED);
        fontProcessors.add(SUPERSCRIPT);
        fontProcessors.add(SUBSCRIPT);
        fontProcessors.add(CIRCLED);
        fontProcessors.add(INVERSE);
        fontProcessors.add(FULLWIDTH);
        fontProcessors.add(SMALL);
        fontProcessors.add(BRACKETS);
        fontProcessors.add(SCRIBBLE);
        fontProcessors.add(BIG_SCRIBBLE);
    }

}
