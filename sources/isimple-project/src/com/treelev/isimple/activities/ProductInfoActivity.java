package com.treelev.isimple.activities;


import android.os.Bundle;
import android.view.View;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.ui.ExpandableListItem;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.ExpandableListActivity;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.SimpleExpandableListAdapter;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductInfoActivity extends ExpandableListActivity {

    public final static String ITEM_ID_TAG = "id";
    private final static String FIELD_TAG = "field_tag";
    private final static String FORMAT_FIELDS = "- %s";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ((TextView) formView.findViewById(R.id.product_name)).setText(product.getName());
        ((TextView) formView.findViewById(R.id.product_manufacturer)).setText(product.getManufacturer());
        ((TextView) formView.findViewById(R.id.product_localizated_name)).setText(product.getLocalizedName());
        ((TextView) formView.findViewById(R.id.product_drink_id)).setText(product.getDrinkID());
        ((TextView) formView.findViewById(R.id.product_region)).setText(String.format(FORMAT_FIELDS, product.getRegion()));
        ((TextView) formView.findViewById(R.id.product_sweetness)).setText(String.format(FORMAT_FIELDS, product.getSweetness().getDescription()));
        ((TextView) formView.findViewById(R.id.product_style)).setText(String.format(FORMAT_FIELDS, product.getStyle().getDescription()));
        ((TextView) formView.findViewById(R.id.product_grapes)).setText(String.format(FORMAT_FIELDS, product.getGrapesUsed()));
        ((TextView) formView.findViewById(R.id.product_alcohol)).setText(String.format(FORMAT_FIELDS, product.getAlcohol()));
        ((TextView) formView.findViewById(R.id.product_volume)).setText(String.format(FORMAT_FIELDS, product.getVolume()));
        ((TextView) formView.findViewById(R.id.product_year)).setText(String.format(FORMAT_FIELDS, product.getYear()));
    }
}