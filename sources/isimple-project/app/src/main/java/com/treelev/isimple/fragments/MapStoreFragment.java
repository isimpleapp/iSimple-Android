//package com.treelev.isimple.fragments;
//
//import android.os.Bundle;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.*;
//
//public class MapStoreFragment extends SupportMapFragment {
//
//    public static final String COORDINATE_STORE = "coordinate_store";
//
//    private final static float START_MAP_ZOOM = 15.0f;
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        LatLng latLngStore = getArguments().getParcelable(COORDINATE_STORE);
//        if(latLngStore != null){
//            BitmapDescriptor btmDescCurrentLoc = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
//            MarkerOptions storeMarker = new MarkerOptions().position(latLngStore).icon(btmDescCurrentLoc);
//            GoogleMap map = getMap();
//            map.addMarker(storeMarker);
//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngStore, START_MAP_ZOOM));
//        }
//    }
//}
