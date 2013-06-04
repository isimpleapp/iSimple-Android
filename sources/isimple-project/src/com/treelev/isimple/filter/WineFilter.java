package com.treelev.isimple.filter;

import android.content.Context;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.*;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.Sweetness;

import java.util.ArrayList;
import java.util.List;

public class WineFilter extends Filter {

    private List<FilterItem> filterItemList;

    public WineFilter(Context context, int currentCategory) {
        super(context, currentCategory);
        filterItemList = createFilterContent();
    }

    @Override
    public List<FilterItem> getFilterContent() {
        return filterItemList;
    }

    private List<FilterItem> createFilterContent() {
        List<FilterItem> filterItems = new ArrayList<FilterItem>();
        filterItems.add(new WineColorFilterItem(getContext()));
        filterItems.add(new DefaultActivityFilterItem(
                getContext(), getContext().getString(R.string.filter_item_sweetness),
                FilterItemData.createFromPresentable(Sweetness.getWineSweetness()),
                SweetnessSqlWhereClauseBuilder.INSTANCE, currentCategory));
        filterItems.add(new DefaultActivityFilterItem(
                getContext(), getContext().getString(R.string.filter_item_year),
                FilterItemData.getAvailableYears(getContext(), DrinkCategory.WINE),
                YearSqlWhereClauseBuilder.INSTANCE, currentCategory));
        filterItems.add(new ExpandableActivityFilterItem(
                getContext(), getContext().getString(R.string.filter_item_region),
                FilterItemData.getAvailableCountryRegions(getContext(), DrinkCategory.WINE),
                RegionSqlWhereClauseBuilder.INSTANCE, currentCategory));
        filterItems.add(new DefaultSeekBarFilterItem(getContext(), "item.price", this));
        return filterItems;
    }
}
