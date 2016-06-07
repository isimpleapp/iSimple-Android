package com.treelev.isimple.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.EnterCatalogUpdateUrlDialogFragment.CatalogUpdateUrlChangeListener;
import com.treelev.isimple.adapters.NavigationDrawerAdapter;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.service.SyncProgressListener;
import com.treelev.isimple.service.SyncServcie;
import com.treelev.isimple.utils.Constants;
import com.treelev.isimple.utils.LogUtils;
import com.treelev.isimple.utils.managers.LocationTrackingManager;
import com.treelev.isimple.utils.managers.ProxyManager;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;
import com.treelev.isimple.utils.observer.Observer;
import com.treelev.isimple.utils.observer.ObserverDataChanged;


public class BaseActivity extends AppCompatActivity implements ActionBar.OnNavigationListener,
        Observer, CatalogUpdateUrlChangeListener, SyncProgressListener {

    public final static String BARCODE = "barcode";

    private int mCurrentCategory;
    public boolean useBarcodeScaner;
    private boolean backAfterBarcodeScaner;
    protected final int LENGTH_SEARCH_QUERY = 3;
    protected boolean mEventChangeDataBase;

    protected DrawerLayout drawerLayout;

    private Dialog mDialog;
    public SyncStatusReceiver syncStatusReceiver;
    public SyncFinishedReceiver syncFinishedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObserverDataChanged.getInstant().addObserver(this);

        syncStatusReceiver = new SyncStatusReceiver();
        syncFinishedReceiver = new SyncFinishedReceiver();

        if (!SyncServcie.startSyncIfNeeded(this, this)) {
            SyncServcie.startOffersSyncIfNeeded(this, this);
        }

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((ISimpleApp) getApplication()).incRefActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((ISimpleApp) getApplication()).decRefActivity();
        if (((ISimpleApp) getApplication()).getCountRefActivity() == 0) {
            LocationTrackingManager.getInstante(this).stopLocationListener();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObserverDataChanged.getInstant().removeObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        this.supportInvalidateOptionsMenu();
        if (useBarcodeScaner) {
            if (backAfterBarcodeScaner) {
                finish();
                startActivity(getIntent());
            }
            backAfterBarcodeScaner = true;
        }

    }

    public void setCurrentCategory(int currentCategory) {
        mCurrentCategory = currentCategory;
    }

    public int getmCurrentCategory() {
        return mCurrentCategory;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Intent newIntent = getStartIntentByItemPosition(itemPosition);
        if (newIntent != null && mCurrentCategory != itemPosition) {
            startActivity(newIntent);
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
//            getSupportActionBar().setSelectedNavigationItem(mCurrentCategory);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String codeInfo;
        if (resultCode == android.app.Activity.RESULT_OK) {
            switch (requestCode) {
                case IntentIntegrator.REQUEST_CODE:

                    codeInfo = data.getStringExtra("SCAN_RESULT");
                    if (codeInfo != null) {
                        checkBarcodeResult(codeInfo);
                    }
                    break;
            }
        }
    }

    protected void createDrawableMenu() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        initDrawerMenuList();
    }

    private void initDrawerMenuList() {
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(this);
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View drawerHeader = inflater.inflate(R.layout.drawer_header, drawerList, false);
        drawerList.addHeaderView(drawerHeader);
        drawerList.setAdapter(adapter);
        drawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawers();
                Intent intent = getStartIntentByItemPosition(position - 1);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });

    }

    private Intent getStartIntentByItemPosition(int itemPosition) {
        Class category = null;
        Intent intent = null;
        int flags = 0;
        switch (itemPosition) {
            case 0: //Catalog
                category = CatalogListActivityNew.class;
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
                break;
            case 1: //Shop
                category = ShopsFragmentActivity.class;
                break;
            case 2: //FavoritesActivity
                category = FavoritesActivity.class;
                break;
            case 3: //ShoppingCartActivity
                category = ShoppingCartActivity.class;
                break;
            case 4: //Scan Code
                Analytics.screen_Scan(this);

                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.initiateScan();
                break;
            case 5: //About
                showAbout();
                break;
            case 6:
                // showCatalogUpdateLink();
                SyncServcie.startSync(this, this);
            case 7:
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + Constants.ISIMPLE_PHONE));
                break;
            default:
                category = null;
        }
        if (!this.getClass().equals(category) && category != null) {
            intent = new Intent(this, category);
            if (flags != 0 && mCurrentCategory != itemPosition) {
                intent.setFlags(flags);
            }
        }
        return intent;
    }

    private void showAbout() {
        String version = "";
        try {
            version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            String dateUpdate = SharedPreferencesManager.getDateUpdate(this);
            String dateCatalogUpdate = SharedPreferencesManager.getDateCatalogUpdate(this);
            String datePriceUpdate = SharedPreferencesManager.getDatePriceUpdate(this);
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(getString(R.string.title_about_info));
            String aboutInfo = String.format(getString(R.string.about_info), version, dateCatalogUpdate, datePriceUpdate, dateUpdate);
            adb.setMessage(Html.fromHtml(aboutInfo));
            adb.setNeutralButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            adb.show();
        }
    }

    private void showCatalogUpdateLink() {
        EnterCatalogUpdateUrlDialogFragment changeCatalogUpdateUrl = new EnterCatalogUpdateUrlDialogFragment();
        changeCatalogUpdateUrl.show(getFragmentManager(), "sometag");
    }

    private void checkBarcodeResult(String code) {
        ProxyManager proxyManager = ProxyManager.getInstanse();
        int count = proxyManager.getCountBarcode(code);
        useBarcodeScaner = true;
        if (count > 1) {
            Intent intent = new Intent(this, CatalogSubCategory.class);
            intent.putExtra(BARCODE, code);
            startActivity(intent);
        } else {
            if (count == 1) {
                Intent intent = new Intent(this, ProductInfoActivity.class);
                intent.putExtra(BARCODE, code);
                startActivity(intent);
            } else {
                int countFromItemDeprecatedTable = proxyManager.getCountBarcodeInDeprecatedTable(code);
                if (countFromItemDeprecatedTable > 1) {
                    Intent intent = new Intent(this, CatalogSubCategory.class);
                    intent.putExtra(BARCODE, code);
                    startActivity(intent);
                } else {
                    if (countFromItemDeprecatedTable == 1) {
                        Intent intent = new Intent(this, ProductInfoActivity.class);
                        intent.putExtra(BARCODE, code);
                        startActivity(intent);
                    } else {
                        showAlertDialog(null, getString(R.string.not_found_barcode));
                        useBarcodeScaner = false;
                    }
                }
            }
        }
    }

    protected void showAlertDialog(String title, String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setNeutralButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        adb.show();
    }

    @Override
    public void dataChanged() {
        mEventChangeDataBase = true;
    }

    @Override
    public void onCatalogUpdateUrlChanged(String url) {
        SharedPreferencesManager.putUpdateFileUrl(url);
    }

    @Override
    public void startListeningForProgress() {
        LogUtils.i("Test log", "startListeningForProgress");
        showDialog();
        LocalBroadcastManager.getInstance(this).registerReceiver(syncStatusReceiver,
                new IntentFilter(Constants.INTENT_ACTION_SYNC_STATE_UPDATE));
        registerReceiver(syncFinishedReceiver,
                new IntentFilter(Constants.INTENT_ACTION_SYNC_FINISHED));
    }

    private class SyncStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateDialog(intent.getStringExtra(Constants.INTENT_EXTRA_SYNC_STATE));
        }

    }

    private class SyncFinishedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i("Test log", "SyncFinishedReceiver onReceive");
            hideDialog();
            LocalBroadcastManager.getInstance(context).unregisterReceiver(syncStatusReceiver);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(syncFinishedReceiver);

            if (!intent.getBooleanExtra(Constants.INTENT_ACTION_SYNC_SUCCESSFULL, true)) {
                Toast.makeText(context, R.string.sync_error_update_index_error, Toast.LENGTH_LONG).show();
            }
        }

    }

    public void updateDialog(String message) {
        if (mDialog != null) {
            ((ProgressDialog) mDialog).setMessage(message);
        }
    }

    public void showDialog() {
        LogUtils.i("Test log", "mDialog = " + mDialog);
        if (mDialog == null) {
            mDialog = ProgressDialog.show(BaseActivity.this,
                    getString(R.string.update_data_notify_label),
                    getString(R.string.update_data_wait_label), false, false);
        }
    }

    public void hideDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void initSearchMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        final View darkView = findViewById(R.id.dark_view);
        darkView.setVisibility(View.GONE);
        darkView.setOnClickListener(null);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem mItemSearch = menu.findItem(R.id.menu_search);
        final SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(mItemSearch);
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
}
