package com.treelev.isimple.filter;

import android.content.Context;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.*;
import com.treelev.isimple.enumerable.item.DrinkCategory;

import java.util.ArrayList;
import java.util.List;

public class WaterFilter extends Filter {

    private List<FilterItem> filterItemList;

    public WaterFilter(Context context) {
        super(context);
        filterItemList = createFilterContent();
    }

    @Override
    public List<FilterItem> getFilterContent() {
        return filterItemList;
    }

    private List<FilterItem> createFilterContent() {
        List<FilterItem> filterItems = new ArrayList<FilterItem>();
        filterItems.add(new ExpandableActivityFilterItem(
                getContext(), getContext().getString(R.string.filter_item_type),
                FilterItemData.getAvailableClassifications(getContext(), DrinkCategory.WATER),
                ClassificationSqlWhereClauseBuilder.INSTANCE));
        filterItems.add(new ExpandableActivityFilterItem(
                getContext(), getContext().getString(R.string.filter_item_region),
                FilterItemData.getAvailableCountryRegions(getContext(), DrinkCategory.WATER),
                RegionSqlWhereClauseBuilder.INSTANCE));
        filterItems.add(new DefaultSeekBarFilterItem(getContext(), "item.price", this));
        return filterItems;
    }
}
