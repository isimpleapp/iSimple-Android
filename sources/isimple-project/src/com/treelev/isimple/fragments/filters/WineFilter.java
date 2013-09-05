package com.treelev.isimple.fragments.filters;

import android.view.LayoutInflater;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.domain.ui.filter_fragment.CountryRegionItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.WineColorItemFilter;

import java.util.Map;

public class WineFilter extends FilterFragment{


    public void initFilterItems(int min, int max, Map<String, FilterItemData[]> dataRegion) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        WineColorItemFilter wineColorItem = new WineColorItemFilter(inflater);
        addItemFilter(wineColorItem);

        CountryRegionItemFilter countryRegionItem = new CountryRegionItemFilter(inflater, this, dataRegion);
        addItemFilter(countryRegionItem);

        initExtendFilter();

        addSortControl();
    }

    @Override
    public void onChangeFilterState() {

    }
}
