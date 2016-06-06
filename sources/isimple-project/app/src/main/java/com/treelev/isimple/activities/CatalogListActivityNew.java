
package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.BannersPagerAdapter;
import com.treelev.isimple.adapters.CatalogItemAdapter;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.data.OfferDAO;
import com.treelev.isimple.domain.db.Offer;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.fragments.BannerFragment.ImageLoaderProvider;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.LocationTrackingManager;
import com.treelev.isimple.utils.managers.ProxyManager;
import com.treelev.isimple.views.TwoWayView;

import java.util.List;

public class CatalogListActivityNew extends BaseActivity implements OnItemClickListener,
        ImageLoaderProvider {

    private static final String TAG = CatalogListActivityNew.class.getSimpleName();

    public final static String CATEGORY_ID = "category_id";
    private View darkView;
    private final static int NAVIGATE_CATEGORY_ID = 0;
    public final static String DRINK_ID = "drink_id";
    public final static String FILTER_WHERE_CLAUSE = "filter_where_clauses";
    private SearchView mSearchView;

    private TwoWayView wineTwoWayView;
    private TwoWayView spiritsTwoWayView;
    private TwoWayView sparklingTwoWayView;
    private TwoWayView portoHeresTwoWayView;
    private TwoWayView sakeTwoWayView;
    private TwoWayView waterTwoWayView;

    private CatalogItemAdapter wineCatalogItemAdapter;
    private CatalogItemAdapter spiritsCatalogItemAdapter;
    private CatalogItemAdapter sparklingCatalogItemAdapter;
    private CatalogItemAdapter portoHeresCatalogItemAdapter;
    private CatalogItemAdapter sakeCatalogItemAdapter;
    private CatalogItemAdapter waterCatalogItemAdapter;

    private ScrollView scrollView;
    private ViewPager bannerPager;
    private ImageLoader bannersImageLoader;
    private DisplayImageOptions bannersImageLoaderOptions;
    private final Handler handler = new Handler();
    private List<Offer> offersList;

    @Override
    protected void onCreate(Bundle sSavedInstanceState) {
        super.onCreate(sSavedInstanceState);

        // hook location
        LocationTrackingManager.getInstante(this).getCurrentLocation(this);

        setContentView(R.layout.catalog_list_layout_new);
        setCurrentCategory(NAVIGATE_CATEGORY_ID);
        createDrawableMenu();
        darkView = findViewById(R.id.dark_view);
        darkView.setVisibility(View.GONE);
        darkView.setOnClickListener(null);

        scrollView = (ScrollView) findViewById(R.id.catalog_scroll_view);

        wineTwoWayView = (TwoWayView) findViewById(R.id.catalog_wine_two_way_view);
        spiritsTwoWayView = (TwoWayView) findViewById(R.id.catalog_spirits_two_way_view);
        sparklingTwoWayView = (TwoWayView) findViewById(R.id.catalog_sparkling_two_way_view);
        portoHeresTwoWayView = (TwoWayView) findViewById(R.id.catalog_porto_heres_two_way_view);
        sakeTwoWayView = (TwoWayView) findViewById(R.id.catalog_sake_two_way_view);
        waterTwoWayView = (TwoWayView) findViewById(R.id.catalog_water_two_way_view);

        wineCatalogItemAdapter = new CatalogItemAdapter(DrinkCategory.WINE.ordinal(), this, null, getSupportLoaderManager(), ProxyManager.SORT_DEFAULT);
        spiritsCatalogItemAdapter = new CatalogItemAdapter(DrinkCategory.SPIRITS.ordinal(), this, null, getSupportLoaderManager(), ProxyManager.SORT_DEFAULT);
        sparklingCatalogItemAdapter = new CatalogItemAdapter(DrinkCategory.SPARKLING.ordinal(), this, null, getSupportLoaderManager(), ProxyManager.SORT_DEFAULT);
        portoHeresCatalogItemAdapter = new CatalogItemAdapter(DrinkCategory.PORTO.ordinal(), this, null, getSupportLoaderManager(), ProxyManager.SORT_DEFAULT);
        sakeCatalogItemAdapter = new CatalogItemAdapter(DrinkCategory.SAKE.ordinal(), this, null, getSupportLoaderManager(), ProxyManager.SORT_DEFAULT);
        waterCatalogItemAdapter = new CatalogItemAdapter(DrinkCategory.WATER.ordinal(), this, null, getSupportLoaderManager(), ProxyManager.SORT_DEFAULT);

        wineTwoWayView.setAdapter(wineCatalogItemAdapter);
        spiritsTwoWayView.setAdapter(spiritsCatalogItemAdapter);
        sparklingTwoWayView.setAdapter(sparklingCatalogItemAdapter);
        portoHeresTwoWayView.setAdapter(portoHeresCatalogItemAdapter);
        sakeTwoWayView.setAdapter(sakeCatalogItemAdapter);
        waterTwoWayView.setAdapter(waterCatalogItemAdapter);

        wineTwoWayView.setOnItemClickListener(this);
        spiritsTwoWayView.setOnItemClickListener(this);
        sparklingTwoWayView.setOnItemClickListener(this);
        portoHeresTwoWayView.setOnItemClickListener(this);
        sakeTwoWayView.setOnItemClickListener(this);
        waterTwoWayView.setOnItemClickListener(this);

        bannersImageLoader = Utils.getImageLoader(getApplicationContext());
        bannersImageLoaderOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.bottle_list_image_default)
                .showImageOnLoading(R.drawable.bottle_list_image_default)
                .showImageForEmptyUri(R.drawable.bottle_list_image_default)
                .showImageOnFail(R.drawable.bottle_list_image_default).cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(300)).resetViewBeforeLoading(false).build();
        bannerPager = (ViewPager) findViewById(R.id.banners);
        fixBannerPagerHeightToFitBanner();
        bannerPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handler.removeCallbacks(switchBannerRunnable);
                        return true;
                    case MotionEvent.ACTION_UP:
                        handler.postDelayed(switchBannerRunnable, 4000);
                        break;
                }
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        offersList = new OfferDAO(getApplicationContext()).getOffersForViewPager();
        BannersPagerAdapter bannersAdapter = new BannersPagerAdapter(getSupportFragmentManager(), offersList);
        bannerPager.setAdapter(bannersAdapter);
        handler.postDelayed(switchBannerRunnable, 4000);

        final ImageView iv = new ImageView(this);
        iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    new Handler().postDelayed(openDrawerRunnable(), 200);
                }
            }
        });

        iv.setImageResource(R.drawable.ic_menu_pink);
        iv.setBackgroundResource(R.drawable.selector_default);
        iv.setPadding(25, 25, 25, 25);

        getSupportActionBar().setIcon(R.drawable.ic_menu_pink);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setCustomView(iv);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("");
    }

    private Runnable openDrawerRunnable() {
        return new Runnable() {

            @Override
            public void run() {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        };
    }

    private void switchBanner() {
        int nextIndex = bannerPager.getCurrentItem() + 1;
        if (nextIndex == offersList.size()) {
            nextIndex = 0;
        }
        bannerPager.setCurrentItem(nextIndex, true);
    }

    private Runnable switchBannerRunnable = new Runnable() {

        @Override
        public void run() {
            if (!bannerPager.isPressed()) {
                switchBanner();
            }
            handler.postDelayed(switchBannerRunnable, 4000);
        }
    };

    public void removeHandlerCallbacks() {
        handler.removeCallbacks(switchBannerRunnable);
    }

    public void restoreHandlerCallbacks() {
        handler.postDelayed(switchBannerRunnable, 4000);
    }

    private void fixBannerPagerHeightToFitBanner() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = 600 * width / 1200; // 1200x600 is banner size
        LayoutParams lp = bannerPager.getLayoutParams();
        lp.height = height;
        bannerPager.setLayoutParams(lp);
    }

    @Override
    protected void createDrawableMenu() {
        super.createDrawableMenu();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Analytics.screen_Catalog(this);
    }

    @Override
    protected void onResume() {
        if (mEventChangeDataBase) {
            mEventChangeDataBase = false;
        }
        wineCatalogItemAdapter.reload();
        spiritsCatalogItemAdapter.reload();
        sparklingCatalogItemAdapter.reload();
        portoHeresCatalogItemAdapter.reload();
        sakeCatalogItemAdapter.reload();
        waterCatalogItemAdapter.reload();
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        initSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onClickCategoryButt(View v) {
        Log.d(TAG, "onClickCategoryButt");
        Intent startIntent = new Intent(getApplicationContext(), CatalogByCategoryActivity.class);
        Integer category = DrinkCategory.getItemCategoryByButtonId(v.getId());
        startIntent.putExtra(CATEGORY_ID, category);
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private void initSearchView(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem mItemSearch = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mItemSearch);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    darkView.setVisibility(View.VISIBLE);
                    darkView.getBackground().setAlpha(150);
                    mSearchView.setQuery(mSearchView.getQuery(), false);
                } else {
                    darkView.setVisibility(View.GONE);
                    mItemSearch.collapseActionView();
                    mSearchView.setQuery("", false);
                }
            }
        });
        MenuItemCompat.setOnActionExpandListener(mItemSearch, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                darkView.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchResultActivity.categoryID = null;
                SearchResultActivity.locationId = null;
                return query.trim().length() < LENGTH_SEARCH_QUERY;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor product = (Cursor) parent.getAdapter().getItem(position);
        Intent startIntent;
        int itemCountIndex = product.getColumnIndex("count");
        if (product.getInt(itemCountIndex) > 1) {
            int itemDrinkIdIndex = product.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_ID);
            startIntent = new Intent(this, CatalogSubCategoryTree.class);
            startIntent.putExtra(DRINK_ID, product.getString(itemDrinkIdIndex));
            startActivity(startIntent);
        } else {
            startIntent = new Intent(this, ProductInfoActivity.class);
            startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG,
                    product.getString(0));
            startActivityForResult(startIntent, 0);
        }
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    @Override
    public ImageLoader getInitializedImageLoader() {
        return bannersImageLoader;
    }

    @Override
    public DisplayImageOptions getImageLoaderOptions() {
        return bannersImageLoaderOptions;
    }

}
