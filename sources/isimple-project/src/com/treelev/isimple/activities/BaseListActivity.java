package com.treelev.isimple.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import com.actionbarsherlock.app.ActionBar;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import org.holoeverywhere.app.ListActivity;

public class BaseListActivity extends ListActivity implements ActionBar.OnNavigationListener {

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Intent newIntent = getStartIntentByItemPosition(itemPosition);
        if (newIntent != null) {
            getSupportActionBar().setSelectedNavigationItem(0);
            startActivity(newIntent);
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        }
        return false;
    }

    protected void createNavigationMenuBar() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Resources resources = getResources();
        String[] mainMenuLabelsArray = resources.getStringArray(R.array.main_menu_items);
        TypedArray typedIconsArray = resources.obtainTypedArray(R.array.main_menu_icons);
        Drawable[] iconLocation = getIconsList(typedIconsArray, mainMenuLabelsArray.length);
        organizeNavigationMenu(iconLocation, mainMenuLabelsArray);
    }

    private Intent getStartIntentByItemPosition(int itemPosition) {
        switch (itemPosition) {
            case 0: //Catalog
                return null;
            case 1:
                return new Intent(this, ShopsActivity.class);
            case 2: //Favorites
                return null;
            case 3: //Basket
                return null;
            case 4: //Scan Code
                return null;
            default:
                return null;
        }
    }

    private Drawable[] getIconsList(TypedArray typedIconsArray, int navigationMenuBarLenght) {
        Drawable[] iconLocation = new Drawable[typedIconsArray.length()];
        for (int i = 0; i < navigationMenuBarLenght; ++i) {
            iconLocation[i] = typedIconsArray.getDrawable(i);
        }
        return iconLocation;
    }

    private void organizeNavigationMenu(Drawable[] iconLocation, String[] mainMenuLabelsArray) {
        NavigationListAdapter navigationAdapter = new NavigationListAdapter(this, iconLocation, mainMenuLabelsArray);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(navigationAdapter, this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
        getSupportActionBar().setSelectedNavigationItem(0);
    }
}
