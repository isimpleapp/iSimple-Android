package com.treelev.isimple.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.DeliveryZone;
import com.treelev.isimple.domain.db.Shop;
import com.treelev.isimple.utils.managers.ProxyManager;
import TextView;

public class PickupActivity extends BaseActivity
        implements View.OnClickListener{

    public final static  String PLACE_STORE = "place_store";

    private final static int NAVIGATE_CATEGORY_ID = 3;

    //ANALYTICS
//    private LatLng mStoreLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickup_layout);
        setCurrentCategory(NAVIGATE_CATEGORY_ID);
        createNavigationMenuBar();
        initView();
    }

    private void initView(){
        String name = getIntent().getStringExtra(PLACE_STORE);
        if(!TextUtils.isEmpty(name)) {
            DeliveryZone deliveryZone = ProxyManager.getInstanse().getDeliveryZone(name);
            if(deliveryZone != null) {
                ((TextView)findViewById(R.id.title_store)).setText(getLabel(deliveryZone.getName()));
                ((TextView)findViewById(R.id.address_store)).setText(deliveryZone.getAddress());
                findViewById(R.id.build_route_btn).setOnClickListener(this);
                //ANALYTICS
//                mStoreLatLng = new LatLng(deliveryZone.getLatitude(), deliveryZone.getLongitude());
//                createMapFragment(mStoreLatLng);
            }
        }
    }

    private String getLabel(String nameDeliveryZone){
        String placeName = nameDeliveryZone;
        if(nameDeliveryZone.equalsIgnoreCase("Москва")){
            placeName = "Москвe";
        } else if(nameDeliveryZone.equalsIgnoreCase("Санкт-Петербург")){
            placeName = "Санкт-Петербурге";
        }

        return  String.format("Склад в %s", placeName);
    }

    //ANALYTICS
//    private void createMapFragment(LatLng latLng){
//        MapStoreFragment mapFragment = new MapStoreFragment();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(MapStoreFragment.COORDINATE_STORE, latLng);
//        mapFragment.setArguments(bundle);
//        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.map_fragment, mapFragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    protected void createNavigationMenuBar() {
        createDrawableMenu();
        getSupportActionBar().setIcon(R.drawable.menu_ico_shopping_cart);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.build_route_btn:
                //ANALYTICS
//                if(mStoreLatLng != null){
//                    Intent intent = new Intent(this, RouteDisplayActivity.class);
//                    intent.putExtra(PLACE_STORE, true);
//                    Shop shop = new Shop();
//                    shop.setLongitude((float)mStoreLatLng.longitude);
//                    shop.setLatitude((float) mStoreLatLng.latitude);
//                    intent.putExtra(ShopInfoActivity.SHOP, shop);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
//                }
                break;
        }
    }
}
