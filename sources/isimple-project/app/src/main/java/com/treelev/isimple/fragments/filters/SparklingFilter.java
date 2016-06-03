package com.treelev.isimple.fragments.filters;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.domain.ui.filter_fragment.CountryRegionItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.ManufactureItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.PriceItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.SweetnesItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.WineColorItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.YearItemFilter;

import java.util.Map;

public class SparklingFilter extends FilterFragment{

    private PriceItemFilter mPriceItem;
    private YearItemFilter mYearItem;

    public void initFilterItems(int min, int max, FilterItemData[] dataManufacture, FilterItemData[] dataSweetnes, Map<String, FilterItemData[]> dataRegion, FilterItemData[] dataYear){
        LayoutInflater inflater = getActivity().getLayoutInflater();

        WineColorItemFilter wineColorItem = new WineColorItemFilter(inflater, this);
        addItemFilter(wineColorItem, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getActivity().getResources().getDisplayMetrics())));
        ManufactureItemFilter manufactureItem = new ManufactureItemFilter(inflater, this, true, dataManufacture);
        addItemFilter(manufactureItem);

        initExtendFilter();
        SweetnesItemFilter sweetnesItem = new SweetnesItemFilter(inflater, this, false, dataSweetnes);
        addItemFilterExtend(sweetnesItem);
        addExtendHorizontalSeparator();
        CountryRegionItemFilter countryRegionItem = new CountryRegionItemFilter(inflater, this, false, dataRegion);
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
        return mPriceItem.isReset() && mYearItem.isReset();
    }
}
