package com.treelev.isimple.fragments.filters;

import android.view.LayoutInflater;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.domain.ui.filter_fragment.PriceItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.StyleSakeItemFilter;

import java.util.Map;

public class SakeFilter extends FilterFragment{

    public void initFilterItems(int min, int max, Map<String, FilterItemData[]> dataStyle){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        StyleSakeItemFilter styleSakeItem = new StyleSakeItemFilter(inflater, this, dataStyle);
        addItemFilter(styleSakeItem);
        PriceItemFilter priceItem = new PriceItemFilter(inflater, min, max);
        addItemFilter(priceItem);
        addControlView();
        addSortControl();
    }

    @Override
    public void onChangeFilterState() {

    }


}
