package com.treelev.isimple.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.CatalogItemCursorAdapter;
import com.treelev.isimple.cursorloaders.DeleteFavouriteItems;
import com.treelev.isimple.cursorloaders.SelectFavouriteItems;
import com.treelev.isimple.listener.SwipeDismissListViewTouchListener;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.ListView;

import java.util.ArrayList;

public class FavoritesActivity extends BaseListActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String FAVORITES = "favorites";

    private Cursor cItems;
    private CatalogItemCursorAdapter mListAdapter;
    private ProxyManager mProxyManager;
    private ArrayList<String> mDeleteItemsId;
    private ArrayList<String> mDeleteItemsIdCallBack;
    private ActionMode mActionMode;
    private Dialog mDialog;
    private Context mContext;

    private final int LOAD_FAVOURITE_ITEMS = 1;
    private final int DELETE_FAVOURITE_ITEMS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        setCurrentCategory(2);
        createNavigationMenuBar();
        ListView listView = getListView();
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//        listView.setMultiChoiceModeListener(multiChoiceModeListener);
        mContext = this;
        mDeleteItemsId = new ArrayList<String>();
        mListAdapter = new CatalogItemCursorAdapter(null, this, false, false);
        mListAdapter.setDeleteItemsId(mDeleteItemsId);
        getListView().setAdapter(mListAdapter);
        getSupportLoaderManager().restartLoader(LOAD_FAVOURITE_ITEMS, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean addFavorites = data.getBooleanExtra(ProductInfoActivity.CHANGE_FAVOURITE, false);
        if(addFavorites){
            getSupportLoaderManager().restartLoader(LOAD_FAVOURITE_ITEMS, null, this);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            startActivityForResult(startIntent, 0);
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        } else if(mListAdapter != null){
//            if(!mDeleteItemsId.contains(product.getString(0))){
//                mDeleteItemsId.add(product.getString(0));
//            } else {
//                mDeleteItemsId.remove(product.getString(0));
//            }
//            mListAdapter.notifyDataSetChanged();
        }
    }

    public void backCatalog(View view){
        getSupportActionBar().setSelectedNavigationItem(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mDialog = ProgressDialog.show(this, this.getString(R.string.dialog_title),
                this.getString(R.string.dialog_select_data_message), false, false);
        switch (i){
            case LOAD_FAVOURITE_ITEMS:
                return new SelectFavouriteItems(this);
            case DELETE_FAVOURITE_ITEMS:
                return new DeleteFavouriteItems(this, mDeleteItemsIdCallBack);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mListAdapter.swapCursor(cursor);
        updateActivity();
        mDialog.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mListAdapter.swapCursor(null);
    }


    private void updateActivity(){
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.not_favourite_items);
        if( getListView().getCount() > 0){
            layout.setVisibility(View.GONE);
         } else {
            layout.setVisibility(View.VISIBLE);
        }
        mDeleteItemsId.clear();
        mListAdapter.notifyDataSetChanged();
    }

    private void deleteSelectedItems(){
        mDeleteItemsIdCallBack = new ArrayList<String>(mDeleteItemsId);
        getSupportLoaderManager().restartLoader(DELETE_FAVOURITE_ITEMS, null, this);
    }

    private ListView.MultiChoiceModeListener multiChoiceModeListener = new ListView.MultiChoiceModeListener() {


        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            Cursor cursor = (Cursor)getListView().getAdapter().getItem(position);
            if(checked){
                mDeleteItemsId.add(cursor.getString(0));
            } else {
                mDeleteItemsId.remove(cursor.getString(0));
            }
            mListAdapter.notifyDataSetChanged();
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
            mActionMode = null;
            mDeleteItemsId.clear();
            if(mListAdapter != null) {
                mListAdapter.notifyDataSetChanged();
            }
        }

    };

    private ActionMode.Callback mActionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_mode_favourites, menu);
            setOnTouchListener(getListView());
            mListAdapter.enableDeleteMode();
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
//                    deleteSelectedItems();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            getListView().setOnTouchListener(null);
            mListAdapter.disableDeleteMode();
            mActionMode = null;
            mDeleteItemsId.clear();
            if(mListAdapter != null) {
                mListAdapter.notifyDataSetChanged();
            }
        }
    };

    private void setOnTouchListener(ListView listView){
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(android.widget.ListView listView, int[] reverseSortedPositions) {
                                Log.v("Delete items", String.valueOf(reverseSortedPositions[0]));
                                Cursor cursor = (Cursor) mListAdapter.getItem(reverseSortedPositions[0]);
                                Log.v("Delete items", cursor.getString(0));
                                mListAdapter.addDeleteItem(cursor.getString(0));
                                mListAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
    }
}
