package com.treelev.isimple.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogByCategoryActivity extends ListActivity implements RadioGroup.OnCheckedChangeListener, ActionBar.OnNavigationListener {

    private final static String FIELD_TAG = "field_tag";
    private ProxyManager proxyManager;
    private List<Item> items;
    private List<Map<String, ?>> uiItemList;
    private SimpleAdapter listCategoriesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_category_layout);
        createNavigation();
        View headerView = getLayoutInflater().inflate(R.layout.category_header_layout, getListView(), false);
        RadioGroup rd = (RadioGroup) headerView.findViewById(R.id.sort_group);
        rd.check(R.id.alphabet_sort);
        rd.setOnCheckedChangeListener(this);
        int categoryId = getIntent().getIntExtra(CatalogListActivity.CATEGORY_NAME_EXTRA_ID, -1);
        initListView(headerView, categoryId);
        SimpleExpandableListAdapter filterAdapter = new SimpleExpandableListAdapter(this, createExpandableGroups(),
                R.layout.category_filtration_expandable_group_layout, new String[]{FIELD_TAG}, new int[]{R.id.group_name}, createExpandableContent(),
                R.layout.category_filtration_expandable_item_layout, new String[]{FIELD_TAG}, new int[]{R.id.item_content}
        );
        ((ExpandableListView) headerView.findViewById(R.id.filtration_view)).setAdapter(filterAdapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setIconifiedByDefault(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HashMap product = (HashMap) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, (String) product.get(Item.UI_TAG_ID));
        startActivity(startIntent);
    }

    private void initListView(View headerView, int categoryId) {
        proxyManager = new ProxyManager(this);
        items = proxyManager.getItemsByCategory(categoryId);
        uiItemList = proxyManager.convertItemsToUI(items, ProxyManager.SORT_NAME_AZ);
        listCategoriesAdapter = new SimpleAdapter(this,
                uiItemList,
                R.layout.catalog_item_layout,
                Item.getUITags(),
                new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_drink_type, R.id.item_volume, R.id.item_price});
        getListView().addHeaderView(headerView, null, false);
        getListView().setAdapter(listCategoriesAdapter);
    }

    private List<Map<String, ?>> createExpandableGroups() {
        List<Map<String, ?>> groupName = new ArrayList<Map<String, ?>>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put(FIELD_TAG, getString(R.string.filtration_group_name));
        groupName.add(item);
        return groupName;
    }

    private List<List<Map<String, ?>>> createExpandableContent() {
        List<List<Map<String, ?>>> content = new ArrayList<List<Map<String, ?>>>();
        List<Map<String, ?>> contentFilterMenu = new ArrayList<Map<String, ?>>();
        for (int j = 0; j < 7; ++j) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(FIELD_TAG, String.valueOf(j));
            contentFilterMenu.add(item);
        }
        content.add(contentFilterMenu);
        return content;
    }

    private void updateList(int sortBy) {
        uiItemList.clear();
        uiItemList.addAll(proxyManager.convertItemsToUI(items, sortBy));
        listCategoriesAdapter.notifyDataSetChanged();
    }

    private void createNavigation() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        Context context = getSupportActionBar().getThemedContext();
        String[] locations = getResources().getStringArray(R.array.main_menu_items);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.main_menu_icons);
        Drawable[] iconLocation = new Drawable[typedArray.length()];
        for (int i = 0; i < locations.length; ++i) {
            iconLocation[i] = typedArray.getDrawable(i);
        }
        NavigationListAdapter list = new NavigationListAdapter(this, iconLocation, locations);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);

    }
}