package com.treelev.isimple.activities;

import android.os.Bundle;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.Shop;
import org.holoeverywhere.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: mhviedchenia
 * Date: 25.05.13
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class ShopActivity extends BaseActivity {

    public static final String SHOP = "SHOP";

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
}
