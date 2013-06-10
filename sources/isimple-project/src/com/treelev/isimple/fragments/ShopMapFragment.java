package com.treelev.isimple.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.ShopInfoActivity;
import com.treelev.isimple.domain.db.Shop;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.domain.ui.DistanceShop;
import com.treelev.isimple.utils.managers.LocationTrackingManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopMapFragment extends SupportMapFragment implements GoogleMap.OnInfoWindowClickListener {

    private List<AbsDistanceShop> shopList;
    private final static float START_MAP_ZOOM = 5.0f;
    private Map<Marker, Shop> markerShopMap;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addShopsMarkers();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent startIntent = new Intent(getActivity(), ShopInfoActivity.class);
        startIntent.putExtra(ShopInfoActivity.SHOP, markerShopMap.get(marker));
        startActivity(startIntent);
        getActivity().overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    public void setNearestShopList(List<AbsDistanceShop> shopList) {
        this.shopList = shopList;
    }

    private void addShopsMarkers() {
        if (shopList != null) {
            GoogleMap map = getMap();
            Location currentLocation = LocationTrackingManager.getCurrentLocation(getActivity());
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            markerShopMap = new HashMap<Marker, Shop>();
            map.addMarker(new MarkerOptions().position(currentLatLng));
            for (AbsDistanceShop distanceShop : shopList) {
                Shop shop = ((DistanceShop) distanceShop).getShop();
                Marker marker = map.addMarker(new MarkerOptions()
                        .title(shop.getLocationName())
                        .snippet(shop.getLocationAddress())
                        .position(new LatLng((double) shop.getLatitude(), (double) shop.getLongitude())));
                markerShopMap.put(marker, shop);
            }
            map.setOnInfoWindowClickListener(this);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, START_MAP_ZOOM));
        }
    }
}
