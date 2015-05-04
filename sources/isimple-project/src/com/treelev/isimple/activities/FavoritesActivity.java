
package com.treelev.isimple.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.RelativeLayout;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.CatalogItemCursorAdapter;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.cursorloaders.DeleteFavouriteItems;
import com.treelev.isimple.cursorloaders.SelectFavouriteItems;
import com.treelev.isimple.listener.SwipeDismissListViewTouchListener;
import com.treelev.isimple.utils.managers.ProxyManager;
import com.treelev.isimple.utils.observer.ObserverDataChanged;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends BaseListActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String FAVORITES = "favorites";

    private Cursor cItems;
    private CatalogItemCursorAdapter mListAdapter;
    private ProxyManager mProxyManager;
    private ActionMode mActionMode;
    private Dialog mDialog;
    private SwipeDismissListViewTouchListener mTouchListener;
    private Context mContext;
    private ArrayList<String> mDeleteItemsId;

    private final int LOAD_FAVOURITE_ITEMS = 1;
    private final int DELETE_FAVOURITE_ITEMS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        setCurrentCategory(2);
        createNavigationMenuBar();
        ListView listView = getListView();
        mDeleteItemsId = new ArrayList<String>();
        initListView();
        getSupportLoaderManager().restartLoader(LOAD_FAVOURITE_ITEMS, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Analytics.screen_Favourites(this);
    }

    @Override
    protected void onResume() {
        if (mEventChangeDataBase) {
            getSupportLoaderManager().restartLoader(LOAD_FAVOURITE_ITEMS, null, this);
            mEventChangeDataBase = false;
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    mActionMode = startActionMode(mActionCallbackBeforeV11);
                } else {
                    mActionMode = startActionMode(mActionCallbackAfterV11);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    public void createNavigationMenuBar() {
        createDrawableMenu();
        getSupportActionBar().setIcon(R.drawable.menu_ico_fav);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor) l.getAdapter().getItem(position);
        if (mActionMode == null) {
            Intent startIntent = new Intent(this, ProductInfoActivity.class);
            startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
            startIntent.putExtra(FAVORITES, true);
            startActivityForResult(startIntent, 0);
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        } else if (mListAdapter != null) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                if (!mListAdapter.containsDeleteItemID(product.getString(0))) {
                    mListAdapter.addDeleteItem(product.getString(0));
                } else {
                    mListAdapter.removeDeleteItemsID(product.getString(0));
                }
                mListAdapter.notifyDataSetChanged();
            }
        }
    }

    public void backCatalog(View view) {
        Intent intent = getStartIntentByItemPosition(0);
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mDialog = ProgressDialog.show(this, this.getString(R.string.dialog_title),
                this.getString(R.string.dialog_select_data_message), false, false);
        switch (i) {
            case LOAD_FAVOURITE_ITEMS:
                return new SelectFavouriteItems(this);
            case DELETE_FAVOURITE_ITEMS:
                List<String> deleteItems = mListAdapter.getDeleteItemsId();
                if (deleteItems.size() > 0) {
                    ObserverDataChanged.getInstant().sendEvent();
                }
                return new DeleteFavouriteItems(this, deleteItems);
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

    private void updateActivity() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.not_favourite_items);
        if (getListView().getCount() > 0) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
        }
        mListAdapter.notifyDataSetChanged();
    }

    private void deleteSelectedItems() {
        getSupportLoaderManager().restartLoader(DELETE_FAVOURITE_ITEMS, null, this);
    }

    private ActionMode.Callback mActionCallbackAfterV11 = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_mode_favourites, menu);
            mTouchListener.setEnabled(true);
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
                    deleteSelectedItems();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mTouchListener.setEnabled(false);
            mListAdapter.disableDeleteMode();
            mActionMode = null;
            if (mListAdapter != null) {
                mListAdapter.notifyDataSetChanged();
            }
        }
    };

    private void initListView() {
        ListView listView = getListView();
        mListAdapter = new CatalogItemCursorAdapter(null, this, false, true);
        mListAdapter.setOnCancelDismiss(new CatalogItemCursorAdapter.OnCancelDismis() {
            @Override
            public void cancelDeleteItem(int position) {
                mTouchListener.removePrepareDeletePosition(position);
            }
        });
        listView.setAdapter(mListAdapter);
        mTouchListener =
                new SwipeDismissListViewTouchListener(
                        getListView(),
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(android.widget.ListView listView,
                                    int[] reverseSortedPositions) {
                                Cursor cursor = (Cursor) mListAdapter
                                        .getItem(reverseSortedPositions[0]);
                                mListAdapter.addDeleteItem(cursor.getString(0));
                                mListAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(mTouchListener);
        mTouchListener.setEnabled(false);
    }

    private ActionMode.Callback mActionCallbackBeforeV11 = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_mode_favourites, menu);
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
            mListAdapter.disableDeleteMode();
            if (mListAdapter != null) {
                mListAdapter.notifyDataSetChanged();
            }
        }
    };
}
