package com.treelev.isimple.filter;

import android.content.Context;
import android.view.View;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.DefaultListFilterActivity;
import com.treelev.isimple.domain.ui.DefaultActivityFilterItem;
import com.treelev.isimple.domain.ui.DefaultSeekBarFilterItem;
import com.treelev.isimple.domain.ui.FilterItem;
import org.holoeverywhere.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

public class WineFilter implements Filter {

    private Context context;
    private View filterHeaderLayout;
    private List<FilterItem> filterItemList;

    public WineFilter(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        filterHeaderLayout = inflater.inflate(R.layout.category_wine_filter_header_layout);
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

    private List<FilterItem> createFilterContent() {
        List<FilterItem> filterItems = new ArrayList<FilterItem>();
        filterItems.add(new DefaultActivityFilterItem(context, context.getString(R.string.filter_item_sweetness), DefaultListFilterActivity.class));
        filterItems.add(new DefaultSeekBarFilterItem(context));
        filterItems.add(new DefaultActivityFilterItem(context, context.getString(R.string.filter_item_region), DefaultListFilterActivity.class));
        filterItems.add(new DefaultActivityFilterItem(context, context.getString(R.string.filter_item_year), DefaultListFilterActivity.class));
        return filterItems;
    }
}
