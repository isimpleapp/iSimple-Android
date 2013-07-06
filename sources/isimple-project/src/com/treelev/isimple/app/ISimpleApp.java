package com.treelev.isimple.app;

import android.location.Location;
import com.treelev.isimple.R;
import com.treelev.isimple.data.ShopDAO;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.holoeverywhere.app.Application;

import java.util.List;

@ReportsCrashes(
    formKey = "",
    mailTo = "dkhanevich@gmail.com, dv@treelev.com, dshaplyko@omertex.com",
    mode = ReportingInteractionMode.DIALOG,
    resDialogTitle = R.string.crash_dialog_title,
    resDialogText = R.string.crash_dialog_text
)
public class ISimpleApp extends Application {

    private List<AbsDistanceShop> distanceShopList;
    private Location currentLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }

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
