package com.treelev.isimple.filter;

import android.content.Context;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.DefaultSeekBarFilterItem;
import com.treelev.isimple.domain.ui.filter.ExpandableActivityFilterItem;
import com.treelev.isimple.domain.ui.filter.FilterItem;
import com.treelev.isimple.domain.ui.filter.FilterItemData;

import java.util.ArrayList;
import java.util.List;

public class WaterFilter extends Filter {

//    private Context context;
//    private View filterHeaderLayout;
    private List<FilterItem> filterItemList;

    public WaterFilter(Context context) {
        super(context);
//        this.context = context;
//        LayoutInflater inflater = LayoutInflater.from(context);
//        filterHeaderLayout = inflater.inflate(R.layout.category_spirits_water_filter_header_layout);
//        filterHeaderLayout.setOnClickListener(this);
        filterItemList = createFilterContent();
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
        filterItems.add(new ExpandableActivityFilterItem(getContext(), getContext().getString(R.string.filter_item_region),
                FilterItemData.getAvailableCountryRegions(getContext(), R.id.category_wine_butt)));
        filterItems.add(new DefaultSeekBarFilterItem(getContext()));
        return filterItems;
    }
}
