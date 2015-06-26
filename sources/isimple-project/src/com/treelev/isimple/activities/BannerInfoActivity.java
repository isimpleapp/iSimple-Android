
package com.treelev.isimple.activities;

import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.CatalogItemCursorAdapter;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.cursorloaders.LoadBannerItems;
import com.treelev.isimple.domain.db.Offer;
import com.treelev.isimple.listener.SwipeDismissListViewTouchListener;
import com.treelev.isimple.utils.Utils;
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
    private ImageView bannerImageView;
    private WebView bannerDescView;
    private ImageLoader bannersImageLoader;
    private DisplayImageOptions bannersImageLoaderOptions;

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

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headerView = inflater.inflate(R.layout.banner_list_header, getListView(), false);
        bannerImageView = (ImageView) headerView.findViewById(R.id.banner_image);
        bannerImageView.setLayoutParams(getBannerLayoutParams());
        bannerDescView = (WebView) headerView.findViewById(R.id.offer_desc);
        if (!TextUtils.isEmpty(offer.getDescription())) {
            bannerDescView.loadDataWithBaseURL(null, offer.getDescription(), "text/html", "utf-8", null);
        } else {
            bannerDescView.setVisibility(View.GONE);
        }
        getListView().addHeaderView(headerView, null, false);
        initListView();

        getSupportLoaderManager().restartLoader(LOAD_BANNER_ITEMS, null, this);

        if (!TextUtils.isEmpty(offer.getImage1200())) {
            bannersImageLoader = Utils.getImageLoader(getApplicationContext());
            bannersImageLoaderOptions = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.drawable.bottle_list_image_default)
                    .showImageOnLoading(R.drawable.bottle_list_image_default)
                    .showImageForEmptyUri(R.drawable.bottle_list_image_default)
                    .showImageOnFail(R.drawable.bottle_list_image_default).cacheInMemory(true)
                    .cacheOnDisk(true)
                    .displayer(new FadeInBitmapDisplayer(300)).resetViewBeforeLoading(false)
                    .build();
            bannersImageLoader.displayImage(offer.getImage1200(), bannerImageView,
                    bannersImageLoaderOptions);
        }
    }

    private LayoutParams getBannerLayoutParams() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = 600 * width / 1200; // 1200x600 is banner size
        LayoutParams lp = bannerImageView.getLayoutParams();
        lp.height = height;
        return lp;
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
        if (!offer.getItemsList().isEmpty()) {
            mDialog = ProgressDialog.show(this, this.getString(R.string.dialog_title),
                    this.getString(R.string.dialog_select_data_message), false, false);
            switch (i) {
                case LOAD_BANNER_ITEMS:
                    return new LoadBannerItems(this, offer.getItemsList());
                default:
                    return null;
            }
        } else {
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
