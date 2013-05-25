package com.treelev.isimple.enumerable.item;

import android.os.Parcel;
import android.os.Parcelable;

public enum DrinkCategory {
    WINE("Вино"), SPARKLING("Игристое"), PORTO("Порто-Херес"),
    SPIRITS("Крепкий Алкоголь"), SAKE("Саке"), WATER("Вода"),
    OTHER("Другое");

    private String name;

    private DrinkCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return getName();
    }

    public static DrinkCategory getDrinkCategory(String name) {
        for (DrinkCategory drinkCategory : values()) {
            if (drinkCategory.name != null && drinkCategory.name.equalsIgnoreCase(name)) {
                return drinkCategory;
            }
        }
        return OTHER;
    }

    public static DrinkCategory getDrinkCategory(int ordinal) {
        return ordinal >= 0 && ordinal < DrinkCategory.values().length
                ? DrinkCategory.values()[ordinal] : OTHER;
    }
}
