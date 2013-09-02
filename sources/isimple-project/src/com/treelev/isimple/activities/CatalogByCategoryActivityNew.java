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
import android.view.animation.TranslateAnimation;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.itemstreecursoradapter.CatalogByCategoryItemTreeCursorAdapter;
import com.treelev.isimple.animation.AnimationWithMargins;
import com.treelev.isimple.cursorloaders.SelectSectionsItems;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.fragments.filters.FilterFragment;
import com.treelev.isimple.fragments.filters.WaterFilter;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.widget.ExpandableListView;

public class CatalogByCategoryActivityNew extends BaseExpandableListActivity
        implements  LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener{

    private final static String FIELD_TAG = "field_tag";
    public final static String FILTER_DATA_TAG = "filter_data";
    public final static String DRINK_ID = "drink_id";
    public final static String FILTER_WHERE_CLAUSE = "filter_where_clauses";

    private CatalogByCategoryItemTreeCursorAdapter mTreeCategoriesAdapter;
    private ExpandableListView filterListView;
    private View darkView;
    private View filterView;
    private RelativeLayout filterContainer;
    private AnimationWithMargins filterCollapseAnimation;
    private AnimationWithMargins filterExpandAnimation;
    private TranslateAnimation filterInstantAnimation;

    private Integer mCategoryID;
    private String mLocationId;
    private String mFilterWhereClause;

    private static final int ANIMATION_DURATION_IN_MILLIS = 500;
    private int mSortBy = ProxyManager.SORT_NAME_AZ;
    private int mTypeSection = ProxyManager.TYPE_SECTION_MAIN;
    private Context mContext;
    private FilterFragment mFilter;
    public final static String EXTRA_RESULT_CHECKED = "isChecked";
    public final static String EXTRA_CHILD_POSITION = "position";

    private int DIP50_IN_PX;
    private int DIP51_IN_PX;

    private SearchView mSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_category_layout_new);
        mLocationId = getIntent().getStringExtra(ShopInfoActivity.LOCATION_ID);
        mCategoryID = getIntent().getIntExtra(CatalogListActivity.CATEGORY_ID, -1);
        mContext = this;
        mTreeCategoriesAdapter = new CatalogByCategoryItemTreeCursorAdapter(mContext, null, getSupportLoaderManager(), mSortBy);
        if (mLocationId == null) {
            mTreeCategoriesAdapter.initCategory(mCategoryID);
            setCurrentCategory(0); //Catalog
        } else {
            mTreeCategoriesAdapter.initCategoryShop(mCategoryID, mLocationId);
            setCurrentCategory(1); //Shop
        }
        disableOnGroupClick();
        createNavigationMenuBar();
        darkView = findViewById(R.id.category_dark_view_cat);
        darkView.setVisibility(View.GONE);
        darkView.setOnClickListener(null);
        initFilter();
        getExpandableListView().setAdapter(mTreeCategoriesAdapter);
        initLoadManager();
    }

    @Override
    protected void onResume() {
        if(mEventChangeDataBase){
            mTreeCategoriesAdapter.refresh();
            mEventChangeDataBase = false;
        }
        super.onResume();
    }

    @Override
    public void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        if (mLocationId != null) {
            getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backOrCollapse();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private SearchView.OnQueryTextListener mQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            SearchResultActivity.categoryID = mCategoryID;
            SearchResultActivity.locationId = mLocationId;
            return query.trim().length() < LENGTH_SEARCH_QUERY;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        SearchResultActivity.categoryID = null;
        SearchResultActivity.locationId = null;
        return query.trim().length() < LENGTH_SEARCH_QUERY;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        final MenuItem mItemSearch = menu.findItem(R.id.menu_search);
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    darkView.setVisibility(View.VISIBLE);
                    darkView.getBackground().setAlpha(150);
                    mSearchView.setQuery(mSearchView.getQuery(), false);
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
        mSearchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Cursor product = mTreeCategoriesAdapter.getChild(groupPosition, childPosition);
        Intent startIntent;
        int itemCountIndex = product.getColumnIndex("count");
        if (product.getInt(itemCountIndex) > 1) {
            int itemDrinkIdIndex = product.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_ID);
            startIntent = new Intent(this, CatalogSubCategoryTree.class);
            startIntent.putExtra(DRINK_ID, product.getString(itemDrinkIdIndex));
            startIntent.putExtra(ShopInfoActivity.LOCATION_ID, mLocationId);
            startIntent.putExtra(FILTER_WHERE_CLAUSE, mFilterWhereClause);
            startActivity(startIntent);
        } else {
            startIntent = new Intent(this, ProductInfoActivity.class);
            startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
            startActivityForResult(startIntent, 0);
        }
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }

    @Override
    public void onBackPressed() {
        backOrCollapse();
    }

    private void initFilter() {
        DrinkCategory drinkCategory = DrinkCategory.getDrinkCategory(mCategoryID);
        View header = null;
        FilterFragment.FilterType type = null;
        switch (drinkCategory) {
            case WINE:
                header = getLayoutInflater().inflate(R.layout.wine_filter_fragment);

                break;
            case SPIRITS:
                header = getLayoutInflater().inflate(R.layout.spirits_filter_fragment);
                break;
            case SPARKLING:
                header = getLayoutInflater().inflate(R.layout.sparkilng_filter_fragment);

                break;
            case SAKE:
                header = getLayoutInflater().inflate(R.layout.sake_filter_fragment);
                break;
            case PORTO:
                header = getLayoutInflater().inflate(R.layout.porto_heres_filter_fragment);
                break;
            case WATER:
                header = getLayoutInflater().inflate(R.layout.water_filter_fragment);
                initWaterFilter(header);
                break;
            default:
                return;
        }
        getExpandableListView().addHeaderView(header);
    }

    private void initWaterFilter(View view){
        mFilter = (FilterFragment) getSupportFragmentManager().findFragmentById(R.id.filter_fragment);
        WaterFilter waterFilter = (WaterFilter) mFilter;
        waterFilter.initFilterItems(0, 100000);
        waterFilter.setOnChangeFilterListener(new FilterFragment.OnChangeStateListener() {
            @Override
            public void onChangeFilterState(String whereClause, boolean group) {
                Log.v("WhereClause = ", whereClause);
                initLoadManager();
            }
        });
    }

    private void backOrCollapse() {
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    private void initLoadManager() {
//TODO
        mFilterWhereClause = mFilter.getWhereClause();
        mSortBy = mFilter.getSortBy();
        mTreeCategoriesAdapter.setSortBy(mSortBy);
        if(mFilterWhereClause == null){
            if(mLocationId == null){
                mTypeSection = ProxyManager.TYPE_SECTION_MAIN;
            } else {
                mTypeSection = ProxyManager.TYPE_SECTION_SHOP_MAIN;
            }
        } else if(mTypeSection != ProxyManager.TYPE_SECTION_FILTRATION_SEARCH){
            mTreeCategoriesAdapter.initFilter(mFilterWhereClause, mLocationId, mSortBy);
            mTypeSection = ProxyManager.TYPE_SECTION_FILTRATION_SEARCH;
        }
        if(mFilterWhereClause != null ) {
            mTreeCategoriesAdapter.setFilterWhereClause(mFilterWhereClause);
        }
        getSupportLoaderManager().restartLoader(mTypeSection, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new SelectSectionsItems(mContext, i);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mTreeCategoriesAdapter.setGroupCursor(cursor);
        mTreeCategoriesAdapter.notifyDataSetChanged();
        expandAllGroup();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

}
