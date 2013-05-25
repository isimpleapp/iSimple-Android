package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ItemCursorAdapter;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.Toast;

public class SearchResultActivity extends BaseListActivity implements RadioGroup.OnCheckedChangeListener {

    public static Integer categoryID;
    public static Class backActivity;
    private Cursor cItems;
    private SimpleCursorAdapter mListCategoriesAdapter;
    private String mQuery;
    private View darkView;
    private ProxyManager mProxyManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        setCurrentCategory(0);
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
    }

    @Override
    protected void onNewIntent(Intent newIntent) {
        setIntent(newIntent);
        finish();
        startActivity(getIntent());
//        handledIntent(newIntent);
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
//                darkView.setVisibility(View.VISIBLE);
//                darkView.getBackground().setAlpha(150);
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
        int sortBy = 0;
        switch (rgb) {
            case R.id.alphabet_sort:
                sortBy = ProxyManager.SORT_NAME_AZ;
                break;
            case R.id.price_sort:
                sortBy = ProxyManager.SORT_PRICE_UP;
                break;
        }
        if(cItems.getCount() == 0) {
            Toast.makeText(this, this.getString(R.string.message_not_found), Toast.LENGTH_LONG).show();
        } else {
            new SortTask(this, categoryID, sortBy).execute(mQuery);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor) l.getAdapter().getItem(position);
        Intent startIntent;
        if( product.getInt(8) > 1){
            startIntent = new Intent(this, CatalogSubCategory.class);
            CatalogSubCategory.categoryID = categoryID;
            CatalogSubCategory.backActivity = SearchResultActivity.class;
            startIntent.putExtra(CatalogByCategoryActivity.DRINK_ID, product.getString(9));
        } else {
            startIntent = new Intent(this, ProductInfoActivity.class);
            startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        }

        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private void handledIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String mDrinkId = intent.getStringExtra(CatalogByCategoryActivity.DRINK_ID);
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            Search search = new Search(this, categoryID);
            search.execute(mQuery);
        }
    }

    private void back() {
        Intent intent = new Intent(this, backActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(CatalogListActivity.CATEGORY_NAME_EXTRA_ID, categoryID);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    private void updateList(int sortBy) {

    }

    private ProxyManager getProxyManager() {
        if (mProxyManager == null) {
            mProxyManager = new ProxyManager(this);
        }
        return mProxyManager;
    }

    private class Search extends AsyncTask<String, Void, Cursor> {

        private Dialog mDialog;
        private Context mContext;
        private ProxyManager mProxyManager;
        private Integer mCategoryId;

        private Search(Context context, Integer categoryId) {
            mContext = context;
            mCategoryId = categoryId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), false, false);
        }

        @Override
        protected Cursor doInBackground(String... params) {
            return getProxyManager().getSearchItemsByCategory(mCategoryId, params[0], ProxyManager.SORT_NAME_AZ);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cItems = cursor;
            startManagingCursor(cItems);
            mListCategoriesAdapter = new ItemCursorAdapter(cItems, SearchResultActivity.this, true);
            getListView().setAdapter(mListCategoriesAdapter);
            mDialog.dismiss();
            if(cItems.getCount() == 0) {
                Toast.makeText(mContext, mContext.getString(R.string.message_not_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class SortTask extends AsyncTask<String, Void, Cursor> {

        private Dialog mDialog;
        private Context mContext;
        private Integer mCategoryId;
        private int mSortBy;
        private ProxyManager proxyManager;


        private SortTask(Context context,Integer categoryId, int sortBy ) {
            mContext = context;
            mCategoryId = categoryId;
            mSortBy = sortBy;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), false, false);
            stopManagingCursor(cItems);
            cItems.close();
        }

        @Override
        protected Cursor doInBackground(String... params) {
            return getProxyManager().getSearchItemsByCategory(mCategoryId, params[0], mSortBy);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cItems = cursor;
            startManagingCursor(cItems);
            mListCategoriesAdapter = new ItemCursorAdapter(cItems, SearchResultActivity.this, true);
            getListView().setAdapter(mListCategoriesAdapter);
            mDialog.dismiss();
        }
    }

}