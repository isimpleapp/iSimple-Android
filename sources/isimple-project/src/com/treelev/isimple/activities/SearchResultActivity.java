package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultActivity extends ListActivity implements RadioGroup.OnCheckedChangeListener,
        ActionBar.OnNavigationListener {

    public static Integer categoryID;
    public static Class backActivity;

    private List<Item> mItems;
    private List<Map<String, ?>> mUiItemList;
    private SimpleAdapter mListCategoriesAdapter;
    private ProxyManager mProxyManager;
    private String mQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        createNavigation();
        RadioGroup rg = (RadioGroup) findViewById(R.id.sort_group);
        rg.setOnCheckedChangeListener(this);
        mProxyManager = new ProxyManager(this);
        handledIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent newIntent) {
        setIntent(newIntent);
        finish();
        startActivity(getIntent());
//        handledIntent(newIntent);
    }

    void handledIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Search search = new Search(this, categoryID);
            search.execute(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.search, menu);
        SearchManager searcMenager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searcMenager.getSearchableInfo(getComponentName()));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() != 0) {
                    return false;
                }
                return true;
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

    void back() {
        Intent intent = new Intent(this, backActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(CatalogListActivity.CATEGORY_NAME_EXTRA_ID, categoryID);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HashMap product = (HashMap) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, (String) product.get(Item.UI_TAG_ID));
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private void updateList(int sortBy) {
        new SortTask(this, mItems).execute(sortBy);
    }

    private void createNavigation() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Context context = getSupportActionBar().getThemedContext();
        String[] menuItemText = getResources().getStringArray(R.array.main_menu_items);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.main_menu_icons);
        Drawable[] menuItemIcon = new Drawable[typedArray.length()];
        for (int i = 0; i < menuItemText.length; ++i) {
            menuItemIcon[i] = typedArray.getDrawable(i);
        }
        NavigationListAdapter navigationAdapter = new NavigationListAdapter(this, menuItemIcon, menuItemText);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(navigationAdapter, this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }



    class Search extends AsyncTask<String, Void, List<Item>> {

        private Dialog mDialog;
        private Context mContext;
        private ProxyManager mProxyManager;
        private Integer mCategoryId;

        public Search(Context context, Integer categoryId) {
            mContext = context;
            mCategoryId = categoryId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_search_title),
                    mContext.getString(R.string.dialog_search_message), false, false);
        }

        @Override
        protected List<Item> doInBackground(String... strings) {
            mProxyManager = new ProxyManager(mContext);
            return mProxyManager.getSearchItemsByCategory(mCategoryId, strings[0]);
        }

        @Override
        protected void onPostExecute(List<Item> result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            mItems = result;
            mProxyManager = new ProxyManager(mContext);
            mUiItemList = mProxyManager.convertItemsToUI(mItems, ProxyManager.SORT_NAME_AZ);
            mListCategoriesAdapter = new SimpleAdapter(mContext,
                    mUiItemList,
                    R.layout.catalog_item_layout,
                    Item.getUITags(),
                    new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_volume, R.id.item_price});
            ListView listView = getListView();
            getListView().setAdapter(mListCategoriesAdapter);
            if (getListView().getCount() == 0) {
                Toast.makeText(mContext, mContext.getString(R.string.message_not_found), Toast.LENGTH_LONG).show();
            }
        }
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
}