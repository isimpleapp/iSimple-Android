package com.treelev.isimple.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.RouteDisplayActivity;
import com.treelev.isimple.activities.ShopInfoActivity;
import com.treelev.isimple.activities.ShopsFragmentActivity;
import com.treelev.isimple.domain.db.Shop;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.domain.ui.DistanceShop;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.LocationTrackingManager;
import com.treelev.isimple.utils.managers.RouteManager;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends SupportMapFragment implements GoogleMap.OnInfoWindowClickListener {

    private List<AbsDistanceShop> shopList;
    private final static float START_MAP_ZOOM = 5.0f;
    private final static float ROUTE_POLYLINE_WIDTH = 3.0f;
    private Map<Marker, Shop> markerShopMap;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof ShopsFragmentActivity) {
            showNearestShopMarkers();
        } else if (getActivity() instanceof RouteDisplayActivity) {
            showRouteMarkers();
        }
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

    private void showNearestShopMarkers() {
        if (shopList != null) {
            createOverlays(false);
        }
    }

    private void showRouteMarkers() {
        createOverlays(true);
    }

    private void createOverlays(boolean isRouteOverlay) {
        GoogleMap map = getMap();
        Location currentLocation = LocationTrackingManager.getCurrentLocation(getActivity());
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        markerShopMap = new HashMap<Marker, Shop>();
        BitmapDescriptor btmDescCurrentLoc = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        MarkerOptions userMarker = new MarkerOptions().position(currentLatLng).icon(btmDescCurrentLoc);
        map.addMarker(userMarker);
        Shop shop;
        if (!isRouteOverlay) {
            for (AbsDistanceShop distanceShop : shopList) {
                shop = ((DistanceShop) distanceShop).getShop();
                markerShopMap.put(createMarker(map, shop), shop);
            }
        } else {
            shop = (Shop) getArguments().getSerializable(ShopInfoActivity.SHOP);
            LatLng shopLatLng = new LatLng((double) shop.getLatitude(), (double) shop.getLongitude());
            markerShopMap.put(createMarker(map, shop), shop);
            if (Utils.isNetworkAvailable(getActivity())) {
                new CreateRouteTask(map).execute(currentLatLng, shopLatLng);
            } else {
                showAlertDialog(getString(R.string.error_dialog_title), getString(R.string.error_route_create_dialog_message));
            }
        }
        map.setOnInfoWindowClickListener(this);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, START_MAP_ZOOM));
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setNeutralButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        adb.show();
    }

    private Marker createMarker(GoogleMap map, Shop shop) {
        return map.addMarker(new MarkerOptions()
                .title(shop.getLocationName())
                .snippet(shop.getLocationAddress())
                .position(new LatLng((double) shop.getLatitude(), (double) shop.getLongitude())));
    }

    private class CreateRouteTask extends AsyncTask<LatLng, Void, PolylineOptions> {

        private GoogleMap map;
        private Dialog progressDialog;

        private CreateRouteTask(GoogleMap map) {
            this.map = map;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.dialog_title),
                    getString(R.string.load_route_dialog_progress_message), false, false);
        }

        @Override
        protected PolylineOptions doInBackground(LatLng... coordsParams) {
            return createRoute(coordsParams[0], coordsParams[1]);
        }

        @Override
        protected void onPostExecute(PolylineOptions resultPolyLine) {
            super.onPostExecute(resultPolyLine);
            map.addPolyline(resultPolyLine);
            progressDialog.dismiss();
        }

        private PolylineOptions createRoute(LatLng currentLatLng, LatLng shopLatLng) {
            List<LatLng> routeList = RouteManager.createRoute(currentLatLng, shopLatLng);
            PolylineOptions polylineOptions = new PolylineOptions();
            for (LatLng latLng : routeList) {
                polylineOptions.add(latLng);
            }
            polylineOptions.width(ROUTE_POLYLINE_WIDTH).color(Color.BLUE);
            return polylineOptions;
        }
    }
}
