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
        String format;
        int factor;
        if( getDistance() > 1000.0f ) {
            format = "%s (%.1f км)";
            factor = 1000;
        } else {
            format = "%s (%.0f м)";
            factor = 1;
        }
        return String.format(format, shop.getLocationName(), getDistance() / factor );
    }
}
