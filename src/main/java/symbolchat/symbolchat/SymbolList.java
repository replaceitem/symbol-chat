package symbolchat.symbolchat;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SymbolList {
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("icon")
    public String icon;

    @SerializedName("position")
    public int position;

    @SerializedName("symbols")
    public List<String> items;

    public SymbolList(List<String> items) {
        this.items = items;
        this.name = "Custom";
        this.icon = "✎";
        this.position = Integer.MAX_VALUE;
        this.id = "custom";
    }

    public void splitStrings() {

        ArrayList<String> newSymbols = new ArrayList<>();

        for(String s : items) {
            for(int i = 0; i < s.codePointCount(0, s.length()); i++) {
                newSymbols.add(new StringBuilder().appendCodePoint(s.codePointAt(s.offsetByCodePoints(0, i))).toString());
            }
        }
        items = newSymbols;
    }
}
