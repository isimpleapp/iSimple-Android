package com.treelev.isimple.utils.managers;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import org.holoeverywhere.app.Application;

public class LocationTrackingManager implements LocationListener {

    private static LocationTrackingManager mInstant;
    private Location mLocation;
    private Context context;
    private LocationManager mLocationManager;
    private Location mLocationDefault;


    private LocationTrackingManager(Context context) {
        this.context = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationDefault = new Location("The Moscow Kremlin");
        mLocationDefault.setLongitude(37.6191666666667f);
        mLocationDefault.setLatitude(55.7505555555556f);
    }

    public static LocationTrackingManager getInstante(){
        if(mInstant == null){
            mInstant = new LocationTrackingManager(Application.getLastInstance());
        }
        return mInstant;
    }

    public Location getCurrentLocation(Context context) {
        return getCurrentLocation();
    }

    private Location getCurrentLocation() {
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        Location current = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return current != null ? current : mLocationDefault;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
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
