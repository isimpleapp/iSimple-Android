package com.treelev.isimple.fragments.filters;

import android.view.LayoutInflater;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.domain.ui.filter_fragment.ClassificationItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.PriceItemFilter;

import java.util.Map;

public class SakeFilter extends FilterFragment{

    private PriceItemFilter mPriceItem;

    public void initFilterItems(int min, int max, Map<String, FilterItemData[]> dataClassification){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ClassificationItemFilter styleSakeItem =
                new ClassificationItemFilter(inflater, getString(R.string.lbl_style_sake_item), true, this, dataClassification);
        addItemFilter(styleSakeItem);
        addHorizontalSeparator();
        PriceItemFilter priceItem = new PriceItemFilter(inflater, min, max);
        addItemFilter(priceItem);
        mPriceItem = priceItem;
        mPriceItem.setEnable(true);
        addControlView();
        addSortControl();
    }

    @Override
    protected boolean isGroup() {
        return mPriceItem.isReset();
    }
}
