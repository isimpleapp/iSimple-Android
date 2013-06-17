package com.treelev.isimple.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.CatalogItemCursorAdapter;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;

public class FavoritesActivity extends BaseListActivity {

    public static final String FAVORITES = "favorites";

    private Cursor cItems;
    private CatalogItemCursorAdapter mListAdapter;
    private ProxyManager mProxyManager;
    private Context mContext;
    private ArrayList<String> mDleteItemsId;
    private ArrayList<View> mDeleteItemView;
    private ActionMode mActionMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        setCurrentCategory(2);
        createNavigationMenuBar();
        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(multiChoiceModeListener);
        mContext = this;
        mDleteItemsId = new ArrayList<String>();
        mDeleteItemView = new ArrayList<View>();
        new SelectByFavorites(mContext).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateActivity();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.action_mode_favourites, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
                return true;
            case R.id.discard_favorites:
                mActionMode = startActionMode(mActionCallback);
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
        if(mActionMode == null){
            Intent startIntent = new Intent(this, ProductInfoActivity.class);
            startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
            startIntent.putExtra(FAVORITES, true);
            startActivity(startIntent);
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        } else {
//            View viewItem = getListView().getChildAt(position);

            ImageView dicsacrdContent = (ImageView) getListView().getChildAt(position).findViewById(R.id.item_image_delete);
            if(dicsacrdContent.getVisibility() == View.GONE){
                mDleteItemsId.add(product.getString(0));
                mDeleteItemView.add(dicsacrdContent);
                dicsacrdContent.setVisibility(View.VISIBLE);
                v.setSelected(true);
            } else {
                mDleteItemsId.remove(product.getString(0));
                mDeleteItemView.remove(dicsacrdContent);
                dicsacrdContent.setVisibility(View.GONE);
                v.setSelected(false);
            }
        }
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
            cItems = cursor;
            startManagingCursor(cItems);
            mListAdapter = new CatalogItemCursorAdapter(cItems, FavoritesActivity.this, false, false);
            getListView().setAdapter(mListAdapter);
            updateActivity();
            mDialog.dismiss();

        }
    }


    private void updateActivity(){
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.not_favourite_items);
        if( getListView().getCount() > 0){
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
            TextView textView = (TextView) findViewById(R.id.favourite_empty);
        }
    }

    private void deleteSelectedItems(){
        stopManagingCursor(cItems);
        getProxyManager().delFavourites(mDleteItemsId);
        getProxyManager().setFavouriteItemTable(mDleteItemsId, false);
        new SelectByFavorites(mContext).execute();
    }

    private void clearDeleteItemView(){
        for(View itemView: mDeleteItemView){
            if(itemView != null){
                itemView.setVisibility(View.GONE);
            }
        }
    }

    private ListView.MultiChoiceModeListener multiChoiceModeListener = new ListView.MultiChoiceModeListener() {


        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            Cursor cursor = (Cursor)getListView().getAdapter().getItem(position);
            ImageView dicsacrdContent = (ImageView) getListView().getChildAt(position).findViewById(R.id.item_image_delete);
            if(checked){
                mDleteItemsId.add(cursor.getString(0));
                mDeleteItemView.add(dicsacrdContent);
                dicsacrdContent.setVisibility(View.VISIBLE);
            } else {
                mDleteItemsId.remove(cursor.getString(0));
                mDeleteItemView.remove(dicsacrdContent);
                dicsacrdContent.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_mode_favourites, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.discard_favorites:
                    deleteSelectedItems();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearDeleteItemView();
        }

    };

    private ActionMode.Callback mActionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_mode_favourites, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.discard_favorites:
                    deleteSelectedItems();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            clearDeleteItemView();
        }
    };
}
