package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.cursorloaders.SelectSectionsItems;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.widget.ExpandableListView;
import android.widget.RelativeLayout;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.CatalogItemTreeCursorAdapter;

public class CatalogListActivityNew extends BaseExpandableListActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{


    public final static String CATEGORY_ID = "category_id";
    private View darkView;
    private RelativeLayout myLayout;
    private View mHeader;
    private final static int NAVIGATE_CATEGORY_ID = 0;
    private CatalogItemTreeCursorAdapter mListCategoriesAdapter;

    @Override
    protected void onCreate(Bundle sSavedInstanceState) {
        super.onCreate(sSavedInstanceState);
        setContentView(R.layout.catalog_list_layout_new);
        setCurrentCategory(NAVIGATE_CATEGORY_ID);
        createNavigationMenuBar();
        darkView = findViewById(R.id.dark_view);
        darkView.setVisibility(View.GONE);
        darkView.setOnClickListener(null);
        ExpandableListView listView = getExpandableListView();
        mHeader = getLayoutInflater().inflate(R.layout.catalog_list_header_view, listView, false);
        listView.addHeaderView(mHeader, null, false);
        mListCategoriesAdapter = new CatalogItemTreeCursorAdapter(CatalogListActivityNew.this, null, getSupportLoaderManager(), true, false);
        listView.setAdapter(mListCategoriesAdapter);
    }


    @Override
    protected void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onResume() {
        getSupportLoaderManager().restartLoader(0, null, this);
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        final MenuItem mItemSearch = menu.findItem(R.id.menu_search);
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() != 0) {
                    SearchResultActivity.categoryID = null;
                    SearchResultActivity.locationId = null;
//                    SearchResultActivity.backActivity = CatalogListActivity.class;
                    return false;
                } else
                    return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
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
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                darkView.setVisibility(View.GONE);
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }
        });
        mSearchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    public void onClickCategoryButt(View v) {
        Intent startIntent = new Intent(getApplicationContext(), CatalogByCategoryActivity.class);
        Integer category = DrinkCategory.getItemCategoryByButtonId(v.getId());
        startIntent.putExtra(CATEGORY_ID, category);
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new SelectSectionsItems(this, ProxyManager.TYPE_SECTION);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mListCategoriesAdapter.setGroupCursor(cursor);
        Log.v("Testting", "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.v("Testting", "onLoaderReset");
    }
}
