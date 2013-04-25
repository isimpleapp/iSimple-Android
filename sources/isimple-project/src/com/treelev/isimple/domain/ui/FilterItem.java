package com.treelev.isimple.domain.ui;

public class FilterItem {

    public final static int ITEM_TYPE_TEXT = 1;
    public final static int ITEM_TYPE_PROGRESS = 2;

    private int itemType;
    private String name;

    public FilterItem(int itemType) {
        this(itemType, null);
    }

    public FilterItem(int itemType, String name) {
        this.itemType = itemType;
        this.name = name;
    }

    public int getItemType() {
        return itemType;
    }

    public String getName() {
        return name;
    }
}
