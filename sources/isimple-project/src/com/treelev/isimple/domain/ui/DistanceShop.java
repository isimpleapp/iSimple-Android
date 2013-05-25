package com.treelev.isimple.domain.ui;

import com.treelev.isimple.domain.db.Shop;

public class DistanceShop extends AbsDistanceShop {

    private Shop shop;

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Override
    public boolean isSection() {
        return false;
    }

    @Override
    public String getTitle() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
