package com.treelev.isimple.domain.db;

public class DeliveryZone {

    private String name;
    private Integer pickupCondition;
    private String pickupDesc;
    private Integer deliveryCondition;
    private String deliveryDesc;
    private Integer specialCondition;
    private String specialDesc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPickupCondition() {
        return pickupCondition;
    }

    public void setPickupCondition(Integer pickupCondition) {
        this.pickupCondition = pickupCondition;
    }

    public String getPickupDesc() {
        return pickupDesc;
    }

    public void setPickupDesc(String pickupDesc) {
        this.pickupDesc = pickupDesc;
    }

    public Integer getDeliveryCondition() {
        return deliveryCondition;
    }

    public void setDeliveryCondition(Integer deliveryCondition) {
        this.deliveryCondition = deliveryCondition;
    }

    public String getDeliveryDesc() {
        return deliveryDesc;
    }

    public void setDeliveryDesc(String deliveryDesc) {
        this.deliveryDesc = deliveryDesc;
    }

    public Integer getSpecialCondition() {
        return specialCondition;
    }

    public void setSpecialCondition(Integer specialCondition) {
        this.specialCondition = specialCondition;
    }

    public String getSpecialDesc() {
        return specialDesc;
    }

    public void setSpecialDesc(String specialDesc) {
        this.specialDesc = specialDesc;
    }

}
