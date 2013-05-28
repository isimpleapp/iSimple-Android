package com.treelev.isimple.domain.db;

public class ItemAvailability {

    private String itemID;
    private String locationID;
    private String customerID;
    private String shiptoCodeID;
    private Float price;

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getShiptoCodeID() {
        return shiptoCodeID;
    }

    public void setShiptoCodeID(String shiptoCodeID) {
        this.shiptoCodeID = shiptoCodeID;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
