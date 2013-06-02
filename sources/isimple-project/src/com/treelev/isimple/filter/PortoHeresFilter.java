package com.treelev.isimple.filter;

import android.content.Context;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.*;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.Sweetness;

import java.util.ArrayList;
import java.util.List;

public class PortoHeresFilter extends Filter {

    private List<FilterItem> filterItemList;

    public PortoHeresFilter(Context context) {
        super(context);
        filterItemList = createFilterContent();
    }

    @Override
    public List<FilterItem> getFilterContent() {
        return filterItemList;
    }

    private List<FilterItem> createFilterContent() {
        List<FilterItem> filterItems = new ArrayList<FilterItem>();
        filterItems.add(new PortoColorFilterItem(getContext()));
        filterItems.add(new DefaultActivityFilterItem(
                getContext(), getContext().getString(R.string.filter_item_sweetness),
                FilterItemData.createFromPresentable(Sweetness.getPortoSweetness()),
                SweetnessSqlWhereClauseBuilder.INSTANCE));
        filterItems.add(new ExpandableActivityFilterItem(
                getContext(), getContext().getString(R.string.filter_item_classifier),
                FilterItemData.getAvailableClassifications(getContext(), DrinkCategory.PORTO),
                ClassificationSqlWhereClauseBuilder.INSTANCE));
        filterItems.add(new ExpandableActivityFilterItem(
                getContext(), getContext().getString(R.string.filter_item_region),
                FilterItemData.getAvailableCountryRegions(getContext(), DrinkCategory.PORTO),
                RegionSqlWhereClauseBuilder.INSTANCE));
        filterItems.add(new DefaultSeekBarFilterItem(getContext(), "item.price"));
        return filterItems;
    }
}
