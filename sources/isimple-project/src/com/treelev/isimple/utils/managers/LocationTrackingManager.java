package com.treelev.isimple.utils.managers;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import org.holoeverywhere.app.Application;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
//        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//        Location current = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        List<String> providers = mLocationManager.getProviders(true);
        float accuracy = mLocation != null ? mLocation.getAccuracy() : Float.MIN_VALUE;
        float currentAccuracy;
        Location currentLocation;
        for(String provider : providers){
            mLocationManager.requestLocationUpdates(provider, 0, 0, this);
            currentLocation = mLocationManager.getLastKnownLocation(provider);
            if(currentLocation != null){
                currentAccuracy = currentLocation.getAccuracy();
                if(currentAccuracy > accuracy){
                    accuracy = currentAccuracy;
                    mLocation = currentLocation;
                }
            }
        }
        return mLocation != null ? mLocation : mLocationDefault;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("LocationTrackingManager ", "onLocationChanged");
        if(mLocation != null){
            if(mLocation.getAccuracy() < location.getAccuracy()){
                mLocation = location;
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.v("LocationTrackingManager ", "onStatusChanged");
        Log.v("LocationTrackingManager ", provider);
        Log.v("LocationTrackingManager", String.valueOf(status));
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v("LocationTrackingManager ", "onProviderEnabled");
        Log.v("LocationTrackingManager", provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.v("LocationTrackingManager ", "onProviderDisabled");
        Log.v("LocationTrackingManager", provider);
    }
}
