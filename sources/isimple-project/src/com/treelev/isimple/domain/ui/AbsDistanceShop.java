package com.treelev.isimple.domain.ui;

public abstract class AbsDistanceShop implements Comparable<AbsDistanceShop>, HeaderItem {

    private Float distance;

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public abstract String getTitle();

    @Override
    public int compareTo(AbsDistanceShop anotherShop) {
        return this.getDistance().compareTo(anotherShop.getDistance());
    }

}
