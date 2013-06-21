package com.treelev.isimple.activities;

import android.support.v4.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.CatalogItemCursorAdapter;
import com.treelev.isimple.adapters.FilterAdapter;
import com.treelev.isimple.animation.AnimationWithMargins;
import com.treelev.isimple.cursorloaders.SelectItemsByCategory;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.domain.ui.filter.FilterItem;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.filter.Filter;
import com.treelev.isimple.filter.*;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.ListView;

public class CatalogByCategoryActivity extends BaseListActivity implements RadioGroup.OnCheckedChangeListener,
        ExpandableListView.OnGroupExpandListener, ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupCollapseListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private final static String FIELD_TAG = "field_tag";
    public final static String FILTER_DATA_TAG = "filter_data";
    public final static String DRINK_ID = "drink_id";
    public final static String FILTER_WHERE_CLAUSE = "filter_where_clauses";
    private Cursor cItems;
    private CatalogItemCursorAdapter mListCategoriesAdapter;
    private ExpandableListView filterListView;
    private View footerView;
    private View darkView;
    private RelativeLayout filterContainer;
    private AnimationWithMargins filterCollapseAnimation;
    private AnimationWithMargins filterExpandAnimation;
    private TranslateAnimation filterInstantAnimation;
    int newLayoutHeight = 120;
    int oldLayoutHeight = 350;
    final int myGroupPosition = 0;
    private Integer mCategoryID;
    private String mLocationId;
    private boolean mExpandFiltr = false;
    private boolean mFiltrUse = false;
    private String mFilterWhereClause;
    private com.treelev.isimple.filter.Filter filter;
    private static final int ANIMATION_DURATION_IN_MILLIS = 500;
    private int mSortBy = ProxyManager.SORT_NAME_AZ;
    private Context mContext;
    public final static String EXTRA_RESULT_CHECKED = "isChecked";
    public final static String EXTRA_CHILD_POSITION = "position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_category_layout);
        mLocationId = getIntent().getStringExtra(ShopInfoActivity.LOCATION_ID);
        View mViewActivity = findViewById(R.layout.catalog_category_layout);
        if (mLocationId == null) {
            setCurrentCategory(0);    //Catalog
        } else {
            setCurrentCategory(1); //Shop
        }
        createNavigationMenuBar();
        darkView = findViewById(R.id.category_dark_view);
        darkView.setVisibility(View.GONE);
        darkView.setOnClickListener(null);
        mCategoryID = getIntent().getIntExtra(CatalogListActivity.CATEGORY_ID, -1);
        mContext = this;
        filter = initFilter();
        initFilterListView();
        mListCategoriesAdapter = new CatalogItemCursorAdapter(null, CatalogByCategoryActivity.this, true, false);
        getListView().setAdapter(mListCategoriesAdapter);
//        new SelectDataTask(this, filter.getSQLWhereClause(), mLocationId).execute(mCategoryID, mSortBy);
    }

    @Override
    protected void onResume() {
        initLoadManager();
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
//        stopManagingCursor(cItems);
//        cItems.close();
        mFilterWhereClause = mFiltrUse ? filter.getSQLWhereClause() : mFilterWhereClause;
//        new SortTask(this, mFilterWhereClause, mLocationId).execute(mSortBy);
        initLoadManager();
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
//                SearchResultActivity.backActivity = CatalogByCategoryActivity.class;
                SearchResultActivity.categoryID = mCategoryID;
                SearchResultActivity.locationId = mLocationId;
                return false;
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

    @Override
    public void onGroupExpand(int groupPosition) {
        footerView.findViewById(R.id.sort_group).setVisibility(View.GONE);
        footerView.findViewById(R.id.filter_button_bar).setVisibility(View.VISIBLE);
        mExpandFiltr = true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return filter.getFilterContent().get(childPosition).process();
    }

    @Override
    public void onBackPressed() {
        backOrCollapse();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor) l.getAdapter().getItem(position);
        Intent startIntent;
        int itemCountIndex = product.getColumnIndex("count");
        int itemDrinkIdIndex = product.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_ID);
        if (product.getInt(itemCountIndex) > 1) {
            startIntent = new Intent(this, CatalogSubCategory.class);
            CatalogSubCategory.categoryID = mCategoryID;
            CatalogSubCategory.backActivity = CatalogByCategoryActivity.class;
            startIntent.putExtra(DRINK_ID, product.getString(itemDrinkIdIndex));
        } else {
            startIntent = new Intent(this, ProductInfoActivity.class);
            startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        }
        startIntent.putExtra(ShopInfoActivity.LOCATION_ID, mLocationId);
        startIntent.putExtra(FILTER_WHERE_CLAUSE, filter.getSQLWhereClause());
//        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
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
        if (mExpandFiltr) {
            Button resetButton = (Button) footerView.findViewById(R.id.reset_butt);
            if (resetButton != null) {
                resetButton.performClick();
            }
            mExpandFiltr = false;
        } else {
            finish();
            overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
        }
    }

    private void initFilterListView() {
        BaseExpandableListAdapter filterAdapter = new FilterAdapter(this, filter);
        filterListView = (ExpandableListView) findViewById(R.id.filtration_view);
        filterListView.setOnGroupExpandListener(this);
        filterListView.setOnGroupCollapseListener(this);
        filterListView.setOnChildClickListener(this);
        filterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        footerView = getLayoutInflater().inflate(R.layout.category_filtration_button_bar_layout, filterListView, false);
        ((RadioGroup) footerView.findViewById(R.id.sort_group)).setOnCheckedChangeListener(this);
        footerView.findViewById(R.id.reset_butt).setOnClickListener(footerButtonClick);
        footerView.findViewById(R.id.search_butt).setOnClickListener(footerButtonClick);
        filterListView.addFooterView(footerView, null, false);
        filterListView.setAdapter(filterAdapter);
    }

    private View.OnClickListener footerButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.reset_butt:
                    if (filter.isChangeState()) {
                        filter.reset();
                        filterListView.invalidate();
                    } else {
                        organizeView();
                        filterListView.collapseGroup(0);
                    }
                    mFiltrUse = false;
                    break;
                case R.id.search_butt:
                    mFilterWhereClause = filter.getSQLWhereClause();
                    initLoadManager();
                    organizeView();
                    filterListView.collapseGroup(0);
                    mFiltrUse = true;
                    break;
            }
        }

        private void organizeView() {
            footerView.findViewById(R.id.sort_group).setVisibility(View.VISIBLE);
            footerView.findViewById(R.id.filter_button_bar).setVisibility(View.GONE);
        }
    };

     @Override
    public void onGroupCollapse(int groupPosition) {
        mExpandFiltr = false;
    }

    private void initLoadManager(){
        getSupportLoaderManager().restartLoader(0, null, this);
    }

///Use LoaderManager
    private Dialog mDialog;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                mContext.getString(R.string.dialog_select_data_message), false, false);
        return new SelectItemsByCategory(this, mCategoryID, mFilterWhereClause, mLocationId, mSortBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mListCategoriesAdapter.swapCursor(cursor);
        mDialog.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mListCategoriesAdapter.swapCursor(null);
    }
}