package com.treelev.isimple.activities;

import android.app.Activity;
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
import org.holoeverywhere.app.AlertDialog;

public class BaseListActivity extends ListActivity implements ActionBar.OnNavigationListener {

    private int mCurrentCategory;
    public final static String BARCODE = "barcode";

    public void setCurrentCategory(int currentCategory) {
        mCurrentCategory = currentCategory;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String codeInfo = "test";
        String typeCode = "test";
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IntentIntegrator.REQUEST_CODE:

                    typeCode = data.getStringExtra("SCAN_RESULT_FORMAT");
                    codeInfo = data.getStringExtra("SCAN_RESULT");
//                    codeInfo = "4610000613306";
                    checkBarcodeResult(codeInfo);
                    break;
            }
        }
    }

    private void checkBarcodeResult(String code) {
        ProxyManager proxyManager = new ProxyManager(this);
        int count = proxyManager.getCountBarcode(code);

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
                showAlertDialog(0,null,"По данному штрихкоду ничего не найдено.","OK");
            }
        }
    }

    protected void showAlertDialog(int iconId, String title, String message, String button) {
        Context dialogContext = new ContextThemeWrapper(this, R.style.Holo_AlertDialog_Light);
        AlertDialog.Builder adb = new AlertDialog.Builder(dialogContext);
//        this.getWindow().setBackgroundDrawableResource(R.color.select_item_navigation);
//        final SpannableString s = new SpannableString(message);
//        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setNeutralButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

//        TextView myMessage = ((TextView) this.findViewById(android.R.id.message));
//        TextView myTitle = ((TextView) this.findViewById(android.R.id.title));
//        // Next Line unfortunately does nothing
//        if(myMessage != null && myTitle != null){
//            myMessage.setTextColor(Color.MAGENTA);
//            myMessage.setBackgroundColor(Color.BLACK);
//            myTitle.setTextColor(Color.CYAN);
//            myTitle.setBackgroundColor(Color.GREEN);
//        }
        adb.show();
    }

    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    private Intent getStartIntentByItemPosition(int itemPosition) {
        Class category = null;
        Intent intent = null;
        switch (itemPosition) {
            case 0: //Catalog
                category = CatalogListActivity.class;
                break;
            case 1: //Shop
                category = ShopsActivity.class;
                break;
            case 2: //Favorites
                category = null;
                break;
            case 3: //Basket
                category = null;
                break;
            case 4: //Scan Code
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.initiateScan();
                getSupportActionBar().setSelectedNavigationItem(mCurrentCategory);
                break;
            default:
                category = null;
        }
        if (!this.getClass().equals(category) && category != null) {
            intent = new Intent(this, category);
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
