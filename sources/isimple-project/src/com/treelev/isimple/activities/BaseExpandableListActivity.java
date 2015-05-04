
package com.treelev.isimple.activities;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ExpandableListActivity;
import org.holoeverywhere.widget.ExpandableListView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.EnterCatalogUpdateUrlDialogFragment.CatalogUpdateUrlChangeListener;
import com.treelev.isimple.adapters.NavigationDrawerAdapter;
import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.utils.managers.LocationTrackingManager;
import com.treelev.isimple.utils.managers.ProxyManager;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;
import com.treelev.isimple.utils.observer.Observer;
import com.treelev.isimple.utils.observer.ObserverDataChanged;

public class BaseExpandableListActivity extends ExpandableListActivity implements
        ActionBar.OnNavigationListener,
        Observer, CatalogUpdateUrlChangeListener {

    protected boolean mEventChangeDataBase;
    protected final int LENGTH_SEARCH_QUERY = 3;
    protected int mCurrentCategory;

    public final static String BARCODE = "barcode";
    private boolean useBarcodeScaner;
    private boolean backAfterBarcodeScaner;
    
    protected DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObserverDataChanged.getInstant().addObserver(this);
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
            LocationTrackingManager.getInstante().stopLocationListener();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObserverDataChanged.getInstant().removeObserver(this);
    }

    public void setCurrentCategory(int currentCategory) {
        mCurrentCategory = currentCategory;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // / this.supportInvalidateOptionsMenu();
        if (useBarcodeScaner) {
            if (backAfterBarcodeScaner) {
                finish();
                startActivity(getIntent());
            }
            backAfterBarcodeScaner = true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Intent newIntent = getStartIntentByItemPosition(itemPosition);
        if (newIntent != null && mCurrentCategory != itemPosition) {
            startActivity(newIntent);
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
            getSupportActionBar().setSelectedNavigationItem(mCurrentCategory);
        }
        return false;
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
            case 0: // Catalog
                category = CatalogListActivity.class;
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
                break;
            case 1: // Shop
                category = ShopsFragmentActivity.class;
                break;
            case 2: // FavoritesActivity
                category = FavoritesActivity.class;
                break;
            case 3: // ShoppingCartActivity
                category = ShoppingCartActivity.class;
                break;
            case 4: // Scan Code
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.initiateScan();
                break;
            case 5: // About
                showAbout();
                break;
            case 6:
                showCatalogUpdateLink();
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

    private void showCatalogUpdateLink() {
        EnterCatalogUpdateUrlDialogFragment changeCatalogUpdateUrl = new EnterCatalogUpdateUrlDialogFragment();
        changeCatalogUpdateUrl.show(getFragmentManager(), "sometag");
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
            ;
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(getString(R.string.title_about_info));
            String aboutInfo = String.format(getString(R.string.about_info), version,
                    dateCatalogUpdate, datePriceUpdate, dateUpdate);
            adb.setMessage(Html.fromHtml(aboutInfo));
            adb.setNeutralButton(getString(R.string.dialog_button_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            adb.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String codeInfo;
        String typeCode;
        if (resultCode == android.app.Activity.RESULT_OK) {
            switch (requestCode) {
                case IntentIntegrator.REQUEST_CODE:

                    typeCode = data.getStringExtra("SCAN_RESULT_FORMAT");
                    codeInfo = data.getStringExtra("SCAN_RESULT");
                    if (codeInfo != null) {
                        checkBarcodeResult(codeInfo);
                    }
                    break;
            }
        }
    }

    private void checkBarcodeResult(String code) {
        ProxyManager proxyManager = new ProxyManager(this);
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
                int countFromItemDeprecatedTable = proxyManager
                        .getCountBarcodeInDeprecatedTable(code);
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
                        showAlertDialog(0, null, getString(R.string.not_found_barcode));
                        useBarcodeScaner = false;
                    }
                }
            }
        }
    }

    protected void showAlertDialog(int iconId, String title, String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        adb.show();
    }

    protected void expandAllGroup() {
        ExpandableListView expandView = getExpandableListView();
        int countGroup = expandView.getExpandableListAdapter().getGroupCount();
        // because in the first application Parent invisible and empty. see
        // implemantation SelectSectionsItems, AbsItemTreeCursorAdapter
        for (int position = 1; position < countGroup; ++position) {
            expandView.expandGroup(position);
        }
    }

    protected void disableOnGroupClick() {
        getExpandableListView().setOnGroupClickListener(
                new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v,
                            int groupPosition, long id) {
                        return true;
                    }
                });
    }

    @Override
    public void dataChanged() {
        mEventChangeDataBase = true;
    }

    @Override
    public void onCatalogUpdateUrlChanged(String url) {
        SharedPreferencesManager.putUpdateFileUrl(url);
    }
}
