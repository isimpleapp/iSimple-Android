package com.treelev.isimple.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import org.holoeverywhere.app.Activity;

/**
 * Created with IntelliJ IDEA.
 * User: mhviedchenia
 * Date: 23.05.13
 * Time: 19:02
 * To change this template use File | Settings | File Templates.
 */
public class ShopsActivity extends Activity implements
        ActionBar.OnNavigationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shops_main);
        createNavigation();
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

    private void createNavigation() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Context context = getSupportActionBar().getThemedContext();
        String[] locations = getResources().getStringArray(R.array.main_menu_items);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.main_menu_icons);
        Drawable[] iconLocation = new Drawable[typedArray.length()];
        for (int i = 0; i < locations.length; ++i) {
            iconLocation[i] = typedArray.getDrawable(i);
        }
        NavigationListAdapter list = new NavigationListAdapter(this, iconLocation, locations);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
        getSupportActionBar().setSelectedNavigationItem(1);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Intent newIntent = null;
        switch (itemPosition) {
            case 0: //Catalog
                newIntent = new Intent(this, CatalogListActivity.class);
                break;
            case 1: //Shops
                break;
            case 2: //Favorites
                break;
            case 3: //Basket
                break;
            case 4: //Scan Code
                break;
            default:
                Log.v("Exception", "Unkown item menu");
        }
        if( newIntent != null )
        {
            getSupportActionBar().setSelectedNavigationItem(1);
            startActivity(newIntent);
            finish();
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        }
        return false;
    }
}
