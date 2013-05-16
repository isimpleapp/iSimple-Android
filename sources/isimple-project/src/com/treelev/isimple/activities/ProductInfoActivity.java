package com.treelev.isimple.activities;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.adapters.ProductContentAdapter;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.ui.ProductContent;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.ExpandableListActivity;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductInfoActivity extends ExpandableListActivity implements ActionBar.OnNavigationListener {

    public final static String ITEM_ID_TAG = "id";
    private final static String FORMAT_FIELDS = "- %s";
    private final static String EMPTY_PRICE_LABEL = "-";
    private final static String FORMAT_ALCOHOL = "%s%% алк.";
    private final static String FORMAT_VOLUME = "%s л.";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNavigation();
        String itemId = getIntent().getStringExtra(ITEM_ID_TAG);
        setContentView(R.layout.product_layout);
        ProxyManager proxyManager = new ProxyManager(this);
        Item mProduct = proxyManager.getItemById(itemId);
        ExpandableListView listView = getExpandableListView();
        View headerView = getLayoutInflater().inflate(R.layout.product_header_view, listView, false);
        listView.addHeaderView(headerView, null, false);
        populateFormsFields(headerView, mProduct);
        List<ProductContent> productContentList = createExpandableItems(mProduct);
        BaseExpandableListAdapter listAdapter = new ProductContentAdapter(this, productContentList);
        listView.setAdapter(listAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    private List<ProductContent> createExpandableItems(Item product) {
        String[] itemsNames = getResources().getStringArray(R.array.expandable_groups_names);
        List<ProductContent> items = new ArrayList<ProductContent>();
        addExpandableItem(items, itemsNames[0], product.getStyleDescription());
        addExpandableItem(items, itemsNames[1], product.getGastronomy());
        addExpandableItem(items, itemsNames[2], product.getTasteQualities());
        addExpandableItem(items, itemsNames[3], product.getInterestingFacts());
        addExpandableItem(items, itemsNames[4], product.getProductionProcess());
        addExpandableItem(items, itemsNames[5], product.getDrinkCategory().getDescription());
        addExpandableItem(items, itemsNames[6], product.getVineyard());
        return items;
    }

    private void addExpandableItem(List<ProductContent> listItems, String itemName, String itemContent) {
        if (!TextUtils.isEmpty(itemContent)) {
            listItems.add(new ProductContent(itemName, itemContent));
        }
    }

    private void populateFormsFields(View formView, Item product) {
        String priceLabel = Utils.organizePriceLabel(product.getPrice());
        ((Button) formView.findViewById(R.id.add_to_basket_butt)).setText(priceLabel != null ? priceLabel : EMPTY_PRICE_LABEL);
        ((TextView) formView.findViewById(R.id.product_name)).setText(product.getName());
        ((TextView) formView.findViewById(R.id.product_manufacturer)).setText(product.getManufacturer());
        ((TextView) formView.findViewById(R.id.product_localizated_name)).setText(product.getLocalizedName());
        ((TextView) formView.findViewById(R.id.product_item_id)).setText(product.getItemID());
        organizeTextView((TextView) formView.findViewById(R.id.product_region), product.getRegion());
        organizeTextView((TextView) formView.findViewById(R.id.product_sweetness), product.getSweetness().getDescription());
        organizeTextView((TextView) formView.findViewById(R.id.product_style), product.getStyle().getDescription());
        organizeTextView((TextView) formView.findViewById(R.id.product_grapes), product.getGrapesUsed());
        organizeTextView((TextView) formView.findViewById(R.id.product_alcohol), Utils.organizeProductLabel(FORMAT_ALCOHOL, trimTrailingZeros(product.getAlcohol())));
        organizeTextView((TextView) formView.findViewById(R.id.product_volume), Utils.organizeProductLabel(FORMAT_VOLUME, trimTrailingZeros(product.getVolume())));
        organizeTextView((TextView) formView.findViewById(R.id.product_year), product.getYear());
        if (priceLabel != null){
        ((TextView) formView.findViewById(R.id.retail_price)).setText(Utils.organizePriceLabel(getResources().getString(R.string.text_for_retail_price, takeRetailPrice(product).toString())));
        } else {
            ((TextView) formView.findViewById(R.id.retail_price)).setText("");
        }
    }

    private Integer takeRetailPrice(Item product) {
        int retailPrice;
        String priceLabel = Utils.organizePriceLabel(product.getPrice());
//        Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "product.getName() = " + product.getName());
        if (priceLabel != null) {
            Scanner in = new Scanner(priceLabel).useDelimiter("[^0-9]+");
            int integerPriceLabel = in.nextInt();
//            Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "integerPriceLabel = " + integerPriceLabel);

            String priceMarkup = Utils.organizePriceLabel(product.getPriceMarkup());
//            Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "priceMarkup = " + priceMarkup);

            if (priceMarkup != null) {
                Scanner intMarkup = new Scanner(priceMarkup).useDelimiter("[^0-9]+");
                int integerPriceMarkup = intMarkup.nextInt();
//                Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "integerPriceMarkup = " + integerPriceMarkup);
                retailPrice = integerPriceLabel * (integerPriceMarkup + 100) / 100;
            } else {
                retailPrice = integerPriceLabel;
            }
//            Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "retailPrice = " + retailPrice);
            return roundToTheTens(retailPrice);
        } else {
            return null;
        }
    }

    private int roundToTheTens(int price) {
        if ((price % 10) != 0) {
            int newPrice;
            return newPrice = price + (10 - (price % 10));
        } else {
            return price;
        }
    }

    private void organizeTextView(TextView textView, String text) {
        if (!TextUtils.isEmpty(text)) {
            textView.setText(String.format(FORMAT_FIELDS, text));
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private String trimTrailingZeros(String number) {
        return Utils.removeZeros(number);
    }

    private void createNavigation() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Context context = getSupportActionBar().getThemedContext();
        String[] menuItemText = getResources().getStringArray(R.array.main_menu_items);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.main_menu_icons);
        Drawable[] menuItemIcon = new Drawable[typedArray.length()];
        for (int i = 0; i < menuItemText.length; ++i) {
            menuItemIcon[i] = typedArray.getDrawable(i);
        }
        NavigationListAdapter navigationAdapter = new NavigationListAdapter(this, menuItemIcon, menuItemText);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(navigationAdapter, this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
    }
}