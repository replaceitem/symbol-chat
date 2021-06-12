package symbolchat.symbolchat;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SymbolList {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("icon")
    public String icon;

    @SerializedName("position")
    public int position;

    @SerializedName("symbols")
    public ArrayList<String> symbols;


    public void splitStrings() {

        ArrayList<String> newSymbols = new ArrayList<>();

        for(String s : symbols) {
            for(int i = 0; i < s.codePointCount(0, s.length()); i++) {
                newSymbols.add(new StringBuilder().appendCodePoint(s.codePointAt(s.offsetByCodePoints(0, i))).toString());
            }
        }
        symbols = newSymbols;
    }
}
