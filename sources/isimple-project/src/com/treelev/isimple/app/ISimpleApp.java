package com.treelev.isimple.app;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.location.Location;
import com.treelev.isimple.data.ShopDAO;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.preference.PreferenceManager;

import java.io.*;
import java.util.Date;
import java.util.List;

public class ISimpleApp extends Application {

    private List<AbsDistanceShop> distanceShopList;
    private Location currentLocation;
    private final static String[] urlList = new String[]{
            "http://s1.isimpleapp.ru/xml/ver0/Catalog-Update.xmlz",
            "http://s1.isimpleapp.ru/xml/ver0/Item-Prices.xmlz",
            "http://s1.isimpleapp.ru/xml/ver0/Item-Availability.xmlz",
            "http://s1.isimpleapp.ru/xml/ver0/Locations-And-Chains-Update.xmlz",
            "http://s1.isimpleapp.ru/xml/ver0/Delivery.xmlz",
            "http://s2.isimpleapp.ru/xml/ver0/Featured.xmlz",
            "http://s1.isimpleapp.ru/xml/ver0/Deprecated.xmlz"
    };

    @Override
    public void onCreate() {
        super.onCreate();
        importDBFromFile(false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
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

    private void importDBFromFile(boolean ovveride) {
        try {
            AssetManager am = getApplicationContext().getAssets();
            File file = new File("/data/data/com.treelev.isimple/databases/");
            file.mkdir();
            File dbFile = new File("/data/data/com.treelev.isimple/databases/iSimple.db");
            if (ovveride) {
                createDb(dbFile, am);
                putFileDatesInPref();
            } else {
                if (!dbFile.exists()) {
                    createDb(dbFile, am);
                    putFileDatesInPref();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void putFileDatesInPref() {
        SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        /*for (String url : urlList) {
            prefEditor.putLong(url, System.currentTimeMillis());
        }*/
        prefEditor.putLong(urlList[0], new Date(113, 5, 1).getTime());
        prefEditor.putLong(urlList[1], new Date(113, 5, 28).getTime());
        prefEditor.putLong(urlList[2], new Date(113, 5, 1).getTime());
        prefEditor.putLong(urlList[3], new Date(113, 5, 1).getTime());
        prefEditor.putLong(urlList[4], new Date(113, 5, 1).getTime());
        prefEditor.putLong(urlList[5], new Date(113, 5, 1).getTime());
        prefEditor.putLong(urlList[6], new Date(113, 5, 1).getTime());
        prefEditor.commit();
    }

    private void createDb(File dbFile, AssetManager am) throws IOException {
        OutputStream os = new FileOutputStream(dbFile);
        byte[] b = new byte[4096];
        int r;
        InputStream is = am.open("iSimple.db");
        while ((r = is.read(b)) > -1) {
            os.write(b, 0, r);
        }
        is.close();
        os.close();
    }
}
