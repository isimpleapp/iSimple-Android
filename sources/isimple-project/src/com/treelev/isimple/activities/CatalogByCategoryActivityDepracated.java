package com.treelev.isimple.activities;

import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.ExpandableListView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.FilterAdapter;
import com.treelev.isimple.adapters.itemstreecursoradapter.CatalogByCategoryItemTreeCursorAdapter;
import com.treelev.isimple.animation.AnimationWithMargins;
import com.treelev.isimple.cursorloaders.SelectSectionsItems;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.domain.ui.filter.FilterItem;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.filter.Filter;
import com.treelev.isimple.filter.PortoHeresFilter;
import com.treelev.isimple.filter.SakeFilter;
import com.treelev.isimple.filter.SparklingFilter;
import com.treelev.isimple.filter.SpiritsFilter;
import com.treelev.isimple.filter.WaterFilter;
import com.treelev.isimple.filter.WineFilter;
import com.treelev.isimple.utils.managers.ProxyManager;

public class CatalogByCategoryActivityDepracated extends BaseExpandableListActivity
        implements RadioGroup.OnCheckedChangeListener, LoaderManager.LoaderCallbacks<Cursor>,
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
    private boolean mExpandFilter = false;
    private boolean mFilterUse = false;
    private String mFilterWhereClause;
    private com.treelev.isimple.filter.Filter filter;
    private static final int ANIMATION_DURATION_IN_MILLIS = 500;
    private int mSortBy = ProxyManager.SORT_NAME_AZ;
    private int mTypeSection = ProxyManager.TYPE_SECTION_MAIN;
    private Context mContext;
    public final static String EXTRA_RESULT_CHECKED = "isChecked";
    public final static String EXTRA_CHILD_POSITION = "position";

    private int DIP50_IN_PX;
    private int DIP51_IN_PX;

    private SearchView mSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DIP50_IN_PX = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        DIP51_IN_PX = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 51, getResources().getDisplayMetrics());

        setContentView(R.layout.catalog_category_layout);
        createDrawableMenu();
        mLocationId = getIntent().getStringExtra(ShopInfoActivity.LOCATION_ID);
        mCategoryID = getIntent().getIntExtra(CatalogListActivity.CATEGORY_ID, -1);
        mContext = this;
        mTreeCategoriesAdapter = new CatalogByCategoryItemTreeCursorAdapter(mContext, null, getSupportLoaderManager(), mSortBy);

        mTreeCategoriesAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if (filterListView.isGroupExpanded(0)) { // фильтр открыт
                    getExpandableListView().post(new Runnable() {
                        @Override
                        public void run() {
                            getExpandableListView().setSelectionFromTop(2, DIP50_IN_PX);
                        }
                    });
                }
            }
        });

        if (mLocationId == null) {
            mTreeCategoriesAdapter.initCategory(mCategoryID);
            setCurrentCategory(0); //Catalog
        } else {
            mTreeCategoriesAdapter.initCategoryShop(mCategoryID, mLocationId);
            setCurrentCategory(1); //Shop
        }
        disableOnGroupClick();
        darkView = findViewById(R.id.category_dark_view_cat);
        darkView.setVisibility(View.GONE);
        darkView.setOnClickListener(null);
        filter = initFilter();
        initFilterListView();
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

//    @Override
//    public void createNavigationMenuBar() {
//        super.createNavigationMenuBar();
//        if (mLocationId != null) {
//            getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backOrCollapse();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int rgb) {
        switch (rgb) {
            case R.id.alphabet_sort:
                mSortBy = ProxyManager.SORT_NAME_AZ;
                break;
            case R.id.price_sort:
                mSortBy = ProxyManager.SORT_PRICE_UP;
                break;
        }
        mTreeCategoriesAdapter.setSortBy(mSortBy);
        mFilterWhereClause = mFilterUse ? filter.getSQLWhereClause() : mFilterWhereClause;
        initLoadManager();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean processed;
        for (FilterItem filterItem : filter.getFilterContent()) {
            processed = filterItem.processResult(requestCode, resultCode, data);
            if (processed)
                break;
        }
//        boolean addFavorite = data.getBooleanExtra(ProductInfoActivity.CHANGE_FAVOURITE, false);
//        if(addFavorite){
//            mTreeCategoriesAdapter.notifyDataSetChanged();
//        }
    }

    private Filter initFilter() {
        DrinkCategory drinkCategory = DrinkCategory.getDrinkCategory(mCategoryID);
        switch (drinkCategory) {
            case WINE:
                return new WineFilter(this, mCurrentCategory);
            case SPIRITS:
                return new SpiritsFilter(this, mCurrentCategory);
            case SPARKLING:
                return new SparklingFilter(this, mCurrentCategory);
            case SAKE:
                return new SakeFilter(this, mCurrentCategory);
            case PORTO:
                return new PortoHeresFilter(this, mCurrentCategory);
            case WATER:
                return new WaterFilter(this, mCurrentCategory);
            default:
                return null;
        }
    }

    private void backOrCollapse() {
        if (mExpandFilter) {
            Button resetButton = (Button) filterView.findViewById(R.id.reset_butt);
            if (resetButton != null) {
                resetButton.performClick();
            }  else {
                mExpandFilter = false;
            }
        } else {
            finish();
            overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
        }
    }

    private void initFilterListView() {
        BaseExpandableListAdapter filterAdapter = new FilterAdapter(this, filter);
        filterView = getLayoutInflater().inflate(R.layout.category_filter_general_layout, getExpandableListView(), false);
        filterListView = (ExpandableListView)filterView.findViewById(R.id.filtration_view);

        getExpandableListView().addHeaderView(filterView);

        ((RadioGroup) filterView.findViewById(R.id.sort_group)).setOnCheckedChangeListener(this);
        filterView.findViewById(R.id.reset_butt).setOnClickListener(footerButtonClick);
        filterView.findViewById(R.id.search_butt).setOnClickListener(footerButtonClick);

        filterListView.setAdapter(filterAdapter);

        filterListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return filter.getFilterContent().get(childPosition).process();
            }
        });

//        filterListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                mExpandFilter = false;
//            }
//        });

        filterListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                filterView.findViewById(R.id.filter_button_bar).setVisibility(View.VISIBLE);
                filterListView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        filter.getFilterContent().size() * DIP51_IN_PX)
                );
                mExpandFilter = true;
            }
        });
    }

    private View.OnClickListener footerButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.reset_butt:
//                    view.setBackgroundColor(Color.MAGENTA);
                    if (filter.isChangeState()) {
                        filter.reset();
                        filterListView.postInvalidate();
                    } else {
                        filterView.findViewById(R.id.filter_button_bar).setVisibility(View.GONE);
                        filterListView.collapseGroup(0);
                        filterListView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, DIP50_IN_PX));
                        mExpandFilter = false;
                    }
//                    view.setBackgroundResource(R.drawable.btn_filter_reset);
                    mFilterUse = false;
                    break;
                case R.id.search_butt:
//                    view.setBackgroundColor(Color.GRAY);
                    mFilterWhereClause = filter.getSQLWhereClause();
                    initLoadManager();
//                    view.setBackgroundResource(R.drawable.btn_filter_find);
                    mFilterUse = true;
                    break;
            }
        }
    };

    private void initLoadManager() {
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

