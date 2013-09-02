package com.treelev.isimple.domain.ui.filter_fragment;


import android.view.LayoutInflater;
import com.treelev.isimple.fragments.filters.FilterFragment;

public abstract class ItemInteractiveFilter extends ItemFilter{

    protected FilterFragment mFilter;

    protected ItemInteractiveFilter(LayoutInflater inflater, FilterFragment filter) {
        super(inflater);
        mFilter = filter;
    }

    protected void onChangeStateItemFilter(){
        mFilter.onChangeFilterState();
    }
}
