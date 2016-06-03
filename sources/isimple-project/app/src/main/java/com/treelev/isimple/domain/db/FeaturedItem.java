package com.treelev.isimple.domain.db;

import com.treelev.isimple.enumerable.item.DrinkCategory;

public class FeaturedItem {

    public final static String MAIN_CATEGORY_TAG = "Main";
    public final static String WINE_CATEGORY_TAG = "Wine";
    public final static String ALCO_CATEGORY_TAG = "Alco";
    public final static String CHAMPA_CATEGORY_TAG = "Champa";
    public final static String PORTO_CATEGORY_TAG = "Porto";
    public final static String SAKE_CATEGORY_TAG = "Sake";
    public final static String WATER_CATEGORY_TAG = "Water";

    private String itemID;
    private int categoryID;

    public FeaturedItem(String itemID, String categoryName) {
        this.itemID = itemID;
        this.categoryID =
        	categoryName.equalsIgnoreCase(MAIN_CATEGORY_TAG) ? -1 :
            categoryName.equalsIgnoreCase(WINE_CATEGORY_TAG) ? DrinkCategory.WINE.ordinal() :
            categoryName.equalsIgnoreCase(ALCO_CATEGORY_TAG) ? DrinkCategory.SPIRITS.ordinal() :
            categoryName.equalsIgnoreCase(CHAMPA_CATEGORY_TAG) ? DrinkCategory.SPARKLING.ordinal() :
            categoryName.equalsIgnoreCase(PORTO_CATEGORY_TAG) ? DrinkCategory.PORTO.ordinal() :
            categoryName.equalsIgnoreCase(SAKE_CATEGORY_TAG) ? DrinkCategory.SAKE.ordinal() :
            categoryName.equalsIgnoreCase(WATER_CATEGORY_TAG) ? DrinkCategory.WATER.ordinal() : -1;
    }

    public FeaturedItem(String itemID, int categoryID) {
        this.itemID = itemID;
        this.categoryID = categoryID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
}
