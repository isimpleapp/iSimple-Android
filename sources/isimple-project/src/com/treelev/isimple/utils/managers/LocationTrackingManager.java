package com.treelev.isimple.utils.managers;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class LocationTrackingManager {

    private Context context;

    public LocationTrackingManager(Context context) {
        this.context = context;
    }

    public Location getCurrentLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        return locationManager.getLastKnownLocation(provider);
    }
}
