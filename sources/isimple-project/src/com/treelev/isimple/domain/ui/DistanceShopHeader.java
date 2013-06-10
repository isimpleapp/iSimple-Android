package com.treelev.isimple.domain.ui;

public class DistanceShopHeader extends AbsDistanceShop {

    private String title;

    public DistanceShopHeader(Float distance, String title){
        setDistance(distance);
        this.title = title;
    }

    @Override
    public boolean isSection() {
        return true;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
