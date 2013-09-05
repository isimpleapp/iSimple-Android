package com.treelev.isimple.fragments.filters;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.domain.ui.filter_fragment.*;

import java.util.List;
import java.util.Map;

public class PortoHeresFilter extends FilterFragment {

    public void initFilterItems(int min, int max, FilterItemData[] dataSweetnes, FilterItemData[] dataYear, Map<String, FilterItemData[]> dataRegion){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        PortoHeresColorItemFilter colorItem = new PortoHeresColorItemFilter(inflater, this);
        addItemFilter(colorItem , new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getActivity().getResources().getDisplayMetrics())));
        PortoHeresTypeItemFilter typeItem = new PortoHeresTypeItemFilter(inflater, this);
        addItemFilter(typeItem);

        initExtendFilter();

        SweetnesItemFilter sweetnesItem = new SweetnesItemFilter(inflater, this, dataSweetnes);
        addItemFilterExtend(sweetnesItem);
        CountryRegionItemFilter countryRegionItem = new CountryRegionItemFilter(inflater, this, dataRegion);
        addItemFilterExtend(countryRegionItem);
        PriceItemFilter priceItem = new PriceItemFilter(inflater, min, max);
        addItemFilterExtend(priceItem);
        YearItemFilter yearItem = new YearItemFilter(inflater, this, dataYear);
        addItemFilterExtend(yearItem);

        addSortControl();
    }


    @Override
    public void onChangeFilterState() {
        if(mListener != null){
            //TODO flag true
            mListener.onChangeFilterState(getWhereClause(), true);
        }
    }
}
