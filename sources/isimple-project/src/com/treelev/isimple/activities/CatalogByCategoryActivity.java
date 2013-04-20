package com.treelev.isimple.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.SimpleExpandableListAdapter;

import java.util.*;

public class CatalogByCategoryActivity extends ListActivity implements RadioGroup.OnCheckedChangeListener {

    private final static String FIELD_TAG = "field_tag";
    private ProxyManager proxyManager;
    private List<Item> items;
    private List<Map<String, ?>> uiItemList;
    private SimpleAdapter listCategoriesAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_category_layout);
        RadioGroup rd = (RadioGroup) findViewById(R.id.sort_group);
        rd.check(R.id.alphabet_sort);
        rd.setOnCheckedChangeListener(this);
        int categoryId = getIntent().getIntExtra(CatalogListActivity.CATEGORY_NAME_EXTRA_ID, -1);
        proxyManager = new ProxyManager(this);
        items = proxyManager.getItemsByCategory(categoryId);
        uiItemList = proxyManager.convertItemsToUI(items, ProxyManager.SORT_NAME_AZ);
        listCategoriesAdapter = new SimpleAdapter(this,
                uiItemList,
                R.layout.catalog_item_layout,
                Item.getUITags(),
                new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_drink_type, R.id.item_volume, R.id.item_price});
        getListView().setAdapter(listCategoriesAdapter);
        SimpleExpandableListAdapter filterAdapter = new SimpleExpandableListAdapter(this, createExpandableGroups(),
                R.layout.category_filtration_expandable_group_layout, new String[]{FIELD_TAG}, new int[]{R.id.group_name}, null,
                R.layout.expandable_item_layout, new String[]{FIELD_TAG}, new int[]{R.id.item_content}
        );
        ((ExpandableListView) findViewById(R.id.filtration_view)).setAdapter(filterAdapter);
    }

    private List<Map<String, ?>> createExpandableGroups() {
        List<Map<String, ?>> groupName = new ArrayList<Map<String, ?>>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put(FIELD_TAG, getString(R.string.filtration_group_name));
        groupName.add(item);
        return groupName;
    }

    /*private List<List<Map<String, ?>>> createExpandableContent() {
        List<List<Map<String, ?>>> list = new ArrayList<List<Map<String, ?>>>();
        for (ExpandableListItem anExpandableListItemList : expandableListItemList) {
            List<Map<String, ?>> itemList = new ArrayList<Map<String, ?>>();
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(FIELD_TAG, anExpandableListItemList.getItemContent());
            itemList.add(item);
            list.add(itemList);
        }
        return list;
    }*/


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HashMap product = (HashMap) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, (String) product.get(Item.UI_TAG_ID));
        startActivity(startIntent);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int rgb) {
        switch (rgb) {
            case R.id.alphabet_sort:
                updateList(ProxyManager.SORT_NAME_AZ);
                break;
            case R.id.price_sort:
                updateList(ProxyManager.SORT_PRICE_UP);
                break;
        }
    }


    private void updateList(int sortBy) {
        uiItemList.clear();
        uiItemList.addAll(proxyManager.convertItemsToUI(items, sortBy));
        listCategoriesAdapter.notifyDataSetChanged();
    }
}