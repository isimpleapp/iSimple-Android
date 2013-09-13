package com.treelev.isimple.activities.filter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.BaseExpandableListActivity;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.utils.Utils;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.RadioButton;
import org.holoeverywhere.widget.SimpleExpandableListAdapter;

import java.util.*;

public class ExpandableListFilterActivity extends BaseExpandableListActivity {
    private static final String BUNDLE_EXTRA = "bundle_extra";
    private static final String GROUP_DATA = "group_data";

    private final static String GROUP_NAME = "group";
    private final static String ITEM_NAME = "item";

    private final static String CURRENT_CATEGORY = "current_category";

    private FilterItemData[] groupData;
    private Map<String, FilterItemData[]> childData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_expandable_layout);
        createNavigationMenuBar();
        getExpandableListView().setAdapter(new CustomExpandableListAdapter(
                getGroupItems(getGroupData()),
                R.layout.filter_item_view,
                new String[] { GROUP_NAME },
                new int[] { R.id.filter_data_name },
                getChildItems(getGroupData(), getChildData()),
                R.layout.filter_item_view,
                new String[] { ITEM_NAME },
                new int[] { R.id.filter_data_name }));
        hookRemoveArrowGroupItem();
    }

    @Override
    protected void createNavigationMenuBar() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RelativeLayout relative = new RelativeLayout(getSupportActionBarContext());
        org.holoeverywhere.widget.TextView title = new org.holoeverywhere.widget.TextView(getSupportActionBarContext());
        title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 27.0f);
        title.setTextColor(getResources().getColor(R.color.isimple_pink));
        title.setPadding(10, 0, 0, 0);
        relative.addView(title);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(relative);
        int currentCategory = getIntent().getIntExtra(CURRENT_CATEGORY, -1);
        switch(currentCategory) {
            case 0:
                getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
                title.setText("Каталог");
                break;
            case 1:
                getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
                title.setText("Магазины");
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    private List<Map<String, ?>> getGroupItems(FilterItemData[] groupData) {
        List<Map<String, ?>> groups = new ArrayList<Map<String, ?>>();
        for (FilterItemData group : groupData) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(GROUP_NAME, group.getName());
            groups.add(item);
        }
        return groups;
    }

    private List<List<Map<String, ?>>> getChildItems(FilterItemData[] groupData, Map<String, FilterItemData[]> childData) {
        List<List<Map<String, ?>>> subItemsData = new ArrayList<List<Map<String, ?>>>();
        for (FilterItemData group : groupData) {
            List<Map<String, ?>> childList = new ArrayList<Map<String, ?>>();
            for (FilterItemData childItem : childData.get(group.getName())) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put(ITEM_NAME, childItem.getName());
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
        putFilterData(resultIntent, getGroupData(), getChildData());
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    public static FilterItemData[] getGroupData(Intent intent) {
        Bundle bundle = intent.getBundleExtra(BUNDLE_EXTRA);
        if (bundle != null) {
            Parcelable[] parcelableItems = bundle.getParcelableArray(GROUP_DATA);
            return (parcelableItems != null) ?
                    Arrays.copyOf(parcelableItems, parcelableItems.length, FilterItemData[].class) :
                    new FilterItemData[0];
        }
        else {
            return new FilterItemData[0];
        }
    }

    public static Map<String, FilterItemData[]> getChildData(Intent intent) {
        return getChildData(intent, getGroupData(intent));
    }

    public static Map<String, FilterItemData[]> getChildData(Intent intent, FilterItemData[] groupData) {
        Bundle bundle = intent.getBundleExtra(BUNDLE_EXTRA);
        Map<String, FilterItemData[]> childData = new HashMap<String, FilterItemData[]>();
        if (bundle != null) {
            if (groupData != null) {
                for (FilterItemData group : groupData) {
                    Parcelable[] parcelableItems = bundle.getParcelableArray(group.getName());
                    childData.put(group.getName(), parcelableItems != null ?
                            Arrays.copyOf(parcelableItems, parcelableItems.length, FilterItemData[].class) :
                            new FilterItemData[0]);
                }
            }
            return childData;
        }
        else {
            return childData;
        }
    }

    public static void putFilterData(Intent intent, FilterItemData[] groupData, Map<String, FilterItemData[]> childData) {
        Bundle bundle = intent.getBundleExtra(BUNDLE_EXTRA);
        if (bundle == null) {
            bundle = new Bundle();
            intent.putExtra(BUNDLE_EXTRA, bundle);
        }
        bundle.putParcelableArray(GROUP_DATA, groupData);
        for (FilterItemData group : groupData)
            bundle.putParcelableArray(group.getName(), childData.get(group.getName()));
    }

    public static void putFilterData(Intent intent, FilterItemData[] groupData, Map<String, FilterItemData[]> childData, int category) {
        Bundle bundle = intent.getBundleExtra(BUNDLE_EXTRA);
        if (bundle == null) {
            bundle = new Bundle();
            intent.putExtra(BUNDLE_EXTRA, bundle);
        }
        bundle.putParcelableArray(GROUP_DATA, groupData);
        for (FilterItemData group : groupData) {
            bundle.putParcelableArray(group.getName(), childData.get(group.getName()));
        }
        intent.putExtra(CURRENT_CATEGORY, category);
    }

    private FilterItemData[] getGroupData() {
        if (groupData == null) {
            groupData = getGroupData(getIntent());
        }
        return groupData;
    }

    private Map<String, FilterItemData[]> getChildData() {
        if (childData == null) {
            childData = getChildData(getIntent(), getGroupData());
        }
        return childData;
    }

    private void hookRemoveArrowGroupItem(){
        ExpandableListView expandView = getExpandableListView();
        CustomExpandableListAdapter adapter = (CustomExpandableListAdapter)expandView.getExpandableListAdapter();
        int countGroup = adapter.getGroupCount();
        for(int position = 0; position < countGroup; ++position){
            expandView.expandGroup(position);
            if(adapter.hasChildren(position)){
                expandView.collapseGroup(position);
            }
        }
    }

    private class CustomExpandableListAdapter extends SimpleExpandableListAdapter implements View.OnClickListener {

        private List<View> mGroupViews;


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
            mGroupViews = new ArrayList<View>();
        }

        public View getGroupView(int position){
            return mGroupViews.get(position);
        }

        @Override
        public View newGroupView(boolean isExpanded, ViewGroup parent) {
            View groupView = super.newGroupView(isExpanded, parent);
            CheckBox checkBox = (CheckBox) groupView.findViewById(R.id.filter_data_check);
            TextView textView = (TextView)groupView.findViewById(R.id.filter_data_name);
            textView.setPadding(30, textView.getPaddingTop(), textView.getPaddingRight(), textView.getPaddingBottom());
            groupView.setOnClickListener(this);
            checkBox.setOnClickListener(this);
            GroupViewHolder viewHolder = new GroupViewHolder();
            viewHolder.listView = (ExpandableListView)parent;
            viewHolder.textView = textView;
            viewHolder.checkBox = checkBox;
            groupView.setTag(viewHolder);
            mGroupViews.add(groupView);
            return groupView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View groupView = super.getGroupView(groupPosition, isExpanded, convertView, parent);
            GroupViewHolder viewHolder = (GroupViewHolder)groupView.getTag();
            FilterItemData[] filters = getFilterItems(groupPosition);
            viewHolder.groupPosition = groupPosition;
            viewHolder.textView.setText(Utils.ellipseString(viewHolder.textView.getText().toString(), 24));
            FilterItemData filterData = getGroupData()[groupPosition];
            viewHolder.textView.setTextColor(filterData.isChecked() || isAnyItemChecked(filters) ? Color.BLACK : Color.LTGRAY);
            viewHolder.checkBox.setChecked(filterData.isChecked());
//            if( !hasChildren(groupPosition) && !viewHolder.hook){
//                groupView.setOnClickListener(null);
//                viewHolder.hook = true;
//            }
            return groupView;
        }

        public boolean hasChildren(int groupPosition){
            return getFilterItems(groupPosition).length > 0;
        }

        @Override
        public View newChildView(boolean isLastChild, ViewGroup parent) {
            View view = super.newChildView(isLastChild, parent);
            TextView textView = (TextView) view.findViewById(R.id.filter_data_name);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.filter_data_check);
            view.setOnClickListener(this);
            checkBox.setOnClickListener(this);
            ChildViewHolder viewHolder = new ChildViewHolder();
            viewHolder.listView = (ExpandableListView)parent;
            viewHolder.textView = textView;
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
            viewHolder.textView.setText(Utils.ellipseString(viewHolder.textView.getText().toString(), 27));
            viewHolder.checkBox.setChecked(filterData.isChecked());
            return childView;
        }

        @Override
        public void onClick(View v) {
            int originalEventSourceId = v.getId();

            if (originalEventSourceId == R.id.filter_data_check) {
                v = (View)v.getParent();
            }
            Object tag = v.getTag();
            if (tag instanceof GroupViewHolder)  {
                processGroupViewClick(v, originalEventSourceId);
            } else if (tag instanceof ChildViewHolder) {
                processChildViewClick(v);
            }
        }

        private void processGroupViewClick(View v, int originalEventSourceId) {
            GroupViewHolder viewHolder = ((GroupViewHolder) v.getTag());
            if (originalEventSourceId != R.id.filter_data_check) {
                if (viewHolder.listView.isGroupExpanded(viewHolder.groupPosition) && hasChildren(viewHolder.groupPosition)) {
                    viewHolder.listView.collapseGroup(viewHolder.groupPosition);
                } else {
                    viewHolder.listView.expandGroup(viewHolder.groupPosition);
                }
            } else {
                FilterItemData filterData = getGroupData()[viewHolder.groupPosition];
                filterData.setChecked(!filterData.isChecked());
                viewHolder.checkBox.setChecked(filterData.isChecked());
                viewHolder.textView.setTextColor(filterData.isChecked() ? Color.BLACK : Color.LTGRAY);
                if (filterData.isChecked()) {
                    FilterItemData[] children = getFilterItems(viewHolder.groupPosition);

                    for (int childPosition = 0; childPosition < children.length; childPosition++) {
                        children[childPosition].setChecked(false);
                    }

                    if (viewHolder.listView.isGroupExpanded(viewHolder.groupPosition) && hasChildren(viewHolder.groupPosition)) {
                        viewHolder.listView.collapseGroup(viewHolder.groupPosition);
                    }
                }
            }
        }

        private void processChildViewClick(View v) {
            ChildViewHolder viewHolder = ((ChildViewHolder) v.getTag());
            FilterItemData filterData = getFilterItemData(viewHolder.groupPosition, viewHolder.childPosition);
            filterData.setChecked(!filterData.isChecked());
            viewHolder.checkBox.setChecked(filterData.isChecked());

            FilterItemData[] children = getFilterItems(viewHolder.groupPosition);
            boolean anyItemChecked = isAnyItemChecked(children);
            if (anyItemChecked) {
                FilterItemData groupData = getGroupData()[viewHolder.groupPosition];
                groupData.setChecked(false);
            }

            long packedGroupPosition = ExpandableListView.getPackedPositionForGroup(viewHolder.groupPosition);
            int flatGroupPosition = viewHolder.listView.getFlatListPosition(packedGroupPosition);
            if (viewHolder.listView.getFirstVisiblePosition() <= flatGroupPosition) {
                View groupView = viewHolder.listView.getChildAt(flatGroupPosition - viewHolder.listView.getFirstVisiblePosition());
                GroupViewHolder groupViewHolder = ((GroupViewHolder) groupView.getTag());
                groupViewHolder.textView.setTextColor(anyItemChecked ? Color.BLACK : Color.LTGRAY);
                if (anyItemChecked) {
                    groupViewHolder.checkBox.setChecked(false);
                }
            }
        }

        private FilterItemData getFilterItemData(int groupPosition, int childPosition) {
            FilterItemData[] filters = getFilterItems(groupPosition);
            return filters[childPosition];
        }

        private FilterItemData[] getFilterItems(int groupPosition) {
            Map<String, Object> group = (Map<String, Object>)getGroup(groupPosition);
            return getChildData().get(group.get(GROUP_NAME));
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
            ExpandableListView listView;
            CheckBox checkBox;
            TextView textView;
            int groupPosition;
            int childPosition;
        }

        private class GroupViewHolder {
            ExpandableListView listView;
            CheckBox checkBox;
            TextView textView;
            int groupPosition;
            boolean hook;
        }
    }
}