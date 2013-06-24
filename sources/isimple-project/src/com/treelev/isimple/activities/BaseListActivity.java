package com.treelev.isimple.activities;

import android.app.Activity;
import android.view.Window;
import org.holoeverywhere.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;
import com.actionbarsherlock.app.ActionBar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.ListActivity;

public class BaseListActivity extends ListActivity implements ActionBar.OnNavigationListener {

    protected int mCurrentCategory;
    public final static String BARCODE = "barcode";
    private boolean useBarcodeScaner;
    private boolean backAfterBarcodeScaner;

    @Override
    protected void onResume(){
        super.onResume();
//        this.supportInvalidateOptionsMenu();
        if(useBarcodeScaner){
            if(backAfterBarcodeScaner){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String codeInfo;
        String typeCode;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IntentIntegrator.REQUEST_CODE:
                    typeCode = data.getStringExtra("SCAN_RESULT_FORMAT");
                    codeInfo = data.getStringExtra("SCAN_RESULT");
//                    codeInfo = "9319002010094";
                    checkBarcodeResult(codeInfo);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    public void setCurrentCategory(int currentCategory) {
        mCurrentCategory = currentCategory;

    }

    protected void createNavigationMenuBar() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Resources resources = getResources();
        String[] mainMenuLabelsArray = resources.getStringArray(R.array.main_menu_items);
        TypedArray typedIconsArray = resources.obtainTypedArray(R.array.main_menu_icons);
        Drawable[] iconLocation = getIconsList(typedIconsArray, mainMenuLabelsArray.length);
        organizeNavigationMenu(iconLocation, mainMenuLabelsArray);
    }

    protected void showAlertDialog(int iconId, String title, String message, String button) {
        Context dialogContext = new ContextThemeWrapper(this, R.style.Holo_AlertDialog_Light);
        AlertDialog.Builder adb = new AlertDialog.Builder(dialogContext);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setNeutralButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        adb.show();
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
                        showAlertDialog(0, null, getString(R.string.not_found_barcode), "OK");
                        useBarcodeScaner = false;
                    }
                }
            }
        }
    }

    private Intent getStartIntentByItemPosition(int itemPosition) {
        Class category = null;
        Intent intent = null;
        int flags = 0;
        switch (itemPosition) {
            case 0: //Catalog
                category = CatalogListActivity.class;
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
                break;
            case 1: //Shop
                category = ShopsFragmentActivity.class;
                break;
            case 2: //FavoritesActivity
                category = FavoritesActivity.class;
                break;
            case 3: //Shopping cart
                category = ShoppingCartActivity.class;
                break;
            case 4: //Scan Code
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.initiateScan();
                getSupportActionBar().setSelectedNavigationItem(mCurrentCategory);
                break;
            default:
                category = null;
        }
        if( !this.getClass().equals(category) && category != null){
            intent =  new Intent(this, category);
            if(flags != 0 && mCurrentCategory != itemPosition) {
                intent.setFlags(flags);
            }
        }
        return intent;
    }

    private Drawable[] getIconsList(TypedArray typedIconsArray, int navigationMenuBarLenght) {
        Drawable[] iconLocation = new Drawable[typedIconsArray.length()];
        for (int i = 0; i < navigationMenuBarLenght; ++i) {
            iconLocation[i] = typedIconsArray.getDrawable(i);
        }
        return iconLocation;
    }

    private void organizeNavigationMenu(Drawable[] iconLocation, String[] mainMenuLabelsArray) {
        NavigationListAdapter navigationAdapter = new NavigationListAdapter(this, iconLocation, mainMenuLabelsArray);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(navigationAdapter, this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
        getSupportActionBar().setSelectedNavigationItem(mCurrentCategory);
    }
}
