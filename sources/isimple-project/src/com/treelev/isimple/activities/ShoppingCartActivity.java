package com.treelev.isimple.activities;

import android.os.Bundle;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;

public class ShoppingCartActivity extends BaseListActivity {

    public final static int NAVIGATE_CATEGORY_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_layout);
        setCurrentCategory(NAVIGATE_CATEGORY_ID);
        createNavigationMenuBar();

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
        getSupportActionBar().setIcon(R.drawable.menu_ico_shopping_cart);
    }
}