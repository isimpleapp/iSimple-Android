package com.treelev.isimple.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.Shop;
import org.holoeverywhere.widget.TextView;

public class ShopInfoActivity extends BaseActivity implements View.OnClickListener {

    public static final String SHOP = "SHOP";
    public static final String LOCATION_ID = "LOCATION_ID";

    private Shop mShop;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.shop_layout);
        setCurrentCategory(1);
        createNavigationMenuBar();
        mShop = (Shop)getIntent().getSerializableExtra(SHOP);
        TextView tv = (TextView) findViewById(R.id.title_shop);
        tv.setText(mShop.getLocationName());
        tv = (TextView) findViewById(R.id.adress_shop);
        tv.setText(mShop.getLocationAddress());
        Button btn = (Button) findViewById(R.id.category_wine_butt);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.category_spirits_butt);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.category_sparkling_butt);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.category_porto_heres_butt);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.category_sake_butt);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.category_water_butt);
        btn.setOnClickListener(this);
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
    protected  void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
    }

    @Override
    public void onClick(View view) {
        Intent startIntent = new Intent(this, CatalogByCategoryActivity.class);
        Integer category  = null;
        switch (view.getId())
        {
            case R.id.category_wine_butt:
                category = 0;
                break;
            case R.id.category_spirits_butt:
                category = 3;
                break;
            case R.id.category_sparkling_butt:
                category = 1;
                break;
            case R.id.category_porto_heres_butt:
                category = 2;
                break;
            case R.id.category_sake_butt:
                category = 4;
                break;
            case R.id.category_water_butt:
                category = 5;
                break;
        }
        startIntent.putExtra(CatalogListActivity.CATEGORY_ID, category);
        startIntent.putExtra(LOCATION_ID, mShop.getLocationID());
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }
}
