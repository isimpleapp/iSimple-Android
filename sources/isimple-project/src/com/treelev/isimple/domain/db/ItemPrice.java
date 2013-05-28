package com.treelev.isimple.domain.db;

public class ItemPrice {

    private String itemID;
    private float price;
    private float priceMarkup;

    public ItemPrice(String itemID, float price, float priceMarkup) {
        this.itemID = itemID;
        this.price = price;
        this.priceMarkup = priceMarkup;
    }

    public String getItemID() {
        return itemID;
    }

    public float getPrice() {
        return price;
    }

    public float getPriceMarkup() {
        return priceMarkup;
    }
}
