package com.treelev.isimple.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.FilterAdapter;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.domain.db.Item;
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
        ActionBar.OnNavigationListener, ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupClickListener {

    private static final int PROGRESS_DLG_ID = 1;
    private final static String FIELD_TAG = "field_tag";
    private List<Item> mItems;
    private List<Map<String, ?>> mUiItemList;
    private SimpleAdapter mListCategoriesAdapter;
    private int mCategoryId;
    private ExpandableListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_category_layout);
        createNavigation();
        mCategoryId = getIntent().getIntExtra(CatalogListActivity.CATEGORY_NAME_EXTRA_ID, -1);
        initDataListView(mCategoryId);
        ((RadioGroup) findViewById(R.id.sort_group)).setOnCheckedChangeListener(this);

        List<String> content = new ArrayList<String>();
        content.add("Содержание сахара");
        content.add("Регион");
        content.add("Вкусовое сочетание");
        content.add("");
        content.add("Год урожая");
        content.add("");

        initFilterListView(content, mCategoryId);
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
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setIconifiedByDefault(false);
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Search search = new Search(CatalogByCategoryActivity.this);
                search.execute(query);
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
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
        groupView.findViewById(R.id.filter_type_butt).setVisibility(View.VISIBLE);
        findViewById(R.id.sort_group).setVisibility(View.GONE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HashMap product = (HashMap) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, (String) product.get(Item.UI_TAG_ID));
        startActivity(startIntent);
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

    private void initFilterListView(List<String> content, int categoryId) {
        BaseExpandableListAdapter filterAdapter = new FilterAdapter(this, "Фильтрация", content);
        listView = (ExpandableListView) findViewById(R.id.filtration_view);
        listView.setOnGroupExpandListener(this);
        if (categoryId != R.id.category_wine_butt) {
            listView.setOnGroupClickListener(this);
        }
        listView.setAdapter(filterAdapter);
    }

    private void updateList(int sortBy) {
        new SortTask(this, mItems).execute(sortBy);
    }

    private void createNavigation() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
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

    private class Search extends AsyncTask<String, Void, Void> {

        private Dialog mDialog;
        private Context context;
        private ProxyManager proxyManager;

        Search(Context context) {
            this.context = context;
            this.proxyManager = new ProxyManager(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(context, context.getString(R.string.dialog_search_title),
                    context.getString(R.string.dialog_search_message), false, false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            mItems = proxyManager.getSearchItemsByCategory(mCategoryId, strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            mUiItemList = proxyManager.convertItemsToUI(mItems, ProxyManager.SORT_NAME_AZ);
            mListCategoriesAdapter = new SimpleAdapter(context,
                    mUiItemList,
                    R.layout.catalog_item_layout,
                    Item.getUITags(),
                    new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_drink_type, R.id.item_volume, R.id.item_price});
            getListView().setAdapter(mListCategoriesAdapter);
        }
    }

}