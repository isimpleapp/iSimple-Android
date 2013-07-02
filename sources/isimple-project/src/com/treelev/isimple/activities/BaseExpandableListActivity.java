package com.treelev.isimple.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import com.actionbarsherlock.app.ActionBar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.ExpandableListActivity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.ExpandableListView;

public class BaseExpandableListActivity extends ExpandableListActivity implements ActionBar.OnNavigationListener {

    protected int mCurrentCategory;
    public final static String BARCODE = "barcode";
    private boolean useBarcodeScaner;
    private boolean backAfterBarcodeScaner;

    public void setCurrentCategory(int currentCategory) {
        mCurrentCategory = currentCategory;
    }

    @Override
    protected void onResume(){
        super.onResume();
///        this.supportInvalidateOptionsMenu();
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

    protected void createNavigationMenuBar() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Resources resources = getResources();
        String[] mainMenuLabelsArray = resources.getStringArray(R.array.main_menu_items);
        TypedArray typedIconsArray = resources.obtainTypedArray(R.array.main_menu_icons);
        Drawable[] iconLocation = getIconsList(typedIconsArray, mainMenuLabelsArray.length);
        organizeNavigationMenu(iconLocation, mainMenuLabelsArray);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String codeInfo;
        String typeCode;
        if (resultCode == android.app.Activity.RESULT_OK) {
            switch (requestCode) {
                case IntentIntegrator.REQUEST_CODE:

                    typeCode = data.getStringExtra("SCAN_RESULT_FORMAT");
                    codeInfo = data.getStringExtra("SCAN_RESULT");
                    checkBarcodeResult(codeInfo);
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

    protected void expandAllGroup(){
        ExpandableListView expandView = getExpandableListView();
        int countGroup = expandView.getExpandableListAdapter().getGroupCount();
// because in the first application Parent invisible and empty. see implemantation SelectSectionsItems, AbsItemTreeCursorAdapter
        for(int position = 1; position < countGroup; ++position){
            expandView.expandGroup(position);
        }
    }

    protected  void disableOnGroupClick(){
        getExpandableListView().setOnGroupClickListener( new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }
}
