package com.treelev.isimple.domain.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.filter.DefaultListFilterActivity;
import org.holoeverywhere.widget.TextView;

public class DefaultActivityFilterItem extends FilterItem {
    private LayoutInflater layoutInflater;
    private FilterItemData[] filterData;

    public DefaultActivityFilterItem(Context context, String label, FilterItemData[] filterData) {
        this (context, label, DefaultListFilterActivity.class, filterData);
    }

    public DefaultActivityFilterItem(Context context, String label, Class activityClass, FilterItemData[] filterData) {
        super(context, ITEM_ACTIVITY, label, activityClass);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.filterData = filterData;
    }

    public FilterItemData[] getFilterData() {
        return filterData;
    }

    public void setFilterData(FilterItemData[] filterData) {
        this.filterData = filterData;
    }

    @Override
    protected Intent createIntent() {
        Intent intent = super.createIntent();
        if (getActivityClass().equals(DefaultListFilterActivity.class) && filterData != null) {
            DefaultListFilterActivity.putFilterData(intent, filterData);
        }
        return intent;
    }

    @Override
    public View renderView(View convertView) {
        if (convertView == null || !(convertView.getTag() instanceof TextView)) {
            convertView = layoutInflater.inflate(R.layout.category_filter_text_item_layout, null);
            TextView text = (TextView) convertView.findViewById(R.id.item_content);
            convertView.setTag(text);
        }
        TextView text = (TextView) convertView.getTag();
        text.setText(getLabel());
        return convertView;
    }
}
