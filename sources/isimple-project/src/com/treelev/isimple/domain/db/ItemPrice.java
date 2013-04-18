package com.treelev.isimple.domain.db;

public class ItemPrice {

    private String itemId;
    private Integer price;
    private Integer priceMarkup;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPriceMarkup() {
        return priceMarkup;
    }

    public void setPriceMarkup(Integer priceMarkup) {
        this.priceMarkup = priceMarkup;
    }
}
