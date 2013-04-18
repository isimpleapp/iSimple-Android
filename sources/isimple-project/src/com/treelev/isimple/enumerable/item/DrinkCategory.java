package com.treelev.isimple.enumerable.item;

public enum DrinkCategory {
    WINE("Вино"), SPIRITS("Крепкий Алкоголь"), SAKE("Саке"), UNKNOWN;

    private String description;

    private DrinkCategory() {
        this(null);
    }

    private DrinkCategory(String description) {
        this.description = description;
    }

    public static DrinkCategory getDrinkCategory(String drinkCategoryId) {
        for (DrinkCategory drinkCategory : values()) {
            if (drinkCategory.description != null && drinkCategory.description.equals(drinkCategoryId)) {
                return drinkCategory;
            }
        }
        return UNKNOWN;
    }
}
