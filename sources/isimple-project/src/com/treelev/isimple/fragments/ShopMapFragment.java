package com.treelev.isimple.fragments;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShopMapFragment extends SupportMapFragment {

    private static final LatLng HAMBURG = new LatLng(53.558, 9.927);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onActivityCreated called!");
        super.onActivityCreated(savedInstanceState);
        GoogleMap map = getMap();
        map.addMarker(new MarkerOptions().title("Hamburg").position(HAMBURG));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }
}
