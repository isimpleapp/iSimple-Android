package com.treelev.isimple.activities;

import android.os.Bundle;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.Shop;
import com.treelev.isimple.fragments.MapFragment;

public class RouteDisplayActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_display_layout);
        setCurrentCategory(ShopsFragmentActivity.NAVIGATE_CATEGORY_ID);
        Shop shop = (Shop) getIntent().getSerializableExtra(ShopInfoActivity.SHOP);
        createNavigationMenuBar();
        organizeMapFragment(shop);
    }

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

    @Override
    protected void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
    }

    private void organizeMapFragment(Shop shop) {
        MapFragment mapFragment = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ShopInfoActivity.SHOP, shop);
        mapFragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, mapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}