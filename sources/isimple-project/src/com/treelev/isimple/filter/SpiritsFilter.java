package com.treelev.isimple.filter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.filter.DefaultExpandableListFilterActivity;
import com.treelev.isimple.activities.filter.DefaultListFilterActivity;
import com.treelev.isimple.domain.ui.DefaultActivityFilterItem;
import com.treelev.isimple.domain.ui.DefaultSeekBarFilterItem;
import com.treelev.isimple.domain.ui.FilterItem;
import org.holoeverywhere.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

public class SpiritsFilter implements Filter, View.OnClickListener {

    private Context context;
    private View filterHeaderLayout;
    private List<FilterItem> filterItemList;

    public SpiritsFilter(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        filterHeaderLayout = inflater.inflate(R.layout.category_spirits_water_filter_header_layout);
        filterHeaderLayout.setOnClickListener(this);
        filterItemList = createFilterContent();
    }

    @Override
    public View getFilterHeaderLayout() {
        return filterHeaderLayout;
    }

    @Override
    public String getSql() {
        return null;
    }

    @Override
    public List<FilterItem> getFilterContent() {
        return filterItemList;
    }

    @Override
    public void onClick(View v) {
        context.startActivity(new Intent(context, DefaultListFilterActivity.class));
    }

    private List<FilterItem> createFilterContent() {
        List<FilterItem> filterItems = new ArrayList<FilterItem>();
        filterItems.add(new DefaultActivityFilterItem(context, context.getString(R.string.filter_item_classifier), null));
        filterItems.add(new DefaultActivityFilterItem(context, context.getString(R.string.filter_item_region),
                DefaultExpandableListFilterActivity.class, null));
        filterItems.add(new DefaultSeekBarFilterItem(context));
        return filterItems;
    }
}
