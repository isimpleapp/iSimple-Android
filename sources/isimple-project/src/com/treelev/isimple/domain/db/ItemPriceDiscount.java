package com.treelev.isimple.domain.db;

public class ItemPriceDiscount {

    private String itemID;
    private float priceDiscount;

    public ItemPriceDiscount(String itemID, float priceDiscount) {
        this.itemID = itemID;
        this.priceDiscount = priceDiscount;
    }

    public String getItemID() {
        return itemID;
    }

    public float getPriceDiscount() {
        return priceDiscount;
    }

}
