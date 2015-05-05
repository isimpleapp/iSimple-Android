
package com.treelev.isimple.enumerable;

public enum UpdateFile {

    CATALOG_UPDATES("CatalogUpdates"), ITEM_PRICES("ItemPrices"), ITEM_AVAILABILITY(
            "ItemAvailability"), LOCATIONS_AND_CHAINS_UPDATES(
            "LocationsAndChainsUpdates"), DELIVERY("Delivery"), OFFERS("OffersList"), FEATURED(
            "Featured"), DISCOUNT("Discount"), DEPRECATED("Deprecated");

    private String updateFileTag;

    private UpdateFile(String updateFileTag) {
        this.updateFileTag = updateFileTag;
    }

    public String getUpdateFileTag() {
        return updateFileTag;
    }

    public static UpdateFile getUpdateFileByTag(String tag) {
        if (CATALOG_UPDATES.getUpdateFileTag().equalsIgnoreCase(tag)) {
            return CATALOG_UPDATES;
        } else if (ITEM_PRICES.getUpdateFileTag().equalsIgnoreCase(tag)) {
            return ITEM_PRICES;
        } else if (ITEM_AVAILABILITY.getUpdateFileTag().equalsIgnoreCase(tag)) {
            return ITEM_AVAILABILITY;
        } else if (LOCATIONS_AND_CHAINS_UPDATES.getUpdateFileTag().equalsIgnoreCase(tag)) {
            return LOCATIONS_AND_CHAINS_UPDATES;
        } else if (DELIVERY.getUpdateFileTag().equalsIgnoreCase(tag)) {
            return DELIVERY;
        } else if (OFFERS.getUpdateFileTag().equalsIgnoreCase(tag)) {
            return OFFERS;
        } else if (FEATURED.getUpdateFileTag().equalsIgnoreCase(tag)) {
            return FEATURED;
        } else if (DISCOUNT.getUpdateFileTag().equalsIgnoreCase(tag)) {
            return DISCOUNT;
        } else if (DEPRECATED.getUpdateFileTag().equalsIgnoreCase(tag)) {
            return DEPRECATED;
        } else {
            return null;
        }
    }

}
