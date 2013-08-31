package com.treelev.isimple.fragments.filters;

import android.view.LayoutInflater;
import com.treelev.isimple.domain.ui.filter_fragment.PriceItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.WaterItemFilter;

public class WaterFilter extends FilterFragment {

    @Override
    protected void initFilterItems() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        WaterItemFilter waterItem = new WaterItemFilter(inflater);
        mLayout.addView(waterItem.getView());
        mItems.add(waterItem);
        PriceItemFilter priceItem = new PriceItemFilter(inflater, mMinPrice, mMaxPrice);
        mLayout.addView(priceItem.getView());
        mItems.add(priceItem);
        mLayout.addView(getControlView());
    }
}
