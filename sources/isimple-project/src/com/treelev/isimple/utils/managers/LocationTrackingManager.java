package com.treelev.isimple.utils.managers;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import org.holoeverywhere.app.Application;

import java.util.List;


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

    public  void stopLocationListener(){
        mLocationManager.removeUpdates(this);
//        turnGPSOff();
    }

    private void turnGPSOff(){
        String provider = Settings.Secure.getString(Application.getLastInstance().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            Application.getLastInstance().sendBroadcast(poke);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mLocation != null){
            if(mLocation.getAccuracy() < location.getAccuracy()){
                mLocation = location;
            }
        }
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
