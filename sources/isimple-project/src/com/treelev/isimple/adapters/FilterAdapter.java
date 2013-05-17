package com.treelev.isimple.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.FilterItem;
import com.treelev.isimple.filter.Filter;
import com.treelev.isimple.views.RangeSeekBar;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import java.util.List;

public class FilterAdapter extends BaseExpandableListAdapter {

    private List<FilterItem> items;
    private LayoutInflater layoutInflater;
    private Context context;

    public FilterAdapter(Context context, Filter filter) {
        this.items = filter.getFilterContent();
        this.context = context;
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
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.category_filter_group_layout, null);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //ViewHolder viewHolder;
        FilterItem filterItem = getChild(groupPosition, childPosition);
        return filterItem.renderView(convertView);

//        if (convertView == null) {
//            convertView = layoutInflater.inflate(R.layout.category_filtration_expandable_item_layout, null);
//            viewHolder = new ViewHolder();
//            viewHolder.text = (TextView) convertView.findViewById(R.id.item_content);
//            viewHolder.seekBarLayout = (LinearLayout) convertView.findViewById(R.id.seek_bar_layout);
//            viewHolder.seekBarLayout.addView(createSeekBar());
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        if (filterItem.getItemType() == FilterItem.ITEM_ACTIVITY) {
//            viewHolder.text.setVisibility(View.VISIBLE);
//            viewHolder.seekBarLayout.setVisibility(View.GONE);
//            viewHolder.text.setText(filterItem.getLabel());
//        } else {
//            viewHolder.text.setVisibility(View.GONE);
//            viewHolder.seekBarLayout.setVisibility(View.VISIBLE);
//        }
//        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private RangeSeekBar<Integer> createSeekBar() {
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(20, 75, context);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                Log.i("TAG", "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
            }
        });
        seekBar.setBackgroundColor(Color.WHITE);

        return seekBar;
    }

    private class ViewHolder {
        public TextView text;
        public LinearLayout seekBarLayout;
    }
}
