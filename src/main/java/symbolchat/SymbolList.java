package symbolchat;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SymbolList {
    public String id;

    @SerializedName("name")
    public String nameKey;

    @SerializedName("icon")
    public String icon;

    @SerializedName("symbols")
    public List<String> items;

    public static SymbolList createCustom() {
        return new SymbolList(
                new ArrayList<>(),
                "symbolchat.tab.custom",
                "✎",
                "custom"
        );
    }
    
    public SymbolList(List<String> items, String nameKey, String icon, String id) {
        this.items = items;
        this.nameKey = nameKey;
        this.icon = icon;
        this.id = id;
    }

    public void postProcess() {
        ArrayList<String> newSymbols = new ArrayList<>();

        for(String s : items) {
            for(int i = 0; i < s.codePointCount(0, s.length()); i++) {
                newSymbols.add(new StringBuilder().appendCodePoint(s.codePointAt(s.offsetByCodePoints(0, i))).toString());
            }
        }
        items = newSymbols;
    }
}
