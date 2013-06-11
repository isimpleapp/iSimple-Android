package com.treelev.isimple.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ItemCursorAdapter;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

public class FavoritesActivity extends BaseListActivity {

    public static final String FAVORITES = "favorites";

    private Cursor cItems;
    private ItemCursorAdapter mListAdapter;
    private ProxyManager mProxyManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        setCurrentCategory(2);
        createNavigationMenuBar();
        new SelectByFavorites(this).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    @Override
    public void createNavigationMenuBar(){
        super.createNavigationMenuBar();
        getSupportActionBar().setIcon(R.drawable.menu_ico_fav);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        startIntent.putExtra(FAVORITES, true);
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private ProxyManager getProxyManager() {
        if (mProxyManager == null) {
            mProxyManager = new ProxyManager(this);
        }
        return mProxyManager;
    }

    public void backCatalog(View view){
        getSupportActionBar().setSelectedNavigationItem(0);
    }

    private class SelectByFavorites extends AsyncTask<String, Void, Cursor> {

        private Dialog mDialog;
        private Context mContext;

        private SelectByFavorites(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), false, false);
        }

        @Override
        protected Cursor doInBackground(String... params) {
            return getProxyManager().getFavouriteItems();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            RelativeLayout layout = (RelativeLayout)findViewById(R.id.not_favourite_items);
            if(cursor.getCount() > 0){
                cItems = cursor;
                startManagingCursor(cItems);
                mListAdapter = new ItemCursorAdapter(cItems, FavoritesActivity.this, false, false);
                getListView().setAdapter(mListAdapter);
                layout.setVisibility(View.GONE);
            } else {
                layout.setVisibility(View.VISIBLE);
                TextView textView = (TextView) findViewById(R.id.favourite_empty);
            }
            mDialog.dismiss();
        }
    }
}
