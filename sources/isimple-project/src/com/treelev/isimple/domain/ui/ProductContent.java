package com.treelev.isimple.domain.ui;

public class ProductContent {

    private String itemName;
    private String itemContent;

    public ProductContent(String itemName, String itemContent) {
        this.itemName = itemName;
        this.itemContent = itemContent;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemContent() {
        return itemContent;
    }
}
