package com.treelev.isimple.enumerable;

public enum DrinkColor {
    RED_WINE("Красное вино", "#A90000"), WHITE_WINE("Белое вино", "#f9e69e"), PINK_WINE("Розовое вино", "#f587a7"),
    WHISKEY("Виски", "#e75202"), BRANDY("Коньяк", "#db5b1b"), ARMAGNAC("Арманьяк", "#db5b1b"), GRAPPA("Граппа", "#dbbe61"),
    RUM("Ром", "#cc4228"), LIQUOR("Ликер", "#484354"), SAKE("Сакэ", "#e2e2e2"), WATER("Вода", "#a6e4ff"),
    SIRUP("Премикс, пюре, сироп", "#e1e234"), ENERGETIC("Энергетик", "#000000"), CACHACA("Кашаса", "#d6d224");

    private String description;
    private String color;

    private DrinkColor(String description, String color) {
        this.description = description;
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }
}
