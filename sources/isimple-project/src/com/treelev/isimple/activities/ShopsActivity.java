package com.treelev.isimple.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.fragments.ShopChainFragment;
import com.treelev.isimple.fragments.ShopListFragment;

public class ShopsActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private Fragment shopListFragment;
    private Fragment shopMapFragment;
    private Fragment shopChainFragment;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shops_layout);
        setCurrentCategory(1);
        createNavigationMenuBar();
        shopListFragment = new ShopListFragment();
        //shopMapFragment = new ShopMapFragment();
        shopChainFragment = new ShopChainFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, shopListFragment);
        fragmentTransaction.commit();
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.shop_layout_radio_group);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
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

    @Override
    protected  void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
    }
}
