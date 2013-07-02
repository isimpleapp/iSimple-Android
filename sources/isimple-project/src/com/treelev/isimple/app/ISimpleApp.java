package com.treelev.isimple.app;

import android.location.Location;
import com.treelev.isimple.data.ShopDAO;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import org.holoeverywhere.app.Application;

import java.util.List;

public class ISimpleApp extends Application {

    private List<AbsDistanceShop> distanceShopList;
    private Location currentLocation;

    public void reloadShopList() {
        distanceShopList = new ShopDAO(this).getNearestShops(currentLocation);
    }

    public List<AbsDistanceShop> getDistanceShopList() {
        return distanceShopList;
    }

    public void setDistanceShopList(List<AbsDistanceShop> distanceShopList) {
        this.distanceShopList = distanceShopList;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location moscowLocation) {
        this.currentLocation = moscowLocation;
    }
}
