package com.treelev.isimple.fragments;

import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.treelev.isimple.domain.db.Shop;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.domain.ui.DistanceShop;
import com.treelev.isimple.utils.managers.LocationTrackingManager;

import java.util.List;

public class ShopMapFragment extends SupportMapFragment implements GoogleMap.OnMarkerClickListener {

    private List<AbsDistanceShop> shopList;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addShopsMarkers();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void setNearestShopList(List<AbsDistanceShop> shopList) {
        this.shopList = shopList;
    }

    private void addShopsMarkers() {
        if (shopList != null) {
            GoogleMap map = getMap();
            Location currentLocation = LocationTrackingManager.getCurrentLocation(getActivity());
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            map.addMarker(new MarkerOptions()
                    .title("Location")
                    .snippet("You are here")
                    .position(currentLatLng));
            for (AbsDistanceShop distanceShop : shopList) {
                Shop shop = ((DistanceShop) distanceShop).getShop();
                map.addMarker(new MarkerOptions()
                        .title(shop.getLocationName())
                        .snippet(shop.getLocationAddress())
                        .position(new LatLng((double) shop.getLatitude(), (double) shop.getLongitude())));
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 5));
        }
    }
}
