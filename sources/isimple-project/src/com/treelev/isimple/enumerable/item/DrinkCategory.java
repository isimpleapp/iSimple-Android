package com.treelev.isimple.enumerable.item;

import android.os.Parcel;
import android.os.Parcelable;

public enum DrinkCategory implements Parcelable{
    WINE("Вино"), SPARKLING("Игристое"), PORTO("Порто-Херес"),
    STRONG("Крепкий Алкоголь"), SAKE("Саке"), WATER("Вода"),
    OTHER("Другое");

    public static final Parcelable.Creator<DrinkCategory> CREATOR = new Parcelable.Creator<DrinkCategory>() {

        public DrinkCategory createFromParcel(Parcel in) {
            return DrinkCategory.values()[in.readInt()];
        }

        public DrinkCategory[] newArray(int size) {
            return new DrinkCategory[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ordinal());
    }

    private String description;

    private DrinkCategory() {
        this(null);
    }

    private DrinkCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static DrinkCategory getDrinkCategory(String drinkCategoryId) {
        for (DrinkCategory drinkCategory : values()) {
            if (drinkCategory.description != null && drinkCategory.description.equalsIgnoreCase(drinkCategoryId)) {
                return drinkCategory;
            }
        }
        return OTHER;
    }
}
