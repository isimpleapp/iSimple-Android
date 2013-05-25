package com.treelev.isimple.activities.filter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.BaseExpandableListActivity;
import com.treelev.isimple.domain.ui.FilterItemData;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.SimpleExpandableListAdapter;

import java.util.*;

public class ExpandableListFilterActivity extends BaseExpandableListActivity {
    private static final String BUNDLE_EXTRA = "bundle_extra";
    private static final String NAME_DATA = "name_data";

    private final static String GROUP_NAME = "group";
    private final static String ITEM_NAME = "item";

    private Map<String, FilterItemData[]> filterData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_expandable_layout);
        createNavigationMenuBar();
        getExpandableListView().setAdapter(new CustomExpandableListAdapter(
                getGroupItems(getFilterData()),
                android.R.layout.simple_expandable_list_item_1,
                new String[] { GROUP_NAME },
                new int[] { android.R.id.text1 },
                getSubItems(getFilterData()),
                R.layout.filter_item_view,
                new String[] { ITEM_NAME },
                new int[] { R.id.filter_data_name }));
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    private List<Map<String, ?>> getGroupItems(Map<String, FilterItemData[]> filterData) {
        List<Map<String, ?>> groups = new ArrayList<Map<String, ?>>();
        for (String groupName : filterData.keySet()) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(GROUP_NAME, groupName);
            groups.add(item);
        }
        return groups;
    }

    private List<List<Map<String, ?>>> getSubItems(Map<String, FilterItemData[]> filterData) {
        List<List<Map<String, ?>>> subItemsData = new ArrayList<List<Map<String, ?>>>();
        for (String groupName : filterData.keySet()) {
            List<Map<String, ?>> childList = new ArrayList<Map<String, ?>>();
            for (FilterItemData subItem : filterData.get(groupName)) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put(ITEM_NAME, subItem.getName());
                childList.add(item);
            }
            subItemsData.add(childList);
        }
        return subItemsData;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        putFilterData(resultIntent, getFilterData());
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    public static Map<String, FilterItemData[]> getFilterData(Intent intent) {
        Bundle bundle = intent.getBundleExtra(BUNDLE_EXTRA);
        String[] names = bundle.getStringArray(NAME_DATA);
        Map<String, FilterItemData[]> filterData = new HashMap<String, FilterItemData[]>();
        if (names != null) {
            for (String name : names) {
                Parcelable[] parcelableItems = bundle.getParcelableArray(name);
                filterData.put(name, parcelableItems != null ?
                        Arrays.copyOf(parcelableItems, parcelableItems.length, FilterItemData[].class) :
                        new FilterItemData[0]);
            }
        }
        return filterData;
    }

    public static void putFilterData(Intent intent, Map<String, FilterItemData[]> filterData) {
        Bundle bundle = intent.getBundleExtra(BUNDLE_EXTRA);
        if (bundle == null) {
            bundle = new Bundle();
            intent.putExtra(BUNDLE_EXTRA, bundle);
        }
        bundle.putStringArray(NAME_DATA, filterData.keySet().toArray(new String[0]));
        for (String name : filterData.keySet())
            bundle.putParcelableArray(name, filterData.get(name));
    }

    private Map<String, FilterItemData[]> getFilterData() {
        if (filterData == null) {
            filterData = getFilterData(getIntent());
        }
        return filterData;
    }

    private class CustomExpandableListAdapter extends SimpleExpandableListAdapter implements View.OnClickListener {

        public CustomExpandableListAdapter(List<? extends Map<String, ?>> groupData,
                                           int groupLayout,
                                           String[] groupFrom,
                                           int[] groupTo,
                                           List<? extends List<? extends Map<String, ?>>> childData,
                                           int childLayout,
                                           String[] childFrom,
                                           int[] childTo) {
            super(ExpandableListFilterActivity.this, groupData, groupLayout,
                    groupFrom, groupTo, childData, childLayout, childFrom, childTo);
        }

        @Override
        public View newGroupView(boolean isExpanded, ViewGroup parent) {
            View groupView = super.newGroupView(isExpanded, parent);
            GroupViewHolder viewHolder = new GroupViewHolder();
            viewHolder.textView = (TextView)groupView.findViewById(android.R.id.text1);
            groupView.setTag(viewHolder);
            return groupView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View groupView = super.getGroupView(groupPosition, isExpanded, convertView, parent);
            GroupViewHolder viewHolder = (GroupViewHolder)groupView.getTag();
            FilterItemData[] filters = getFilterItems(groupPosition);
            viewHolder.groupPosition = groupPosition;
            viewHolder.textView.setTextColor(isAnyItemChecked(filters) ? Color.BLACK : Color.LTGRAY);
            return groupView;
        }

        @Override
        public View newChildView(boolean isLastChild, ViewGroup parent) {
            View view = super.newChildView(isLastChild, parent);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.filter_data_check);
            view.setOnClickListener(this);
            checkBox.setOnClickListener(this);
            ChildViewHolder viewHolder = new ChildViewHolder();
            viewHolder.checkBox = checkBox;
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View childView = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
            ChildViewHolder viewHolder = (ChildViewHolder)childView.getTag();
            viewHolder.groupPosition = groupPosition;
            viewHolder.childPosition = childPosition;
            FilterItemData filterData = getFilterItemData(groupPosition, childPosition);
            viewHolder.checkBox.setChecked(filterData.isChecked());
            return childView;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.filter_data_check) {
                v = (View)v.getParent();
            }
            ChildViewHolder viewHolder = ((ChildViewHolder) v.getTag());
            FilterItemData filterData = getFilterItemData(viewHolder.groupPosition, viewHolder.childPosition);
            filterData.setChecked(!filterData.isChecked());
            viewHolder.checkBox.setChecked(filterData.isChecked());
        }

        private FilterItemData getFilterItemData(int groupPosition, int childPosition) {
            FilterItemData[] filters = getFilterItems(groupPosition);
            return filters[childPosition];
        }

        private FilterItemData[] getFilterItems(int groupPosition) {
            Map<String, Object> group = (Map<String, Object>)getGroup(groupPosition);
            return getFilterData().get(group.get(GROUP_NAME));
        }

        private boolean isAnyItemChecked(FilterItemData[] filterData) {
            if (filterData != null) {
                for (FilterItemData item : filterData) {
                    if (item.isChecked())
                        return true;
                }
            }
            return false;
        }

        private class ChildViewHolder {
            CheckBox checkBox;
            int groupPosition;
            int childPosition;
        }

        private class GroupViewHolder {
            TextView textView;
            int groupPosition;
        }
    }
}