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
import com.treelev.isimple.adapters.itemstreecursoradapter.SearchItemTreeCursorAdapter;
import com.treelev.isimple.cursorloaders.SelectSectionsItems;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.widget.ExpandableListView;

public class SearchResultActivityNew extends  BaseExpandableListActivity
        implements RadioGroup.OnCheckedChangeListener,  LoaderManager.LoaderCallbacks<Cursor>{

    public static Integer categoryID;
    public static String locationId;
    private SearchItemTreeCursorAdapter mTreeSearchAdapter;
    private String mQuery;
    private View darkView;
    private int mSortBy = ProxyManager.SORT_NAME_AZ;
    private String mLocationId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_new);
        mLocationId = locationId;
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
        mTreeSearchAdapter = new SearchItemTreeCursorAdapter(this, null, getSupportLoaderManager(), mQuery, categoryID, locationId, mSortBy);
        getExpandableListView().setAdapter(mTreeSearchAdapter);
        getExpandableListView().setOnChildClickListener(this);
        disableOnGroupClick();
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
        if(getExpandableListView().getCount() > 0) {
            mTreeSearchAdapter.setSortBy(mSortBy);
            getSupportLoaderManager().restartLoader(0, null, this);
        } else {
//            Toast.makeText(this, this.getString(R.string.message_not_found), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Cursor product = mTreeSearchAdapter.getChild(groupPosition, childPosition);
        Intent startIntent;
        int itemCountIndex = product.getColumnIndex("count");
        if (product.getInt(itemCountIndex) > 1) {
            int itemDrinkIdIndex = product.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_ID);
            startIntent = new Intent(this, CatalogSubCategory.class);
            CatalogSubCategory.categoryID = null;
            CatalogSubCategory.backActivity = CatalogListActivityNew.class;
            startIntent.putExtra(CatalogByCategoryActivityNew.DRINK_ID, product.getString(itemDrinkIdIndex));
            startIntent.putExtra(ShopInfoActivity.LOCATION_ID, mLocationId);
        } else {
            startIntent = new Intent(this, ProductInfoActivity.class);
            startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        }
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }

    private void handledIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String mDrinkId = intent.getStringExtra(CatalogListActivityNew.DRINK_ID);
            mQuery = intent.getStringExtra(SearchManager.QUERY);
        }
    }

    private void back() {
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new SelectSectionsItems(this, ProxyManager.TYPE_SECTION_FILTRATION_SEARCH);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mTreeSearchAdapter.setGroupCursor(cursor);
        mTreeSearchAdapter.notifyDataSetChanged();
        expandAllGroup();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}