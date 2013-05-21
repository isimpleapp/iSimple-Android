package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ItemCursorAdapter;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.apache.http.util.ByteArrayBuffer;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class CatalogListActivity extends ListActivity implements ActionBar.OnNavigationListener {

    public final static String CATEGORY_NAME_EXTRA_ID = "category_name";
    private View darkView;
    private RelativeLayout myLayout;
    private SimpleCursorAdapter mListCategoriesAdapter;
    private Cursor cItems;
    private ProxyManager mProxyManager;

    @Override
    protected void onCreate(Bundle sSavedInstanceState) {
        super.onCreate(sSavedInstanceState);
        setContentView(R.layout.catalog_list_layout);
        createNavigation();
        ListView listView = getListView();
        darkView = findViewById(R.id.dark_view);
        darkView.setVisibility(View.GONE);
        darkView.setOnClickListener(null);
        View headerView = getLayoutInflater().inflate(R.layout.catalog_list_header_view, listView, false);
        listView.addHeaderView(headerView, null, false);
        new SelectDataRandomTask(this).execute();
//        ProxyManager proxyManager = new ProxyManager(this);
//        SimpleAdapter simpleAdapter = new SimpleAdapter(this, proxyManager.getRandomItems(), R.layout.catalog_item_layout,
//                Item.getUITags(), new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_volume, R.id.item_price,
//                R.id.product_category});
//        listView.setAdapter(simpleAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        HashMap product = (HashMap) l.getAdapter().getItem(position);
//        Intent startIntent = new Intent(this, ProductInfoActivity.class);
//        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, (String) product.get(Item.UI_TAG_ID));
//        startActivity(startIntent);
//        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor) l.getAdapter().getItem(position);
        Intent startIntent;
        if( product.getInt(8) > 1){
            startIntent = new Intent(this, CatalogSubCategory.class);
            CatalogSubCategory.categoryID = null;
            CatalogSubCategory.backActivity = CatalogListActivity.class;
            startIntent.putExtra(CatalogByCategoryActivity.DRINK_ID, product.getString(9));
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

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    public void onClickCategoryButt(View v) {
        Intent startIntent = new Intent(getApplicationContext(), CatalogByCategoryActivity.class);
        startIntent.putExtra(CATEGORY_NAME_EXTRA_ID, v.getId());
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
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
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
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
            return getProxyManager().getRandomItems();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cItems = cursor;
            startManagingCursor(cItems);
            mListCategoriesAdapter = new ItemCursorAdapter(cItems, CatalogListActivity.this, true);
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