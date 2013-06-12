package com.treelev.isimple.utils.managers;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationTrackingManager implements LocationListener {

    private Location mLocation;
    private Context context;

    private LocationTrackingManager(Context context) {
        this.context = context;
    }

    public static Location getCurrentLocation(Context context) {
        return new LocationTrackingManager(context).getCurrentLocation();
    }

    private Location getCurrentLocation() {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        return mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
