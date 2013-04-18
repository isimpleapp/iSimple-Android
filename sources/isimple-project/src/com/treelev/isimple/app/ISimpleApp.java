package com.treelev.isimple.app;

import android.content.res.AssetManager;
import android.location.Location;
import com.treelev.isimple.data.ShopDAO;
import com.treelev.isimple.domain.ui.DistanceShop;
import org.holoeverywhere.app.Application;

import java.io.*;
import java.util.List;

public class ISimpleApp extends Application {

    private List<DistanceShop> distanceShopList;
    private Location currentLocation;

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

    public List<DistanceShop> getDistanceShopList() {
        return distanceShopList;
    }

    public void setDistanceShopList(List<DistanceShop> distanceShopList) {
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
            } else {
                if (!dbFile.exists()) {
                    createDb(dbFile, am);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
