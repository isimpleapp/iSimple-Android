package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ItemCursorAdapter;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;

/**
 * Created with IntelliJ IDEA.
 * User: mhviedchenia
 * Date: 20.05.13
 * Time: 18:59
 * To change this template use File | Settings | File Templates.
 */
public class CatalogSubCategory extends ListActivity implements RadioGroup.OnCheckedChangeListener,
        ActionBar.OnNavigationListener {

    public static Class backActivity;
    public static Integer categoryID;
    private Integer mCategoryID;
    private Cursor cItems;
    private SimpleCursorAdapter mListCategoriesAdapter;
    private ProxyManager mProxyManager;
    private View mDarkView;
    private String mDrinkID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        createNavigation();
        RadioGroup rg = (RadioGroup) findViewById(R.id.sort_group);
        rg.setOnCheckedChangeListener(this);
        mDarkView = findViewById(R.id.category_dark_view);
        mDarkView.setVisibility(View.GONE);
        mDarkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mDrinkID = getIntent().getStringExtra(CatalogByCategoryActivity.DRINK_ID);
        new SelectByDrinkId(this).execute(mDrinkID);
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
    public void onCheckedChanged(RadioGroup radioGroup, int rgb) {
        int sortBy = 0;
        switch (rgb) {
            case R.id.alphabet_sort:
                sortBy = ProxyManager.SORT_NAME_AZ;
                break;
            case R.id.price_sort:
                sortBy = ProxyManager.SORT_PRICE_UP;
                break;
        }
         new SortTask(this).execute(sortBy);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Intent newIntent = null;
        switch (itemPosition) {
            case 0: //Catalog
                break;
            case 1: //Shops
                newIntent = new Intent(this, ShopsActivity.class);
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
            getSupportActionBar().setSelectedNavigationItem(0);
            startActivity(newIntent);
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProxyManager != null) {
            mProxyManager.release();
            mProxyManager = null;
        }
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
        getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
        getSupportActionBar().setSelectedNavigationItem(0);
    }

    private ProxyManager getProxyManager() {
        if (mProxyManager == null) {
            mProxyManager = new ProxyManager(this);
        }
        return mProxyManager;
    }

    class SelectByDrinkId extends AsyncTask<String, Void, Cursor> {

        private Dialog mDialog;
        private Context mContext;
        private ProxyManager mProxyManager;

        private SelectByDrinkId(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), false, false);
        }

        @Override
        protected Cursor doInBackground(String... params) {
            return getProxyManager().getItemsByDrinkId(params[0], ProxyManager.SORT_NAME_AZ);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cItems = cursor;
            startManagingCursor(cItems);
            mListCategoriesAdapter = new ItemCursorAdapter(cItems, CatalogSubCategory.this, false);
            getListView().setAdapter(mListCategoriesAdapter);
            mDialog.dismiss();
        }
    }

    private class SortTask extends AsyncTask<Integer, Void, Cursor> {

        private Dialog mDialog;
        private Context mContext;

        private SortTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_sort_message), false, false);
        }

        @Override
        protected Cursor doInBackground(Integer... params) {
            return getProxyManager().getItemsByDrinkId(mDrinkID, params[0]);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cItems = cursor;
            startManagingCursor(cItems);
            mListCategoriesAdapter = new ItemCursorAdapter(cItems, CatalogSubCategory.this, false);
            getListView().setAdapter(mListCategoriesAdapter);
            mDialog.dismiss();
        }
    }

}
