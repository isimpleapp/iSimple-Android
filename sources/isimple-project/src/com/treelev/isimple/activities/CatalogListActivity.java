package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.apache.http.util.ByteArrayBuffer;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ListView;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class CatalogListActivity extends ListActivity implements ActionBar.OnNavigationListener {

    public final static String CATEGORY_NAME_EXTRA_ID = "category_name";

    SearchView mSearchView;
    Context mContext;

    @Override
    protected void onCreate(Bundle sSavedInstanceState) {
        super.onCreate(sSavedInstanceState);
        setContentView(R.layout.catalog_list_layout);
        createNavigation();
        ListView listView = getListView();
        View headerView = getLayoutInflater().inflate(R.layout.catalog_list_header_view, listView, false);
        listView.addHeaderView(headerView, null, false);
        ProxyManager proxyManager = new ProxyManager(this);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, proxyManager.getRandomItems(), R.layout.catalog_item_layout,
                Item.getUITags(), new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_drink_type,
                R.id.item_volume, R.id.item_price});
        //simpleAdapter.setViewBinder(new ImageBinder(this));
        listView.setAdapter(simpleAdapter);
        mContext = this;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HashMap product = (HashMap) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, (String) product.get(Item.UI_TAG_ID));
        startActivity(startIntent);
    }

    public void onClickCategoryButt(View v) {
        Intent startIntent = new Intent(getApplicationContext(), CatalogByCategoryActivity.class);
        startIntent.putExtra(CATEGORY_NAME_EXTRA_ID, v.getId());
        startActivity(startIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.search, menu);
        SearchManager searcMenager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);

        mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searcMenager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.onActionViewCollapsed();
                mSearchView.setQuery("", false);
                mSearchView.clearFocus();
                SearchResult.categoryID = null;
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
                    ((ImageView) view).setImageDrawable(context.getResources().getDrawable(R.drawable.image_not_found));
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

    private void createNavigation() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        Context context = getSupportActionBar().getThemedContext();
        String[] menuItemText = getResources().getStringArray(R.array.main_menu_items);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.main_menu_icons);
        Drawable[] menuItemIcon = new Drawable[typedArray.length()];
        for(int i = 0; i < menuItemText.length; ++i) {
            menuItemIcon[i] = typedArray.getDrawable(i);
        }
        NavigationListAdapter navigationAdapter = new NavigationListAdapter(this, menuItemIcon, menuItemText);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(navigationAdapter, this);
    }
}