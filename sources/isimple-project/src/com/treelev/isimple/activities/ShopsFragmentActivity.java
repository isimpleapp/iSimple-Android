package com.treelev.isimple.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.fragments.ShopChainFragment;
import com.treelev.isimple.fragments.ShopListFragment;

import java.util.List;

public class ShopsFragmentActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private Fragment shopListFragment;
    private Fragment shopMapFragment;
    private Fragment shopChainFragment;
    private FragmentTransaction fragmentTransaction;
    public final static String ITEM_PRODUCT_ID = "id";
    public final static String NEAREST_SHOP_LIST_ID = "shops_list";
    private Cursor wineCursor;
    public final static int NAVIGATE_CATEGORY_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shops_layout);
        organizeFrameLayout();
        setCurrentCategory(NAVIGATE_CATEGORY_ID);
        createNavigationMenuBar();
        String wineId = getIntent().getStringExtra(ProductInfoActivity.ITEM_ID_TAG);
        Bundle bundle = createBundle(wineId);
        initFragments(bundle);
        organizeFragments();
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.shop_layout_radio_group);
        radioGroup.setOnCheckedChangeListener(this);
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment mapFragment;
        switch (checkedId) {
            case R.id.group_shop_list:
                fragmentTransaction.replace(R.id.fragment_container, shopListFragment);
                break;
            case R.id.group_shop_map:
                fragmentTransaction.replace(R.id.fragment_container, shopMapFragment);
                break;
            case R.id.group_shop_network:
                fragmentTransaction.replace(R.id.fragment_container, shopChainFragment);
                break;
        }
        fragmentTransaction.commit();
    }

    protected void createNavigationMenuBar() {
        createDrawableMenu();
        getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
    }

    public void setShopMapFragmentArguments(List<AbsDistanceShop> shopList) {
        //ANALYTICS
//        ((MapFragment) shopMapFragment).setNearestShopList(shopList);
    }

    private void organizeFragments() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, shopListFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void initFragments(Bundle bundle) {
        //ANALYTICS
        shopListFragment = new ShopListFragment();
        shopListFragment.setArguments(bundle);
//        shopMapFragment = new MapFragment();
        shopChainFragment = new ShopChainFragment();
        shopChainFragment.setArguments(bundle);
    }

    private void organizeFrameLayout() {
        ViewGroup frameLayout = (ViewGroup) findViewById(R.id.fragment_container);
        frameLayout.requestTransparentRegion(frameLayout);
    }

    private Bundle createBundle(String wineId) {
        Bundle bundle = new Bundle();
        bundle.putString(ProductInfoActivity.ITEM_ID_TAG, wineId);
        return bundle;
    }
}
