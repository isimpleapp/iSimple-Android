package com.treelev.isimple.app;

import android.content.Context;
import android.location.Location;
import com.treelev.isimple.R;
import com.treelev.isimple.data.ShopDAO;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.utils.managers.ProxyManager;
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
    private static ISimpleApp instantce;

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        
        if (instantce == null) {
        	instantce = this;
        }
    }

    public static ISimpleApp getInstantce() {
		return instantce;
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

//Cart
    private  Boolean mIsCartActive;

    public void updateStateCart(){
        mIsCartActive = new ProxyManager(this).getCountOrders() > 0;
    }

    public void setActiveCartState(){
        mIsCartActive = true;
    }

    public void setDisactiveCartState(){
        mIsCartActive = false;
    }

    public boolean getStateCart(){
        return mIsCartActive != null ? mIsCartActive : false;
    }

//track the status of the application is minimized or closed
    private int mCountRefActivity;

    public void incRefActivity(){
        ++mCountRefActivity;
    }

    public void decRefActivity(){
        --mCountRefActivity;
    }

    public int getCountRefActivity(){
        return mCountRefActivity;
    }
}
