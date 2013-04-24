package com.treelev.isimple.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.TextView;

import java.util.List;

public class FilterAdapter extends BaseExpandableListAdapter {

    private String groupName;
    private List<String> items;
    private LayoutInflater infalInflater;

    public FilterAdapter(Context context, String groupName, List<String> items) {
        this.groupName = groupName;
        this.items = items;
        infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return groupName != null ? 1 : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition == 0 ? groupName : null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
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
        String group = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = infalInflater.inflate(R.layout.category_filtration_expandable_group_layout, null);
        }
        ((TextView) convertView.findViewById(R.id.group_name)).setText(group);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String filterItem = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            int resource;
            if (childPosition == 3) {
                resource = R.layout.category_filtration_progress_bar_layout;
            } else if (childPosition == 5) {
                resource = R.layout.category_filtration_button_bar_layout;
            } else {
                resource = R.layout.category_filtration_expandable_item_layout;
            }
            convertView = infalInflater.inflate(resource, null);
        }
        TextView textView = ((TextView) convertView.findViewById(R.id.item_content));
        if (textView != null) {
            textView.setText(filterItem);
        }
        return convertView;
    }

    private View.OnClickListener resetButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
