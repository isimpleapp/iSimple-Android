package com.treelev.isimple.activities;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.ui.ExpandableListItem;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.ExpandableListActivity;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.SimpleExpandableListAdapter;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductInfoActivity extends ExpandableListActivity implements ActionBar.OnNavigationListener {

    public final static String ITEM_ID_TAG = "id";
    private final static String FIELD_TAG = "field_tag";
    private final static String FORMAT_FIELDS = "- %s";
    private final static String EMPTY_PRICE_LABEL = "-";
    private final static String FORMAT_ALCOHOL = "%s%% алк.";
    private final static String FORMAT_VOLUME = "%s л.";
    Item mProduct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNavigation();
        String itemId = getIntent().getStringExtra(ITEM_ID_TAG);
        setContentView(R.layout.product_layout);
        ProxyManager proxyManager = new ProxyManager(this);
        mProduct = proxyManager.getItemById(itemId);
        ExpandableListView listView = getExpandableListView();
        View headerView = getLayoutInflater().inflate(R.layout.product_header_view, listView, false);
        listView.addHeaderView(headerView, null, false);
        populateFormsFields(headerView, mProduct);
        List<ExpandableListItem> expandableListItemList = createExpandableItems(mProduct);
        SimpleExpandableListAdapter listAdapter = new SimpleExpandableListAdapter(this, createExpandableGroups(expandableListItemList),
                R.layout.product_info_expandable_group_layout, new String[]{FIELD_TAG}, new int[]{R.id.group_name}, createExpandableContent(expandableListItemList),
                R.layout.expandable_item_layout, new String[]{FIELD_TAG}, new int[]{R.id.item_content}
        );
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

    private List<List<Map<String, ?>>> createExpandableContent(List<ExpandableListItem> expandableListItemList) {
        List<List<Map<String, ?>>> list = new ArrayList<List<Map<String, ?>>>();
        for (ExpandableListItem anExpandableListItemList : expandableListItemList) {
            List<Map<String, ?>> itemList = new ArrayList<Map<String, ?>>();
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(FIELD_TAG, anExpandableListItemList.getItemContent());
            itemList.add(item);
            list.add(itemList);
        }
        return list;
    }

    private List<ExpandableListItem> createExpandableItems(Item product) {
        String[] itemsNames = getResources().getStringArray(R.array.expandable_groups_names);
        List<ExpandableListItem> items = new ArrayList<ExpandableListItem>();
        addExpandableItem(items, itemsNames[0], product.getStyleDescription());
        addExpandableItem(items, itemsNames[1], product.getGastronomy());
        addExpandableItem(items, itemsNames[2], product.getTasteQualities());
        addExpandableItem(items, itemsNames[3], product.getInterestingFacts());
        addExpandableItem(items, itemsNames[4], product.getProductionProcess());
        addExpandableItem(items, itemsNames[5], product.getDrinkCategory().getDescription());
        addExpandableItem(items, itemsNames[6], product.getVineyard());
        return items;
    }

    private void addExpandableItem(List<ExpandableListItem> listItems, String itemName, String itemContent) {
        if (!TextUtils.isEmpty(itemContent)) {
            listItems.add(new ExpandableListItem(itemName, itemContent));
        }
    }

    private List<Map<String, ?>> createExpandableGroups(List<ExpandableListItem> expandableListItemList) {
        List<Map<String, ?>> list = new ArrayList<Map<String, ?>>();
        for (ExpandableListItem listItem : expandableListItemList) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(FIELD_TAG, listItem.getItemName());
            list.add(item);
        }
        return list;
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
        organizeTextView((TextView) formView.findViewById(R.id.product_alcohol), organizeValueAlcohol(FORMAT_ALCOHOL,trimTrailingZeros(product.getAlcohol())));
        organizeTextView((TextView) formView.findViewById(R.id.product_volume), organizeValueAlcohol(FORMAT_VOLUME,Utils.organizeVolumeLabel(trimTrailingZeros(product.getVolume()))));
        organizeTextView((TextView) formView.findViewById(R.id.product_year), product.getYear());
    }

    private void organizeTextView(TextView textView, String text) {
        if (!TextUtils.isEmpty(text)) {
            textView.setText(String.format(FORMAT_FIELDS, text));
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private String organizeValueAlcohol(String format,String text) {
        String result = null;
        if(!TextUtils.isEmpty(text)){
            result = String.format(format, text);}
        return result;
    }

    private static String trimTrailingZeros(String number) {
        if(!number.contains(".")) {
            return number;
        }

        return number.replaceAll("\\.?0*$", "");
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