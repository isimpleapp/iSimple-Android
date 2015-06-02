package com.treelev.isimple.domain.db;

public class ItemPrice {

    private String itemID;
    private float price;
    private float priceMarkup;
    private int leftOvers;

    public ItemPrice(String itemID, float price, float priceMarkup, int leftOvers) {
        this.itemID = itemID;
        this.price = price;
        this.priceMarkup = priceMarkup;
        this.leftOvers = leftOvers;
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

    public int getLeftOvers() {
        return leftOvers;
    }
}
