package com.treelev.isimple.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.FilterItem;
import com.treelev.isimple.filter.Filter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.BaseExpandableListAdapter;

import java.util.List;

public class FilterAdapter extends BaseExpandableListAdapter {

    private List<FilterItem> items;
    private LayoutInflater layoutInflater;
    private View groupView;

    public FilterAdapter(Context context, Filter filter) {
        this.items = filter.getFilterContent();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items.size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return null;
    }

    @Override
    public FilterItem getChild(int groupPosition, int childPosition) {
        return items.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (groupView == null) {
            groupView = layoutInflater.inflate(R.layout.category_filter_group_layout, null);
        }
        return groupView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        FilterItem filterItem = getChild(groupPosition, childPosition);
        return filterItem.renderView(convertView);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
