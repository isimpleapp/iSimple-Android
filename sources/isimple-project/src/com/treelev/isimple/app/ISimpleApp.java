
package com.treelev.isimple.app;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.holoeverywhere.app.Application;

import android.content.Context;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.parse.Parse;
import com.treelev.isimple.R;
import com.treelev.isimple.data.ShopDAO;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.utils.managers.ProxyManager;

@ReportsCrashes(
        formKey = "",
        mailTo = "dv@treelev.com, vitalyduong@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogText = R.string.crash_dialog_text)
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

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "EOjaTLOWC0dTftQKqLhP1WLKQADQ1Sbu1aJo5av1",
                "9vrbvgnVmBPnGEhfgFOpyWHoXOvkB2YAd2kEErsL");
        
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

    // Cart
    private Boolean mIsCartActive;

    public void updateStateCart() {
        mIsCartActive = ProxyManager.getInstanse().getCountOrders() > 0;
    }

    public void setActiveCartState() {
        mIsCartActive = true;
    }

    public void setDisactiveCartState() {
        mIsCartActive = false;
    }

    public boolean getStateCart() {
        return mIsCartActive != null ? mIsCartActive : false;
    }

    // track the status of the application is minimized or closed
    private int mCountRefActivity;

    public void incRefActivity() {
        ++mCountRefActivity;
    }

    public void decRefActivity() {
        --mCountRefActivity;
    }

    public int getCountRefActivity() {
        return mCountRefActivity;
    }
    
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }

    public static String getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getInstantce().getSystemService(
                Context.TELEPHONY_SERVICE);
        String imeiOrEsn = telephonyManager.getDeviceId();

        WifiManager manager = (WifiManager) getInstantce().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String macAddress = info.getMacAddress();

        String uuid = UUID.randomUUID().toString();

        return new StringBuilder(imeiOrEsn).append("_").append(macAddress).append("_")
                .append(uuid).toString();
    }
    
    public static String getDeviceName() {
        return android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
    }
    
}
