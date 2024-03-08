package net.replaceitem.symbolchat.font;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

public class Fonts {
    
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    public static int i = 10;
    
    public static class Dummy {
        private final String name;
        private final FontMapBuilder build;

        public Dummy(String name, FontMapBuilder build) {
            this.name = name;
            this.build = build;
        }
        
        public void save() {
            File file = new File("./fonts/" + name + ".json");
            try(JsonWriter writer = new JsonWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                writer.setIndent("    ");
                JsonObject root = new JsonObject();
                root.add("order", new JsonPrimitive(i));
                root.add("mappings", build.map);
                i += 10;
                GSON.toJson(root, writer);
                System.out.println(file.getAbsolutePath());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static final Dummy SUPERSCRIPT = new Dummy("superscript",
            new FontMapBuilder()
                    .putAlphabetUpper("ᴬᴮᶜᴰᴱꟳᴳᴴᴵᴶᴷᴸᴹᴺᴼᴾꟴᴿˢᵀᵁⱽᵂˣʸᶻ")
                    .putAlphabetLower("ᵃᵇᶜᵈᵉᶠᵍʰ𞁌ʲᵏˡᵐ𞀽ᵒᵖ𐞥ʳˢᵗᵘᵛʷˣʸᶻ")
                    .putNumbers("⁰¹²³⁴⁵⁶⁷⁸⁹")
                    .put('+','⁺')
                    .put('-','⁻')
                    .put('=','⁼')
                    .put('(','⁽')
                    .put(')','⁾')
                    
            );

    public static final Dummy SUBSCRIPT = new Dummy("subscript",
            new FontMapBuilder()
                    .putAlphabetUpper("ₐ𞁓𞁞DₑբGₕᵢⱼ𞁚ₗₘ𞁝𞁜ₚQᵣₛₜ𞁢ᵥ𞁤𞁡𞁟Z")
                    .putAlphabetLower("ₐ𞁥𞁞ₔₑբ₉ₕᵢⱼₖₗₘₙₒₚqᵣₛₜᵤᵥ𞁤ₓᵧ₂")
                    .putNumbers("₀₁₂₃₄₅₆₇₈₉")
                    .put('+','₊')
                    .put('-','₋')
                    .put('=','₌')
                    .put('(','₍')
                    .put(')','₎')
                    
            );


    public static final Dummy CIRCLED = new Dummy("circled",
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
                    
            );

    public static final Dummy NEGATIVE_CIRCLED = new Dummy("negative_circled",
            new FontMapBuilder()
                    .shiftAlphabetUpper(0x1F150)
                    .shiftAlphabetLower(0x1F150)
                    .shiftSequence('1', 0x278A, 9)
                    .put('0', "⓿")
                    
            );

    public static final Dummy SQUARED = new Dummy("squared",
            new FontMapBuilder()
                    .shiftAlphabetUpper(0x1F130)
                    .shiftAlphabetLower(0x1F130)
                    
            );

    public static final Dummy NEGATIVE_SQUARED = new Dummy("negative_squared",
            new FontMapBuilder()
                    .shiftAlphabetUpper(0x1F170)
                    .shiftAlphabetLower(0x1F170)
                    
            );

    public static final Dummy REGIONAL_INDICATOR = new Dummy("regional_indicator",
            new FontMapBuilder()
                    .shiftAlphabetUpper(0x1F1E6)
                    .shiftAlphabetLower(0x1F1E6)
                    
            );
    
    public static final Dummy INVERSE = new Dummy("inverse",
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
                    
            );

    public static final Dummy FULLWIDTH = new Dummy("fullwidth",
            new FontMapBuilder()
                    .shiftSequence('!', 0xFF01, '~'-'!'+1)
                    
            );

    public static final Dummy SMALL = new Dummy("small",
            new FontMapBuilder()
                    .putAlphabetUpper("ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ")
                    .putAlphabetLower("ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ")
                    
            );


    public static final Dummy BRACKETS = new Dummy("brackets",
            new FontMapBuilder()
                    .shiftSequence('1', 0x2474, 9)
                    .shiftAlphabetUpper(0x1F110)
                    .shiftAlphabetLower(0x249C)
                    
            );

    public static final Dummy MATHEMATICAL = new Dummy("mathematical",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDDA0")
                    .shiftAlphabetLower("\uD835\uDDBA")
                    .shiftNumbers("\uD835\uDFE2")
                    
            );

    public static final Dummy MATHEMATICAL_BOLD = new Dummy("mathematical_bold",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDDD4")
                    .shiftAlphabetLower("\uD835\uDDEE")
                    .shiftNumbers("\uD835\uDFEC")
                    
            );

    public static final Dummy MATHEMATICAL_ITALIC = new Dummy("mathematical_italic",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDE08")
                    .shiftAlphabetLower("\uD835\uDE22")
                    .shiftNumbers("\uD835\uDFE2")
                    
            );

    public static final Dummy MATHEMATICAL_BOLD_ITALIC = new Dummy("mathematical_bold_italic",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDE3C")
                    .shiftAlphabetLower("\uD835\uDE56")
                    .shiftNumbers("\uD835\uDFE2")
                    
            );

    public static final Dummy MATHEMATICAL_SCRIPT = new Dummy("mathematical_script",
            new FontMapBuilder()
                    .putAlphabetUpper("𝒜\uD835\uDC35𝒞𝒟\uD835\uDC38\uD835\uDC39𝒢\uD835\uDC3B\uD835\uDC3C𝒥𝒦\uD835\uDC3F\uD835\uDC40𝒩𝒪𝒫𝒬\uD835\uDC45𝒮𝒯𝒰𝒱𝒲𝒳𝒴𝒵")
                    .putAlphabetLower("𝒶𝒷𝒸𝒹𝑒𝒻𝑔𝒽𝒾𝒿𝓀𝓁𝓂𝓃𝑜𝓅𝓆𝓇𝓈𝓉𝓊𝓋𝓌𝓍𝓎𝓏") // some are missing, using those from italic instead
                    .shiftNumbers("\uD835\uDFE2")
                    
            );

    public static final Dummy MATHEMATICAL_BOLD_SCRIPT = new Dummy("mathematical_bold_script",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDCD0")
                    .shiftAlphabetLower("\uD835\uDCEA")
                    .shiftNumbers("\uD835\uDFE2")
                    
            );

    public static final Dummy MATHEMATICAL_DOUBLE_STRUCK = new Dummy("mathematical_double_struck",
            new FontMapBuilder()
                    .putAlphabetUpper("𝔸𝔹ℂ𝔻𝔼𝔽𝔾ℍ𝕀𝕁𝕂𝕃𝕄ℕ𝕆ℙℚℝ𝕊𝕋𝕌𝕍𝕎𝕏𝕐ℤ") // identical to lower, since upper has some missing unifont chars
                    .shiftAlphabetLower("\uD835\uDD52")
                    .shiftNumbers("\uD835\uDFD8")
                    
            );

    public static final Dummy MATHEMATICAL_BOLD_FRAKTUR = new Dummy("mathematical_bold_fraktur",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDD6C")
                    .shiftAlphabetLower("\uD835\uDD86")
                    .shiftNumbers("\uD835\uDFE2")
                    
            );

    public static final Dummy MATHEMATICAL_MONOSPACE = new Dummy("mathematical_monospace",
            new FontMapBuilder()
                    .shiftAlphabetUpper("\uD835\uDE70")
                    .shiftAlphabetLower("\uD835\uDE8A")
                    .shiftNumbers("\uD835\uDFF6")
                    
            );


    public static final Dummy SCRIBBLE = new Dummy("scribble",
            new FontMapBuilder()
                    .putAlphabetUpper("ᗩᗷᑢᗫᘿᖴᏩᕼᓰℐᏦᒪℳℕℴᕵℚᖇᏕτᑘᐺᘺ᙭ᖻℤ")
                    .putAlphabetLower("αϬᏨȡℯƒℊℎᎥℑҡℓᗰℵℴᕵᕴℜᏕᖶ∪Ꮙѡ᙭௶Ꮓ")
                    
            );

    public static final Dummy BIG_SCRIBBLE = new Dummy("big_scribble",
            new FontMapBuilder()
                    .putAlphabetUpper("卂乃匚ᗪ乇千Ꮆ卄丨ﾌҜㄥ爪几ㄖ卩Ɋ尺丂ㄒㄩᐯ山乂ㄚ乙")
                    .putAlphabetLower("卂乃匚ᗪ乇千Ꮆ卄丨ﾌҜㄥ爪几ㄖ卩Ɋ尺丂ㄒㄩᐯ山乂ㄚ乙")
                    
            );

    
    public static void save(Dummy dummy) {
        dummy.save();
    }

    public static void main(String[] args) {
        registerFonts();
    }

    public static void registerFonts() {
        save(SUPERSCRIPT);
        save(SUBSCRIPT);
        save(CIRCLED);
        save(NEGATIVE_CIRCLED);
        save(SQUARED);
        save(NEGATIVE_SQUARED);
        save(REGIONAL_INDICATOR);
        save(INVERSE);
        save(FULLWIDTH);
        save(SMALL);
        save(BRACKETS);
        save(MATHEMATICAL);
        save(MATHEMATICAL_BOLD);
        save(MATHEMATICAL_ITALIC);
        save(MATHEMATICAL_BOLD_ITALIC);
        save(MATHEMATICAL_SCRIPT);
        save(MATHEMATICAL_BOLD_SCRIPT);
        save(MATHEMATICAL_DOUBLE_STRUCK);
        save(MATHEMATICAL_BOLD_FRAKTUR);
        save(MATHEMATICAL_MONOSPACE);
        save(SCRIBBLE);
        save(BIG_SCRIBBLE);
    }

}
