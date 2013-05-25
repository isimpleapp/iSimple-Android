package com.treelev.isimple.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import com.actionbarsherlock.app.ActionBar;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import org.holoeverywhere.app.Activity;

public class BaseActivity extends Activity implements ActionBar.OnNavigationListener {

    private int mCurrentCategory;

    public void setCurrentCategory(int currentCategory) {
        mCurrentCategory = currentCategory;

    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Intent newIntent = getStartIntentByItemPosition(itemPosition);
        if (newIntent != null && mCurrentCategory != itemPosition) {
            startActivity(newIntent);
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
            getSupportActionBar().setSelectedNavigationItem(mCurrentCategory);
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
        Class category = null;
        Intent intent = null;
        switch (itemPosition) {
            case 0: //Catalog
//                return new Intent(this, CatalogListActivity.class);
                category = CatalogListActivity.class;
                break;
            case 1: //Shop
//                return new Intent(this, ShopsActivity.class);
                category = ShopsActivity.class;
                break;
            case 2: //Favorites
                category = null;
                break;
            case 3: //Basket
                category = null;
                break;
            case 4: //Scan Code
                category = null;
                break;
            default:
//                return null;
                category = null;
        }
        if( !this.getClass().equals(category) && category != null){
            intent =  new Intent(this, category);
        }
        return intent;
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
        getSupportActionBar().setSelectedNavigationItem(mCurrentCategory);
    }
}
