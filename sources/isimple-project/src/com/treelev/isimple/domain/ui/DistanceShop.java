package com.treelev.isimple.domain.ui;

import com.treelev.isimple.domain.db.Shop;

public class DistanceShop implements Comparable<DistanceShop> {

    private Float distance;
    private Shop shop;

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Override
    public int compareTo(DistanceShop anotherShop) {
        return this.distance.compareTo(anotherShop.getDistance());
    }
}
