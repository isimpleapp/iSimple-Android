package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.CatalogItemCursorAdapter;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.apache.http.util.ByteArrayBuffer;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class CatalogListActivity extends BaseListActivity {

    public final static String CATEGORY_ID = "category_id";
    private View darkView;
    private RelativeLayout myLayout;
    private ProxyManager mProxyManager;
    private View mHeader;
    private final static int NAVIGATE_CATEGORY_ID = 0;

    @Override
    protected void onCreate(Bundle sSavedInstanceState) {
        super.onCreate(sSavedInstanceState);
        setContentView(R.layout.catalog_list_layout);
        setCurrentCategory(NAVIGATE_CATEGORY_ID);
        createNavigationMenuBar();
        darkView = findViewById(R.id.dark_view);
        darkView.setVisibility(View.GONE);
        darkView.setOnClickListener(null);
        new SelectDataRandomTask(this).execute();
    }

    @Override
    protected void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView listView = getListView();
        if (mHeader != null) {
            listView.removeHeaderView(mHeader);
        }
        mHeader = getLayoutInflater().inflate(R.layout.catalog_list_header_view, listView, false);
        listView.addHeaderView(mHeader, null, false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor) l.getAdapter().getItem(position);
        Intent startIntent;
        if (product.getInt(14) > 1) {
            startIntent = new Intent(this, CatalogSubCategory.class);
            CatalogSubCategory.categoryID = null;
            CatalogSubCategory.backActivity = CatalogListActivity.class;
            startIntent.putExtra(CatalogByCategoryActivity.DRINK_ID, product.getString(12));
        } else {
            startIntent = new Intent(this, ProductInfoActivity.class);
            startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        }
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
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
                if (query.trim().length() != 0) {
                    SearchResultActivity.categoryID = null;
                    SearchResultActivity.backActivity = CatalogListActivity.class;
                    return false;
                } else
                    return true;
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

    public void onClickCategoryButt(View v) {
        Intent startIntent = new Intent(getApplicationContext(), CatalogByCategoryActivity.class);
        Integer category = DrinkCategory.getItemCategoryByButtonId(v.getId());
        startIntent.putExtra(CATEGORY_ID, category);
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private ProxyManager getProxyManager() {
        if (mProxyManager == null) {
            mProxyManager = new ProxyManager(this);
        }
        return mProxyManager;
    }

    private class SelectDataRandomTask extends AsyncTask<Void, Void, Cursor> {

        private Dialog mDialog;
        private Context mContext;

        private SelectDataRandomTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), false, false);
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            return getProxyManager().getFeaturedMainItems();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            Cursor cItems = cursor;
            startManagingCursor(cItems);
            SimpleCursorAdapter mListCategoriesAdapter = new CatalogItemCursorAdapter(cItems, CatalogListActivity.this, true, false);
            getListView().setAdapter(mListCategoriesAdapter);
            mDialog.dismiss();
        }
    }

    private static class ImageBinder implements SimpleAdapter.ViewBinder {

        private Context context;

        private ImageBinder(Context context) {
            this.context = context;
        }

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            boolean result = false;
            if (view.getId() == R.id.item_image) {
                Bitmap bitmap;
                File tempFile = null;
                try {
                    tempFile = new File("/sdcard/temp.jpg");
                    URL imageUrl = new URL((String) data);
                    URLConnection conn = imageUrl.openConnection();
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(50);
                    int current;
                    while ((current = bis.read()) != -1) {
                        byteArrayBuffer.append((byte) current);
                    }
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(byteArrayBuffer.toByteArray());
                    fos.close();
                    ((ImageView) view).setImageBitmap(BitmapFactory.decodeFile(tempFile.getPath()));
                } catch (Exception e) {
                    e.printStackTrace();
                    ((ImageView) view).setImageDrawable(context.getResources().getDrawable(R.drawable.filter_label_arrow_down));
                } finally {
                    if (tempFile != null) {
                        tempFile.delete();
                    }
                }
                result = true;
            }
            return result;
        }
    }
}