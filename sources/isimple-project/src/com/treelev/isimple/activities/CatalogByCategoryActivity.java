package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.FilterAdapter;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.ui.FilterItem;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.*;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogByCategoryActivity extends ListActivity implements RadioGroup.OnCheckedChangeListener,
        ActionBar.OnNavigationListener, ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupClickListener,
        ExpandableListView.OnChildClickListener {

    private final static String FIELD_TAG = "field_tag";
    public final static String FILTER_DATA_TAG = "filter_data";
    private List<Item> mItems;
    private List<Map<String, ?>> mUiItemList;
    private SimpleAdapter mListCategoriesAdapter;
    private ExpandableListView listView;
    private RadioGroup checkTypeRg;
    private View footerView;
    private Integer mCategoryID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_category_layout);
        createNavigation();
        mCategoryID = getIntent().getIntExtra(CatalogListActivity.CATEGORY_NAME_EXTRA_ID, -1);
        initDataListView(mCategoryID);
        initFilterListView(createFilterList(), mCategoryID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, CatalogListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int rgb) {
        switch (rgb) {
            case R.id.alphabet_sort:
                updateList(ProxyManager.SORT_NAME_AZ);
                break;
            case R.id.price_sort:
                updateList(ProxyManager.SORT_PRICE_UP);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.search, menu);
        SearchManager searcMenager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searcMenager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchResult.backActivity = CatalogByCategoryActivity.class;
                SearchResult.categoryID = mCategoryID;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
        mSearchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        View groupView = listView.getChildAt(groupPosition);
        groupView.findViewById(R.id.group_name).setVisibility(View.GONE);
        footerView.findViewById(R.id.sort_group).setVisibility(View.GONE);
        footerView.findViewById(R.id.filter_button_bar).setVisibility(View.VISIBLE);
        RelativeLayout categoryTypeLayout = (RelativeLayout) groupView.findViewById(R.id.category_type_view);
        categoryTypeLayout.setVisibility(View.VISIBLE);
        categoryTypeLayout.findViewById(R.id.red_wine_butt).setOnClickListener(categoryTypeClick);
        categoryTypeLayout.findViewById(R.id.white_wine_butt).setOnClickListener(categoryTypeClick);
        categoryTypeLayout.findViewById(R.id.pink_wine_butt).setOnClickListener(categoryTypeClick);
        checkTypeRg = (RadioGroup) categoryTypeLayout.findViewById(R.id.check_type_rg);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ошибка");
        builder.setMessage("Фильтрация недоступна");
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HashMap product = (HashMap) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, (String) product.get(Item.UI_TAG_ID));
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent filterDataIntent = new Intent(this, FilterActivity.class);
        filterDataIntent.putExtra(FILTER_DATA_TAG, childPosition);
        startActivity(filterDataIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    private List<FilterItem> createFilterList() {
        List<FilterItem> filterItems = new ArrayList<FilterItem>();
        filterItems.add(new FilterItem(FilterItem.ITEM_TYPE_TEXT, "Содержание сахара"));
        filterItems.add(new FilterItem(FilterItem.ITEM_TYPE_TEXT, "Регион"));
        filterItems.add(new FilterItem(FilterItem.ITEM_TYPE_TEXT, "Вкусовое сочетание"));
        filterItems.add(new FilterItem(FilterItem.ITEM_TYPE_PROGRESS));
        filterItems.add(new FilterItem(FilterItem.ITEM_TYPE_TEXT, "Год урожая"));
        return filterItems;
    }

    private void initDataListView(int categoryId) {
        ProxyManager mProxyManager = new ProxyManager(this);
        mItems = mProxyManager.getItemsByCategory(categoryId);
        mUiItemList = mProxyManager.convertItemsToUI(mItems, ProxyManager.SORT_NAME_AZ);
        mListCategoriesAdapter = new SimpleAdapter(this,
                mUiItemList,
                R.layout.catalog_item_layout,
                Item.getUITags(),
                new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_drink_type, R.id.item_volume, R.id.item_price});
        getListView().setAdapter(mListCategoriesAdapter);
    }

    private void initFilterListView(List<FilterItem> content, int categoryId) {
        BaseExpandableListAdapter filterAdapter = new FilterAdapter(this, getString(R.string.filtration_label), content);
        listView = (ExpandableListView) findViewById(R.id.filtration_view);
        listView.setOnGroupExpandListener(this);
        listView.setOnChildClickListener(this);
        if (categoryId != R.id.category_wine_butt) {
            listView.setOnGroupClickListener(this);
        }
        footerView = getLayoutInflater().inflate(R.layout.category_filtration_button_bar_layout, listView, false);
        ((RadioGroup) footerView.findViewById(R.id.sort_group)).setOnCheckedChangeListener(this);
        footerView.findViewById(R.id.reset_butt).setOnClickListener(resetButtonClick);
        listView.addFooterView(footerView, null, false);
        listView.setAdapter(filterAdapter);
    }

    private void updateList(int sortBy) {
        new SortTask(this, mItems).execute(sortBy);
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

    private View.OnClickListener categoryTypeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            checkTypeRg.check(getCheckIdByViewId(view.getId()));
        }

        private int getCheckIdByViewId(int viewId) {
            switch (viewId) {
                case R.id.red_wine_butt:
                    return R.id.check_red_wine;
                case R.id.white_wine_butt:
                    return R.id.check_white_wine;
                case R.id.pink_wine_butt:
                    return R.id.check_pink_wine;
                default:
                    return -1;
            }
        }
    };

    private View.OnClickListener resetButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            organizeView();
            listView.collapseGroup(0);
        }

        private void organizeView() {
            View groupView = listView.getChildAt(0);
            groupView.findViewById(R.id.group_name).setVisibility(View.VISIBLE);
            footerView.findViewById(R.id.sort_group).setVisibility(View.VISIBLE);
            footerView.findViewById(R.id.filter_button_bar).setVisibility(View.GONE);
            RelativeLayout categoryTypeLayout = (RelativeLayout) groupView.findViewById(R.id.category_type_view);
            categoryTypeLayout.setVisibility(View.GONE);
            categoryTypeLayout.findViewById(R.id.red_wine_butt).setOnClickListener(null);
            categoryTypeLayout.findViewById(R.id.white_wine_butt).setOnClickListener(null);
            categoryTypeLayout.findViewById(R.id.pink_wine_butt).setOnClickListener(null);
        }
    };

    private class SortTask extends AsyncTask<Integer, Void, List<Map<String, ?>>> {

        private Dialog mDialog;
        private Context context;
        private List<Item> mItems;
        private ProxyManager proxyManager;

        private SortTask(Context context, List<Item> items) {
            this.context = context;
            this.mItems = items;
            proxyManager = new ProxyManager(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mUiItemList.clear();
            mDialog = ProgressDialog.show(context, context.getString(R.string.dialog_search_title),
                    context.getString(R.string.dialog_sort_message), false, false);
        }

        @Override
        protected List<Map<String, ?>> doInBackground(Integer... sortParams) {
            return proxyManager.convertItemsToUI(mItems, sortParams[0]);
        }

        @Override
        protected void onPostExecute(List<Map<String, ?>> items) {
            super.onPostExecute(items);
            mUiItemList.addAll(items);
            mListCategoriesAdapter.notifyDataSetChanged();
            mDialog.dismiss();
        }
    }
}