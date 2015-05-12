
package com.treelev.isimple.activities;

import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.CatalogItemCursorAdapter;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.cursorloaders.LoadBannerItems;
import com.treelev.isimple.domain.db.Offer;
import com.treelev.isimple.listener.SwipeDismissListViewTouchListener;
import com.treelev.isimple.utils.managers.ProxyManager;

public class BannerInfoActivity extends BaseListActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    
    public static final String OFFER_ID = "OFFER_ID";

    private long bannerId;
    private Offer offer;
    private CatalogItemCursorAdapter mListAdapter;
    private Dialog mDialog;
    private SwipeDismissListViewTouchListener mTouchListener;
    private ProxyManager mProxyManager;

    private final int LOAD_BANNER_ITEMS = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_info);
        createNavigationMenuBar();
        
        bannerId = getIntent().getLongExtra(OFFER_ID, -1);
        if (bannerId == -1) {
            throw new RuntimeException("You must pass banner id as intent argument");
        }
        mProxyManager = ProxyManager.getInstanse();
        offer = mProxyManager.getOfferById(bannerId);
        initListView();
        getSupportLoaderManager().restartLoader(LOAD_BANNER_ITEMS, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Analytics.screen_Favourites(this);
    }

    @Override
    protected void onResume() {
        if (mEventChangeDataBase) {
            getSupportLoaderManager().restartLoader(LOAD_BANNER_ITEMS, null, this);
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
        return super.onCreateOptionsMenu(menu);
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

    public void createNavigationMenuBar() {
        createDrawableMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        startActivityForResult(startIntent, 0);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
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
            case LOAD_BANNER_ITEMS:
                return new LoadBannerItems(this, offer.getItemsList());
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
        mListAdapter.notifyDataSetChanged();
    }

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

}
