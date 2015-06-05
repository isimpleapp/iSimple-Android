package com.treelev.isimple.fragments.filters;

import android.view.LayoutInflater;
import com.treelev.isimple.domain.ui.filter_fragment.PriceItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.WaterItemFilter;

public class WaterFilter extends FilterFragment {

    private PriceItemFilter mPriceItemFilter;


    public void initFilterItems(int min, int max) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        WaterItemFilter waterItem = new WaterItemFilter(inflater, this);
        addItemFilter(waterItem);
        addHorizontalSeparator();
        PriceItemFilter priceItem = new PriceItemFilter(inflater, min, max, true);
        priceItem.setIsWater(true);
        addItemFilter(priceItem);
        mPriceItemFilter = priceItem;
        mPriceItemFilter.setEnable(true);
//        addControlView();
        addWaterControlView();
//        addSortControl();
        addWaterSortControl();
    }

    @Override
    protected boolean isGroup() {
        return true;
    }
}
