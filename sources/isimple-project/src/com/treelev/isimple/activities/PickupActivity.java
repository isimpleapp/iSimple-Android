package com.treelev.isimple.activities;

import android.os.Bundle;
import com.treelev.isimple.R;

public class PickupActivity extends BaseActivity{

    public final static int NAVIGATE_CATEGORY_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickup_layout);
        setCurrentCategory(NAVIGATE_CATEGORY_ID);
        createNavigationMenuBar();

    }

    @Override
    protected void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        getSupportActionBar().setIcon(R.drawable.menu_ico_shopping_cart);
    }
}
