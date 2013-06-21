package com.treelev.isimple.domain.db;

public class Order {
    private String mItemID;
    private int mQuantity;

    public Order(String itemID, int quantity){
        mItemID = itemID;
        mQuantity = quantity;
    }

    public String getItemID(){
        return mItemID;
    }

    public int getQuantity(){
        return mQuantity;
    }
}
