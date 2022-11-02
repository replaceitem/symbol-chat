package net.replaceitem.symbolchat;


import net.minecraft.client.resource.language.I18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class FontProcessor {

    public static List<FontProcessor> fontProcessors;

    protected final String nameKey;

    public FontProcessor(String nameKey) {
        this.nameKey = "symbolchat.font." + nameKey;
    }

    public abstract String convertChar(String string);

    public String convertString(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < string.length(); i++) {
            stringBuilder.append(convertChar(String.valueOf(string.charAt(i))));
        }
        return stringBuilder.toString();
    }

    public String getConvertedName() {
        return this.convertString(I18n.translate(this.nameKey));
    }

    @Override
    public String toString() {
        return getConvertedName();
    }

    public static FontProcessor NORMAL = new FontProcessor("normal") {
        @Override
        public String convertChar(String string) {
            return string;
        }
    };

    public static FontProcessor CAPITALIZED = new FontProcessor("capitalized") {
        @Override
        public String convertChar(String string) {
            return "";
        }

        @Override
        public String convertString(String string) {
            return string.toUpperCase(Locale.ROOT);
        }
    };

    public static FontProcessor SUPERSCRIPT = new FontProcessor("superscript") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);
            if(c >= '0' && c <= '9') {
                return "⁰¹²³⁴⁵⁶⁷⁸⁹".substring(c-'0',c-'0'+1);
            }
            if(c >= 'A' && c <= 'Z') {
                return "ᴬᴮᶜᴰᴱᶠᴳᴴᴵᴶᴷᴸᴹᴺᴼᴾQᴿˢᵀᵁⱽᵂˣʸᶻ".substring(c-'A',c-'A'+1);
            }
            if(c >= 'a' && c <= 'z') {
                return "ᵃᵇᶜᵈᵉᶠᵍʰᶦʲᵏˡᵐⁿᵒᵖᑫʳˢᵗᵘᵛʷˣʸᶻ".substring(c-'a',c-'a'+1);
            }

            if(c=='+') return "⁺";
            if(c=='-') return "⁻";
            if(c=='=') return "⁼";
            if(c=='(') return "⁽";
            if(c==')') return "⁾";

            return string;
        }
    };

    public static FontProcessor SUBSCRIPT = new FontProcessor("subscript") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);
            if(c >= '0' && c <= '9') {
                return "₀₁₂₃₄₅₆₇₈₉".substring(c-'0',c-'0'+1);
            }
            if(c >= 'A' && c <= 'Z') {
                return "ₐ₈CDₑբGhᵢⱼKLMNₒPQᵣSTᵤᵥᵥᵥₓᵧZ".substring(c-'A',c-'A'+1);
            }
            if(c >= 'a' && c <= 'z') {
                return "ₐ₆꜀ₔₑբ₉hᵢⱼklmnₒpqᵣstᵤᵥᵥᵥₓᵧ₂".substring(c-'a',c-'a'+1);
            }

            if(c=='+') return "₊";
            if(c=='-') return "₋";
            if(c=='=') return "₌";
            if(c=='(') return "₍";
            if(c==')') return "₎";

            return string;
        }
    };


    public static FontProcessor CIRCLED = new FontProcessor("circled") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);
            if(c >= '0' && c <= '9') {
                if(c == '0') return FontProcessor.symbolFromUnicodeNumber(0x24EA);
                return FontProcessor.symbolFromUnicodeNumber(c-'1'+0x2460);
            }
            if(c >= 'A' && c <= 'Z') {
                return FontProcessor.symbolFromUnicodeNumber(c-'A'+0x24B6);
            }

            if(c >= 'a' && c <= 'z') {
                return FontProcessor.symbolFromUnicodeNumber(c-'a'+0x24D0);
            }

            if(c=='+') return "⊕";
            if(c=='-') return "⊝";
            if(c=='=') return "⊜";
            if(c=='*') return "⊛";
            if(c=='/') return "⊘";

            return string;
        }
    };


    public static FontProcessor INVERSE = new FontProcessor("inverse") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);
            if(c >= '0' && c <= '9') {
                return "0ƖՇƐh૬9L86".substring(c-'0',c-'0'+1);
            }
            if(c >= 'A' && c <= 'Z') {
                return "ⱯᗺƆᗡƎℲ⅁HIſʞ˥WuOԀῸᴚSʇ∩ΛMX⅄Z".substring(c-'A',c-'A'+1);
            }
            if(c >= 'a' && c <= 'z') {
                return "ɐqɔpǝɟɓɥᴉſʞๅɯuodbɹsʇnʌʍxʎz".substring(c-'a',c-'a'+1);
            }

            if(c=='!') return "i";
            if(c==',') return "'";
            if(c=='.') return "˙";
            if(c=='?') return "¿";

            return string;
        }

        @Override
        public String getConvertedName() {
            return new StringBuilder(super.getConvertedName()).reverse().toString();
        }
    };

    public static FontProcessor FULLWIDTH = new FontProcessor("fullwidth") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);
            if(c >= '!' && c <= '~') {
                return FontProcessor.symbolFromUnicodeNumber(c-'!'+0xFF01);
            }

            return string;
        }
    };

    public static FontProcessor SMALL = new FontProcessor("small") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);

            if(c >= 'A' && c <= 'Z') {
                return "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ".substring(c-'A',c-'A'+1);
            }
            if(c >= 'a' && c <= 'z') {
                return "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ".substring(c-'a',c-'a'+1);
            }

            return string;
        }
    };


    public static FontProcessor BRACKETS = new FontProcessor("brackets") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);
            if(c >= '1' && c <= '9') {
                return FontProcessor.symbolFromUnicodeNumber(c-'1'+0x2474);
            }
            if(c >= 'A' && c <= 'Z') {
                return FontProcessor.symbolFromUnicodeNumber(c-'A'+0x249C);
            }

            if(c >= 'a' && c <= 'z') {
                return FontProcessor.symbolFromUnicodeNumber(c-'a'+0x249C);
            }

            return string;
        }
    };


    public static FontProcessor SCRIBBLE = new FontProcessor("scribble") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);



            if(c >= 'A' && c <= 'Z') {
                return "ᗩᗷᑢᗫᘿᖴᏩᕼᓰℐᏦᒪℳℕℴᕵℚᖇᏕτᑘᐺᘺ᙭ᖻℤ".substring(c-'A',c-'A'+1);
            }
            if(c >= 'a' && c <= 'z') {
                return "αϬᏨȡℯƒℊℎᎥℑҡℓᗰℵℴᕵᕴℜᏕᖶ∪Ꮙѡ᙭௶Ꮓ".substring(c-'a',c-'a'+1);
            }

            return string;
        }
    };

    public static FontProcessor BIG_SCRIBBLE = new FontProcessor("big_scribble") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);



            if(c >= 'A' && c <= 'Z') {
                return "卂乃匚ᗪ乇千Ꮆ卄丨ﾌҜㄥ爪几ㄖ卩Ɋ尺丂ㄒㄩᐯ山乂ㄚ乙".substring(c-'A',c-'A'+1);
            }
            if(c >= 'a' && c <= 'z') {
                return "卂乃匚ᗪ乇千Ꮆ卄丨ﾌҜㄥ爪几ㄖ卩Ɋ尺丂ㄒㄩᐯ山乂ㄚ乙".substring(c-'a',c-'a'+1);
            }

            return string;
        }
    };


    public static void registerFontProcessors() {
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


    private static String symbolFromUnicodeNumber(int num) {
        return new String(Character.toChars(num));
    }
}
