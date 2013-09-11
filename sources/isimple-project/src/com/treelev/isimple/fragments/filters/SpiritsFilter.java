package com.treelev.isimple.fragments.filters;

import android.view.LayoutInflater;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.domain.ui.filter_fragment.*;
import com.treelev.isimple.domain.ui.filter_fragment.CountryRegionItemFilter;

import java.util.Map;

public class SpiritsFilter extends FilterFragment {

    private PriceItemFilter mPriceItem;
    private YearItemFilter mYearItem;

    public void initFilterItems(int min, int max, Map<String, FilterItemData[]> dataClassification,  FilterItemData[] dataYear){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ClassificationItemFilter classificationItem =
                new ClassificationItemFilter(inflater, getString(R.string.lbl_type_drink), true, this,  dataClassification);
        addItemFilter(classificationItem);

        initExtendFilter();
        PriceItemFilter priceItem = new PriceItemFilter(inflater, min, max);
        addItemFilterExtend(priceItem);
        mPriceItem = priceItem;
        addExtendHorizontalSeparator();
        YearItemFilter yearItem = new YearItemFilter(inflater, this, dataYear);
        addItemFilterExtend(yearItem);
        mYearItem = yearItem;

        addControlViewExtendFilter();
        addSortControl();

    }

    @Override
    protected void onShowExtendFilter() {
        super.onShowExtendFilter();
        mPriceItem.setEnable(true);
    }

    @Override
    protected boolean isGroup() {
        return mPriceItem.isReset() || !mYearItem.isReset();
    }
}
