package com.example.domain;

public enum StampRank {
    MILD(1, "マイルド", "#4CAF50"),
    MEDIUM(2, "ミディアム", "#FFEB3B"),
    HOT(3, "ホット", "#FF9800"),
    SPICY(4, "スパイシー", "#F44336"),
    MAGMA(5, "マグマ", "#212121");

    private final int level;
    private final String label;
    private final String colorHex;

    StampRank(int level, String label, String colorHex) {
        this.level = level;
        this.label = label;
        this.colorHex = colorHex;
    }

    public int getLevel() {
        return level;
    }

    public String getLabel() {
        return label;
    }

    public String getColorHex() {
        return colorHex;
    }

    public static StampRank fromStampCount(Integer count) {
        if (count <= 25)
            return MILD;
        else if (count <= 50)
            return MEDIUM;
        else if (count <= 75)
            return HOT;
        else if (count <= 100)
            return SPICY;
        else
            return MAGMA;
    }
}
