package com.treelev.isimple.domain.db;

public class Order {
    private String mItemID;
    private int mQuantity;
    private float mPrice;

    public Order(String itemID, int quantity, float price){
        mItemID = itemID;
        mQuantity = quantity;
        mPrice = price;
    }

    public String getItemID(){
        return mItemID;
    }

    public int getQuantity(){
        return mQuantity;
    }

	public float getPrice() {
		return mPrice;
	}
}
