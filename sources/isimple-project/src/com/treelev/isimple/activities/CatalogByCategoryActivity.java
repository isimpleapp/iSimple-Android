package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.FilterAdapter;
import com.treelev.isimple.adapters.ItemCursorAdapter;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.filter.*;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.ListView;

public class CatalogByCategoryActivity extends ListActivity implements RadioGroup.OnCheckedChangeListener,
        ActionBar.OnNavigationListener, ExpandableListView.OnGroupExpandListener, ExpandableListView.OnChildClickListener {

    private final static String FIELD_TAG = "field_tag";
    public final static String FILTER_DATA_TAG = "filter_data";
    public final static String DRINK_ID = "drink_id";
    private Cursor cItems;
    private SimpleCursorAdapter mListCategoriesAdapter;
    private ExpandableListView filterListView;
    private View footerView;
    private View darkView;
    private Integer mCategoryID;
    private boolean mExpandFiltr = false;
    private ProxyManager mProxyManager;
    private com.treelev.isimple.filter.Filter filter;

    public final static int RESULT_REQUEST_CODE = 1;
    public final static String EXTRA_RESULT_CHECKED = "isChecked";
    public final static String EXTRA_CHILD_POSITION = "position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_category_layout);
        createNavigation();
        darkView = findViewById(R.id.category_dark_view);
        darkView.setVisibility(View.GONE);
        darkView.setOnClickListener(null);
        mCategoryID = getIntent().getIntExtra(CatalogListActivity.CATEGORY_NAME_EXTRA_ID, -1);
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
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        LinearLayout groupView = (LinearLayout) filterListView.getChildAt(groupPosition);
        groupView.findViewById(R.id.group_name).setVisibility(View.GONE);
        groupView.addView(filter.getFilterHeaderLayout(),
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics())));
        footerView.findViewById(R.id.sort_group).setVisibility(View.GONE);
        footerView.findViewById(R.id.filter_button_bar).setVisibility(View.VISIBLE);
        /*RelativeLayout categoryTypeLayout = (RelativeLayout) groupView.findViewById(R.id.category_type_view);
        categoryTypeLayout.setVisibility(View.VISIBLE);
        categoryTypeLayout.findViewById(R.id.red_wine_butt).setOnClickListener(categoryTypeClick);
        categoryTypeLayout.findViewById(R.id.white_wine_butt).setOnClickListener(categoryTypeClick);
        categoryTypeLayout.findViewById(R.id.pink_wine_butt).setOnClickListener(categoryTypeClick);
        CheckBox checkBoxRedWine = (CheckBox) categoryTypeLayout.findViewById(R.id.red_wine_check);
        CheckBox checkBoxWhiteWine = (CheckBox) categoryTypeLayout.findViewById(R.id.white_wine_check);
        CheckBox checkBoxPinkWine = (CheckBox) categoryTypeLayout.findViewById(R.id.pink_wine_check);
        filterTypeCheckBoxArray = new CheckBox[]{checkBoxRedWine, checkBoxWhiteWine, checkBoxPinkWine};*/

        mExpandFiltr = true;
//        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_down_anim);
//        groupView.startAnimation(anim);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        filter.getFilterContent().get(childPosition).process(mCategoryID, childPosition);
        return false;
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
        if( product.getInt(8) > 1){
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
        if (requestCode == RESULT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                boolean isItemChecked = data.getBooleanExtra(EXTRA_RESULT_CHECKED, false);
                int childPosition = data.getIntExtra(EXTRA_CHILD_POSITION, -1);
                //TODO доделать изменение цвета текста при выборе
                /*filterListView.getI
                TextView filterItemText = (TextView) filterLayout.findViewById(R.id.item_content);
                int color;
                if (isItemChecked) {
                    color = Color.BLACK;
                } else {
                    color = getResources().getColor(R.color.filter_item_text_color);
                }
                filterItemText.setTextColor(color);*/
            }
        }
    }

    private com.treelev.isimple.filter.Filter initFilter() {
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
        footerView = getLayoutInflater().inflate(R.layout.category_filtration_button_bar_layout, filterListView, false);
        ((RadioGroup) footerView.findViewById(R.id.sort_group)).setOnCheckedChangeListener(this);
        footerView.findViewById(R.id.reset_butt).setOnClickListener(resetButtonClick);
        filterListView.addFooterView(footerView, null, false);
        filterListView.setAdapter(filterAdapter);
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
    }

    private View.OnClickListener resetButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Animation anim = null;
            organizeView();
            //resetFilterCheckBox();
//            anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_anim);
//            listView.startAnimation(anim);
            filterListView.collapseGroup(0);
        }

        private void organizeView() {
            View groupView = filterListView.getChildAt(0);
            ((ViewGroup) groupView).removeView(groupView.findViewById(R.id.category_type_view));
            groupView.findViewById(R.id.group_name).setVisibility(View.VISIBLE);
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