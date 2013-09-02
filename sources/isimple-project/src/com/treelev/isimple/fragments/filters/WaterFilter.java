package com.treelev.isimple.fragments.filters;

import android.view.LayoutInflater;
import com.treelev.isimple.domain.ui.filter_fragment.PriceItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.WaterItemFilter;

public class WaterFilter extends FilterFragment {

    private PriceItemFilter mPriceItemFilter;


    public void initFilterItems(int min, int max) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        WaterItemFilter waterItem = new WaterItemFilter(inflater, this);
        mLayout.addView(waterItem.getView());
        mItems.add(waterItem);
        PriceItemFilter priceItem = new PriceItemFilter(inflater, min, max);
        mLayout.addView(priceItem.getView());
        mPriceItemFilter = priceItem;
        priceItem.setEnable(true);
        mItems.add(priceItem);
        addControlView();
        addSortControl();
    }

    @Override
    public void onChangeFilterState() {
        if(mListener != null){
            mListener.onChangeFilterState(getWhereClause(), mPriceItemFilter.isInitialState());
        }
    }
}
