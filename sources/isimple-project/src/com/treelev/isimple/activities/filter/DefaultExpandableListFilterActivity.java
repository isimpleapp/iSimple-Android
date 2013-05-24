package com.treelev.isimple.activities.filter;

import android.os.Bundle;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.BaseExpandableListActivity;
import com.treelev.isimple.domain.ui.FilterItem;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultExpandableListFilterActivity extends BaseExpandableListActivity {

    private final static String COUNTRY_NAME = "country";
    private final static String REGION_NAME = "region";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int categoryId = getIntent().getIntExtra(FilterItem.EXTRA_CATEGORY_ID, -1);
        setContentView(R.layout.filter_expandable_layout);
        createNavigationMenuBar();
        ProxyManager proxyManager = new ProxyManager(this);
        Map<String, List<String>> regions = proxyManager.getRegionsByCategory(categoryId);
        getExpandableListView().setAdapter(new SimpleExpandableListAdapter(this, getGroupItems(regions),
                android.R.layout.simple_expandable_list_item_1, new String[]{COUNTRY_NAME}, new int[]{android.R.id.text1},
                getChildItems(regions), R.layout.filter_item_view, new String[]{REGION_NAME}, new int[]{R.id.filter_data_name}));
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    private List<Map<String, ?>> getGroupItems(Map<String, List<String>> regions) {
        List<Map<String, ?>> countries = new ArrayList<Map<String, ?>>();
        for (Map.Entry<String, List<String>> entry : regions.entrySet()) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(COUNTRY_NAME, entry.getKey());
            countries.add(item);
        }
        return countries;
    }

    private List<List<Map<String, ?>>> getChildItems(Map<String, List<String>> regions) {
        List<List<Map<String, ?>>> groupData = new ArrayList<List<Map<String, ?>>>();
        for (Map.Entry<String, List<String>> entry : regions.entrySet()) {
            List<Map<String, ?>> childList = new ArrayList<Map<String, ?>>();
            for (String region : entry.getValue()) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put(REGION_NAME, region);
                childList.add(item);
            }
            groupData.add(childList);
        }
        return groupData;
    }
}