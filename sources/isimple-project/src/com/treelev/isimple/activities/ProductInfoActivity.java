package com.treelev.isimple.activities;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNavigation();
        String itemId = getIntent().getStringExtra(ITEM_ID_TAG);
        setContentView(R.layout.product_layout);
        ProxyManager proxyManager = new ProxyManager(this);
        Item product = proxyManager.getItemById(itemId);
        ExpandableListView listView = getExpandableListView();
        View headerView = getLayoutInflater().inflate(R.layout.product_header_view, listView, false);
        listView.addHeaderView(headerView, null, false);
        populateFormsFields(headerView, product);
        List<ExpandableListItem> expandableListItemList = createExpandableItems(product);
        SimpleExpandableListAdapter listAdapter = new SimpleExpandableListAdapter(this, createExpandableGroups(expandableListItemList),
                R.layout.product_info_expandable_group_layout, new String[]{FIELD_TAG}, new int[]{R.id.group_name}, createExpandableContent(expandableListItemList),
                R.layout.expandable_item_layout, new String[]{FIELD_TAG}, new int[]{R.id.item_content}
        );
        listView.setAdapter(listAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
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
        items.add(new ExpandableListItem(itemsNames[0], product.getStyleDescription()));
        items.add(new ExpandableListItem(itemsNames[1], product.getGastronomy()));
        items.add(new ExpandableListItem(itemsNames[2], product.getTasteQualities()));
        items.add(new ExpandableListItem(itemsNames[3], product.getInterestingFacts()));
        items.add(new ExpandableListItem(itemsNames[4], product.getProductionProcess()));
        items.add(new ExpandableListItem(itemsNames[5], product.getDrinkCategory().getDescription()));
        items.add(new ExpandableListItem(itemsNames[6], product.getVineyard()));
        return items;
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
        organizeTextView((TextView) formView.findViewById(R.id.product_name), product.getName());
        organizeTextView((TextView) formView.findViewById(R.id.product_manufacturer), product.getManufacturer());
        organizeTextView((TextView) formView.findViewById(R.id.product_localizated_name), product.getLocalizedName());
        organizeTextView((TextView) formView.findViewById(R.id.product_drink_id), product.getDrinkID());
        organizeTextView((TextView) formView.findViewById(R.id.product_region), product.getRegion() != null ?
                String.format(FORMAT_FIELDS, product.getRegion()) : null);
        organizeTextView((TextView) formView.findViewById(R.id.product_sweetness), product.getSweetness().getDescription() != null ?
                String.format(FORMAT_FIELDS, product.getSweetness().getDescription()) : null);
        organizeTextView((TextView) formView.findViewById(R.id.product_style), product.getStyle().getDescription() != null ?
                String.format(FORMAT_FIELDS, product.getStyle().getDescription()) : null);
        organizeTextView((TextView) formView.findViewById(R.id.product_grapes), product.getGrapesUsed() != null ?
                String.format(FORMAT_FIELDS, product.getGrapesUsed()) : null);
        organizeTextView((TextView) formView.findViewById(R.id.product_alcohol), product.getAlcohol() != null ?
                String.format(FORMAT_FIELDS, product.getAlcohol()) : null);
        organizeTextView((TextView) formView.findViewById(R.id.product_volume), product.getVolume() != null ?
                String.format(FORMAT_FIELDS, product.getVolume()) : null);
        organizeTextView((TextView) formView.findViewById(R.id.product_year), product.getYear() != null ?
                String.format(FORMAT_FIELDS, product.getYear()) : null);
    }

    private void organizeTextView(TextView textView, String text) {
        if (text != null) {
            textView.setText(text);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void createNavigation() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
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
    }
}