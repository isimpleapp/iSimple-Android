package com.treelev.isimple.domain.ui;

/**
 * Created with IntelliJ IDEA.
 * User: mhviedchenia
 * Date: 25.05.13
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
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
