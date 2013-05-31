package com.treelev.isimple.utils.managers;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import java.util.List;

public class LocationTrackingManager {

    private static Location mLocation;
    static {
        mLocation = new Location("kremlin");
        mLocation.setLatitude(55.7516666666667d);
        mLocation.setLongitude(37.6177777777778d);
    }

    private LocationTrackingManager() {}

    public static Location getCurrentLocation(Context context) {
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        String provider = locationManager.getBestProvider(criteria, false);
//        return locationManager.getLastKnownLocation(provider);
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;
        long minTime = 1000;
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> matchingProviders = locationManager.getAllProviders();
        for (String provider: matchingProviders) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if ((time > minTime && accuracy < bestAccuracy)) {
                    mLocation = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                }
                else if (time < minTime && bestAccuracy == Float.MAX_VALUE && time > bestTime) {
                    mLocation = location;
                    bestTime = time;
                }
            }
        }
        mLocation.setAccuracy(0.0f);
        mLocation.setTime(0);
        return mLocation;
    }
}
