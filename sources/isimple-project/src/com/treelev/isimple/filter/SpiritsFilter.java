package com.treelev.isimple.filter;

import android.content.Context;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.*;
import com.treelev.isimple.enumerable.item.DrinkCategory;

import java.util.ArrayList;
import java.util.List;

public class SpiritsFilter extends Filter {

    private List<FilterItem> filterItemList;

    public SpiritsFilter(Context context) {
        super(context);
        filterItemList = createFilterContent();
    }

    @Override
    public List<FilterItem> getFilterContent() {
        return filterItemList;
    }

    private List<FilterItem> createFilterContent() {
        List<FilterItem> filterItems = new ArrayList<FilterItem>();
        filterItems.add(new ExpandableActivityFilterItem(getContext(),
                getContext().getString(R.string.filter_item_classifier),
                FilterItemData.getAvailableClassifications(getContext(), DrinkCategory.SPIRITS),
                ClassificationSqlWhereClauseBuilder.INSTANCE));
        filterItems.add(new ExpandableActivityFilterItem(getContext(),
                getContext().getString(R.string.filter_item_region),
                FilterItemData.getAvailableCountryRegions(getContext(), DrinkCategory.SPIRITS),
                RegionSqlWhereClauseBuilder.INSTANCE));
        filterItems.add(new DefaultSeekBarFilterItem(getContext(), "item.price"));
        return filterItems;
    }
}
