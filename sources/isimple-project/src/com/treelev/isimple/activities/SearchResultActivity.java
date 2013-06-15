package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.RadioGroup;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.CatalogItemCursorAdapter;
import com.treelev.isimple.cursorloaders.SelectBySearch;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.Toast;

public class SearchResultActivity extends BaseListActivity implements RadioGroup.OnCheckedChangeListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    public static Integer categoryID;
//    public static Class backActivity;
    public static String locationId;
    private CatalogItemCursorAdapter mListSearchAdapter;
    private String mQuery;
    private View darkView;
    private int mSortBy = ProxyManager.SORT_NAME_AZ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        if(locationId == null ){
            setCurrentCategory(0);
        } else {
            setCurrentCategory(1);
        }
        createNavigationMenuBar();
        RadioGroup rg = (RadioGroup) findViewById(R.id.sort_group);
        rg.setOnCheckedChangeListener(this);
        ProxyManager mProxyManager = new ProxyManager(this);
        darkView = findViewById(R.id.category_dark_view);
        darkView.setVisibility(View.GONE);
        darkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        handledIntent(getIntent());
        mListSearchAdapter = new CatalogItemCursorAdapter(null, SearchResultActivity.this, true, false);
        getListView().setAdapter(mListSearchAdapter);
    }

    @Override
    protected void onResume() {
        getSupportLoaderManager().restartLoader(0, null, this);
        super.onResume();
    }

    @Override
    public void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        if( locationId != null ){
            getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
        }
    }

    @Override
    protected void onNewIntent(Intent newIntent) {
        setIntent(newIntent);
        finish();
        startActivity(getIntent());
//        handledIntent(newIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.search, menu);
        final MenuItem mItemSearch = menu.findItem(R.id.menu_search);
        SearchManager searcMenager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searcMenager.getSearchableInfo(getComponentName()));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return query.trim().length() == 0;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
        mSearchView.setOnQueryTextFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    darkView.setVisibility(View.VISIBLE);
                    darkView.getBackground().setAlpha(150);
                } else {
                    darkView.setVisibility(View.GONE);
                    mItemSearch.collapseActionView();
                    mSearchView.setQuery("", false);
                }
            }
        });
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener()
        {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                darkView.setVisibility(View.GONE);
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item)
            {
//                darkView.setVisibility(View.VISIBLE);
//                darkView.getBackground().setAlpha(150);
                return true;
            }
        });

        mSearchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                back();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int rgb) {
        int mS = 0;
        switch (rgb) {
            case R.id.alphabet_sort:
                mSortBy = ProxyManager.SORT_NAME_AZ;
                break;
            case R.id.price_sort:
                mSortBy = ProxyManager.SORT_PRICE_UP;
                break;
        }
        if(getListView().getCount() > 0) {
            getSupportLoaderManager().restartLoader(0, null, this);
        } else {
//            Toast.makeText(this, this.getString(R.string.message_not_found), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor) l.getAdapter().getItem(position);
        Intent startIntent;
        if( product.getInt(14) > 1){
            startIntent = new Intent(this, CatalogSubCategory.class);
            CatalogSubCategory.categoryID = categoryID;
            CatalogSubCategory.backActivity = SearchResultActivity.class;
            startIntent.putExtra(CatalogByCategoryActivity.DRINK_ID, product.getString(12));
        } else {
            startIntent = new Intent(this, ProductInfoActivity.class);
            startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        }
        startIntent.putExtra(ShopInfoActivity.LOCATION_ID, locationId);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private void handledIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String mDrinkId = intent.getStringExtra(CatalogByCategoryActivity.DRINK_ID);
            mQuery = intent.getStringExtra(SearchManager.QUERY);
        }
    }

    private void back() {
//        Intent intent = new Intent(this, backActivity);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(CatalogListActivity.CATEGORY_ID, categoryID);
//        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    private void updateList(int sortBy) {

    }

    private Dialog mDialog;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mDialog = ProgressDialog.show(this, this.getString(R.string.dialog_title),
                this.getString(R.string.dialog_select_data_message), false, false);
        return new SelectBySearch(this, mQuery, categoryID, locationId, mSortBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mListSearchAdapter.swapCursor(cursor);
        mDialog.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mListSearchAdapter.swapCursor(null);
    }
}