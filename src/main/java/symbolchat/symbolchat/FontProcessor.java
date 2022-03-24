package symbolchat.symbolchat;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class FontProcessor {

    public static List<FontProcessor> fontProcessors;

    public String name;
    private final String convertedName;

    public FontProcessor(String name) {
        this.name = name;
        this.convertedName = this.convertString(this.name);
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
        return convertedName;
    }

    @Override
    public String toString() {
        return getConvertedName();
    }

    public static FontProcessor NORMAL = new FontProcessor("Normal") {
        @Override
        public String convertChar(String string) {
            return string;
        }
    };

    public static FontProcessor CAPITALIZED = new FontProcessor("Capitalized") {
        @Override
        public String convertChar(String string) {
            return "";
        }

        @Override
        public String convertString(String string) {
            return string.toUpperCase(Locale.ROOT);
        }
    };

    public static FontProcessor SUPERSCRIPT = new FontProcessor("Superscript") {
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

    public static FontProcessor SUBSCRIPT = new FontProcessor("Subscript") {
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


    public static FontProcessor CIRCLED = new FontProcessor("Circled") {
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


    public static FontProcessor INVERSE = new FontProcessor("esrevnI") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);
            if(c >= '0' && c <= '9') {
                return "0ƖՇƐh૬9L86".substring(c-'0',c-'0'+1);
            }
            if(c >= 'A' && c <= 'Z') {
                return "ⱯqɔpƎɟɓɥᴉſʞๅWuOdῸɹSʇnʌMX⅄Z".substring(c-'A',c-'A'+1);
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
    };

    public static FontProcessor FULLWIDTH = new FontProcessor("FullWidth") {
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

    public static FontProcessor SMALL = new FontProcessor("Small") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);

            if(c >= 'A' && c <= 'Z') {
                return "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ".substring(c-'A',c-'A'+1);
            }
            if(c >= 'a' && c <= 'z') {
                return "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ".substring(c-'a',c-'a'+1);
            }

            return string;
        }
    };


    public static FontProcessor BRACKETS = new FontProcessor("Brackets") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);
            if(c >= '0' && c <= '9') {
                if( c == '0') return FontProcessor.symbolFromUnicodeNumber(0x24C4);
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


    public static FontProcessor SCRIBBLE = new FontProcessor("Scribble") {
        @Override
        public String convertChar(String string) {
            if(string.length() == 0) return string;
            if(string.length() > 1) return string;
            char c = string.charAt(0);



            if(c >= 'A' && c <= 'Z') {
                return "ᗩᗷᑢᕲᘿᖴᘜᕼᓰᒚᐸᒪᘻᘉᓍᕵᕴᖇᏕᖶᑘᐺᘺ᙭ᖻℤ".substring(c-'A',c-'A'+1);
            }
            if(c >= 'a' && c <= 'z') {
                return "ᗩᗷᑢᕲᘿᖴᘜᕼᓰᒚᐸᒪᘻᘉᓍᕵᕴᖇᏕᖶᑘᐺᘺ᙭ᖻℤ".substring(c-'a',c-'a'+1);
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
    }


    private static String symbolFromUnicodeNumber(int num) {
        return new String(Character.toChars(num));
    }
}
