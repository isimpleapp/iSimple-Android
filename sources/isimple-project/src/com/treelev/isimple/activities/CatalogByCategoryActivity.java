package com.treelev.isimple.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.SimpleExpandableListAdapter;

import java.util.*;

public class CatalogByCategoryActivity extends ListActivity implements RadioGroup.OnCheckedChangeListener, ActionBar.OnNavigationListener {

    private static final int PROGRESS_DLG_ID = 666;
    private final static String FIELD_TAG = "field_tag";
    private ProxyManager mProxyManager;
    private List<Item> mItems;
    private List<Map<String, ?>> mUiItemList;
    private SimpleAdapter mListCategoriesAdapter;
    private Context mContext;
    private int mCategoryId;
    View mHeaderView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_category_layout);
        createNavigation();
        mContext = this;
        mHeaderView = getLayoutInflater().inflate(R.layout.category_header_layout, getListView(), false);
        RadioGroup rd = (RadioGroup) mHeaderView.findViewById(R.id.sort_group);
        rd.check(R.id.alphabet_sort);
        rd.setOnCheckedChangeListener(this);
        mCategoryId = getIntent().getIntExtra(CatalogListActivity.CATEGORY_NAME_EXTRA_ID, -1);
        initListView(mHeaderView, mCategoryId);
        SimpleExpandableListAdapter filterAdapter = new SimpleExpandableListAdapter(this, createExpandableGroups(),
                R.layout.category_filtration_expandable_group_layout, new String[]{FIELD_TAG}, new int[]{R.id.group_name}, createExpandableContent(),
                R.layout.category_filtration_expandable_item_layout, new String[]{FIELD_TAG}, new int[]{R.id.item_content}
        );
        ((ExpandableListView) mHeaderView.findViewById(R.id.filtration_view)).setAdapter(filterAdapter);
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
        mContext = this;
        SearchView.OnQueryTextListener qyeryTextListner = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Search search = new Search(mContext);
                search.execute(query);
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        searchView.setOnQueryTextListener(qyeryTextListner);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HashMap product = (HashMap) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, (String) product.get(Item.UI_TAG_ID));
        startActivity(startIntent);
    }

    private void initListView(View headerView, int categoryId) {
        mProxyManager = new ProxyManager(this);
        mItems = mProxyManager.getItemsByCategory(categoryId);
        mUiItemList = mProxyManager.convertItemsToUI(mItems, ProxyManager.SORT_NAME_AZ);
        mListCategoriesAdapter = new SimpleAdapter(this,
                mUiItemList,
                R.layout.catalog_item_layout,
                Item.getUITags(),
                new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_drink_type, R.id.item_volume, R.id.item_price});
        getListView().addHeaderView(headerView, null, false);
        getListView().setAdapter(mListCategoriesAdapter);
    }

    private List<Map<String, ?>> createExpandableGroups() {
        List<Map<String, ?>> groupName = new ArrayList<Map<String, ?>>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put(FIELD_TAG, getString(R.string.filtration_group_name));
        groupName.add(item);
        return groupName;
    }

    private List<List<Map<String, ?>>> createExpandableContent() {
        List<List<Map<String, ?>>> content = new ArrayList<List<Map<String, ?>>>();
        List<Map<String, ?>> contentFilterMenu = new ArrayList<Map<String, ?>>();
        for (int j = 0; j < 7; ++j) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(FIELD_TAG, String.valueOf(j));
            contentFilterMenu.add(item);
        }
        content.add(contentFilterMenu);
        return content;
    }

    private void updateList(int sortBy) {
        mUiItemList.clear();
        mUiItemList.addAll(mProxyManager.convertItemsToUI(mItems, sortBy));
        mListCategoriesAdapter.notifyDataSetChanged();
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

    class Search extends AsyncTask<String, Void, Void> {

        private Dialog mDialog;
        private Context mContext;

        public Search(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_search_tittle),
                    mContext.getString(R.string.dialog_serch_message), false, false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            mProxyManager = new ProxyManager(mContext);
            mItems = mProxyManager.getSerchItemsByCategory(mCategoryId, strings[0]);
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            mUiItemList = mProxyManager.convertItemsToUI(mItems, ProxyManager.SORT_NAME_AZ);
            mListCategoriesAdapter = new SimpleAdapter(mContext,
                    mUiItemList,
                    R.layout.catalog_item_layout,
                    Item.getUITags(),
                    new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_drink_type, R.id.item_volume, R.id.item_price});
            getListView().addHeaderView(mHeaderView, null, false);
            getListView().setAdapter(mListCategoriesAdapter);
        }
    }

}