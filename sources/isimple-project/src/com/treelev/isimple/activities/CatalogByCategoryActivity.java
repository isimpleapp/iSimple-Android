package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.FilterAdapter;
import com.treelev.isimple.adapters.ItemCursorAdapter;
import com.treelev.isimple.animation.AnimationWithMargins;
import com.treelev.isimple.domain.ui.filter.FilterItem;
import com.treelev.isimple.filter.*;
import com.treelev.isimple.filter.Filter;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.ListView;

public class CatalogByCategoryActivity extends BaseListActivity implements RadioGroup.OnCheckedChangeListener,
        ExpandableListView.OnGroupExpandListener, ExpandableListView.OnChildClickListener {

    private final static String FIELD_TAG = "field_tag";
    public final static String FILTER_DATA_TAG = "filter_data";
    public final static String DRINK_ID = "drink_id";
    private Cursor cItems;
    private SimpleCursorAdapter mListCategoriesAdapter;
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
    private boolean mExpandFiltr = false;
    private ProxyManager mProxyManager;
    private com.treelev.isimple.filter.Filter filter;
    private static final int ANIMATION_DURATION_IN_MILLIS = 500;
    public final static String EXTRA_RESULT_CHECKED = "isChecked";
    public final static String EXTRA_CHILD_POSITION = "position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_category_layout);
        setCurrentCategory(0);
        createNavigationMenuBar();
        darkView = findViewById(R.id.category_dark_view);
        darkView.setVisibility(View.GONE);
        darkView.setOnClickListener(null);
        mCategoryID = getIntent().getIntExtra(CatalogListActivity.CATEGORY_ID, -1);
        filter = initFilter();
        initFilterListView();
        new SelectDataTask(this).execute(mCategoryID);
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
                SearchResultActivity.backActivity = CatalogByCategoryActivity.class;
                SearchResultActivity.categoryID = mCategoryID;
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
        if (product.getInt(8) > 1) {
            startIntent = new Intent(this, CatalogSubCategory.class);
            CatalogSubCategory.categoryID = mCategoryID;
            CatalogSubCategory.backActivity = CatalogByCategoryActivity.class;
            startIntent.putExtra(DRINK_ID, product.getString(9));
        } else {
            startIntent = new Intent(this, ProductInfoActivity.class);
            startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        }

        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProxyManager != null) {
            mProxyManager.release();
            mProxyManager = null;
        }
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
        switch (mCategoryID) {
            case R.id.category_wine_butt:
                return new WineFilter(this);
            case R.id.category_spirits_butt:
                return new SpiritsFilter(this);
            case R.id.category_sparkling_butt:
                return new SparklingFilter(this);
            case R.id.category_sake_butt:
                return new SakeFilter(this);
            case R.id.category_porto_heres_butt:
                return new PortoHeresFilter(this);
            case R.id.category_water_butt:
                return new WaterFilter(this);
            default:
                return null;
        }
    }

    private ProxyManager getProxyManager() {
        if (mProxyManager == null) {
            mProxyManager = new ProxyManager(this);
        }
        return mProxyManager;
    }

    private void backOrCollapse() {
        if (mExpandFiltr) {
            resetButtonClick.onClick(null);
            mExpandFiltr = false;
        } else {
            Intent intent = new Intent(this, CatalogListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
        }
    }

    private void initFilterListView() {
        BaseExpandableListAdapter filterAdapter = new FilterAdapter(this, filter);
        filterListView = (ExpandableListView) findViewById(R.id.filtration_view);
        filterListView.setOnGroupExpandListener(this);
        filterListView.setOnChildClickListener(this);
        filterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        footerView = getLayoutInflater().inflate(R.layout.category_filtration_button_bar_layout, filterListView, false);
        ((RadioGroup) footerView.findViewById(R.id.sort_group)).setOnCheckedChangeListener(this);
        footerView.findViewById(R.id.reset_butt).setOnClickListener(resetButtonClick);
        filterListView.addFooterView(footerView, null, false);
        filterListView.setAdapter(filterAdapter);
    }

    private View.OnClickListener resetButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            organizeView();
            filterListView.collapseGroup(0);
        }

        private void organizeView() {
//            View groupView = filterListView.getChildAt(0);
            //((ViewGroup) groupView).removeView(groupView.findViewById(R.id.category_type_view));
//            groupView.setVisibility(View.VISIBLE);
            footerView.findViewById(R.id.sort_group).setVisibility(View.VISIBLE);
            footerView.findViewById(R.id.filter_button_bar).setVisibility(View.GONE);
        }
    };

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
            return getProxyManager().getItemsByCategory(mCategoryID, params[0]);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cItems = cursor;
            startManagingCursor(cItems);
            mListCategoriesAdapter = new ItemCursorAdapter(cItems, CatalogByCategoryActivity.this, true);
            getListView().setAdapter(mListCategoriesAdapter);
            mDialog.dismiss();
        }
    }

    private class SelectDataTask extends AsyncTask<Integer, Void, Cursor> {

        private Dialog mDialog;
        private Context mContext;

        private SelectDataTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), false, false);
        }

        @Override
        protected Cursor doInBackground(Integer... params) {
            return getProxyManager().getItemsByCategory(params[0], ProxyManager.SORT_NAME_AZ);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cItems = cursor;
            startManagingCursor(cItems);
            mListCategoriesAdapter = new ItemCursorAdapter(cItems, CatalogByCategoryActivity.this, true);
            getListView().setAdapter(mListCategoriesAdapter);
            mDialog.dismiss();
        }
    }
}