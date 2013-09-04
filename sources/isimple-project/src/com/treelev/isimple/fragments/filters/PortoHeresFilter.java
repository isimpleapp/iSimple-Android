package com.treelev.isimple.fragments.filters;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.domain.ui.filter_fragment.DefaultActivityItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.PortoHeresColorItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.PortoHeresTypeItemFilter;
import com.treelev.isimple.domain.ui.filter_fragment.YearItemFilter;

import java.util.List;

public class PortoHeresFilter extends FilterFragment {

    public void initFilterItems(int min, int max, FilterItemData[] dataYear){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        PortoHeresColorItemFilter colorItem = new PortoHeresColorItemFilter(inflater, this);
        addItemFilter(colorItem , new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getActivity().getResources().getDisplayMetrics())));
        PortoHeresTypeItemFilter typeItem = new PortoHeresTypeItemFilter(inflater, this);
        addItemFilter(typeItem);

        YearItemFilter yearItem = new YearItemFilter(inflater, this, dataYear);
        addItemFilter(yearItem);

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
