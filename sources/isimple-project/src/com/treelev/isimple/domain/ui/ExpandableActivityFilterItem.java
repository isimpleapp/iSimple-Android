package com.treelev.isimple.domain.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.filter.ExpandableListFilterActivity;
import com.treelev.isimple.activities.filter.DefaultListFilterActivity;
import org.holoeverywhere.widget.TextView;

import java.util.Map;

public class ExpandableActivityFilterItem extends FilterItem {
    private LayoutInflater layoutInflater;
    private Map<String, FilterItemData[]> filterData;

    public ExpandableActivityFilterItem(Context context, String label, Map<String, FilterItemData[]> filterData) {
        super(context, ITEM_ACTIVITY, label, ExpandableListFilterActivity.class);
        layoutInflater = LayoutInflater.from(context);
        this.filterData = filterData;
    }

    private boolean isAnyItemChecked() {
        if (filterData != null) {
            for (String key : filterData.keySet()) {
                for (FilterItemData item : filterData.get(key)) {
                    if (item.isChecked())
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    protected Intent createIntent() {
        Intent intent = super.createIntent();
        if (filterData != null) {
            ExpandableListFilterActivity.putFilterData(intent, filterData);
        }
        return intent;
    }

    @Override
    public boolean processResult(int requestCode, int resultCode, Intent data) {
        if (super.processResult(requestCode, resultCode, data)) {
            filterData = ExpandableListFilterActivity.getFilterData(data);
            return true;
        }
        else {
            return false;
        }
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
        text.setTextColor(isAnyItemChecked() ? Color.BLACK : Color.LTGRAY);

        return convertView;
    }
}
