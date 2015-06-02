package com.treelev.isimple.enumerable.item;

import com.treelev.isimple.R;
import com.treelev.isimple.filter.*;

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

    public static DrinkCategory getDrinkCategory(Integer ordinal) {
        return ordinal != null && ordinal >= 0 && ordinal < DrinkCategory.values().length
                ? DrinkCategory.values()[ordinal] : OTHER;
    }

    public static Integer getItemCategoryByButtonId(int buttonId) {
        Integer category = null;
        switch (buttonId) {
            case R.id.category_wine_butt:
                category = WINE.ordinal();
                break;
            case R.id.category_spirits_butt:
                category = SPIRITS.ordinal();
                break;
            case R.id.category_sparkling_butt:
                category = SPARKLING.ordinal();
                break;
            case R.id.category_porto_heres_butt:
                category = PORTO.ordinal();
                break;
            case R.id.category_sake_butt:
                category = SAKE.ordinal();
                break;
            case R.id.category_water_butt:
                category = WATER.ordinal();
                break;
        }
        return category;
    }

    public static Integer getItemCategoryByFilter(Filter filterObject) {
        Integer category = null;
        if (filterObject instanceof SakeFilter) {
            category = SAKE.ordinal();
        } else if (filterObject instanceof WaterFilter) {
            category = WATER.ordinal();
        } else if (filterObject instanceof PortoHeresFilter) {
            category = PORTO.ordinal();
        } else if (filterObject instanceof SparklingFilter) {
            category = SPARKLING.ordinal();
        } else if (filterObject instanceof SpiritsFilter) {
            category = SPIRITS.ordinal();
        } else if (filterObject instanceof WineFilter) {
            category = WINE.ordinal();
        }
        return category;
    }
}
