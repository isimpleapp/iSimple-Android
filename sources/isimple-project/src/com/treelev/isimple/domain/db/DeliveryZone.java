package com.treelev.isimple.domain.db;

import com.treelev.isimple.domain.PickupLocation;

public class DeliveryZone {

    private String name;
    private Integer pickupCost;
    private Integer pickupCondition;
    private PickupLocation pickupLocation;
    private String pickupDesc;
    private Integer deliveryCost;
    private Integer deliveryCondition;
    private String deliveryDesc;
    private Integer specialCost;
    private Integer specialCondition;
    private String specialDesc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPickupCost() {
        return pickupCost;
    }

    public void setPickupCost(Integer pickupCost) {
        this.pickupCost = pickupCost;
    }

    public Integer getPickupCondition() {
        return pickupCondition;
    }

    public void setPickupCondition(Integer pickupCondition) {
        this.pickupCondition = pickupCondition;
    }

    public PickupLocation getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(PickupLocation pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getPickupDesc() {
        return pickupDesc;
    }

    public void setPickupDesc(String pickupDesc) {
        this.pickupDesc = pickupDesc;
    }

    public Integer getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(Integer deliveryCost) {
        this.deliveryCost = deliveryCost;
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

    public Integer getSpecialCost() {
        return specialCost;
    }

    public void setSpecialCost(Integer specialCost) {
        this.specialCost = specialCost;
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
