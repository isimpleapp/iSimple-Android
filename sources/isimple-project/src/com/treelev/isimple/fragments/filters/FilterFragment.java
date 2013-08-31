package com.treelev.isimple.fragments.filters;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter_fragment.ItemFilter;
import org.holoeverywhere.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterFragment extends Fragment {

    public enum FilterType{PortoHeres, Sake, Sparkling, Spirits, Water, Wine}

    public interface OnChangeStateListener{
        public void onChangeFilterState(String whereClause);
    }

    private OnChangeStateListener mListener;
    protected List<ItemFilter> mItems;
    protected int mMinPrice;
    protected int mMaxPrice;
    protected LinearLayout mLayout;

    public static FilterFragment newInstance(FilterType type, int minPrice, int maxPrice){
        FilterFragment filter = null;
        switch (type){
            case PortoHeres:
                filter = new PortoHeresFilter();
                break;
            case Sake:
                filter = new SakeFilter();
                break;
            case Sparkling:
                filter = new SparklingFilter();
                break;
            case Spirits:
                filter = new SparklingFilter();
                break;
            case Water:
                filter = new WaterFilter();
                break;
            case Wine:
                filter = new WineFilter();
                break;
        }
        filter.mItems = new ArrayList<ItemFilter>();
        filter.mMaxPrice = maxPrice;
        filter.mMinPrice= minPrice;
        return filter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_fragment_layout, container, false);
        mLayout = (LinearLayout)view;
        initFilterItems();
        return view;
    }

    public void setOnChangeFilterListener(OnChangeStateListener listener){
        mListener = listener;
    }

    protected void onChangeFilterState(){
        if(mListener != null){
            mListener.onChangeFilterState(getWhereClause());
        }
    }

    protected  String getWhereClause(){
        StringBuilder whereClause = new StringBuilder();
        for(ItemFilter item : mItems){
            whereClause.append(item.getWhereClause());
        }
        return whereClause.toString();
    }

    protected void resetFilter(){
        for(ItemFilter item : mItems){
            item.reset();
        }
    }

    protected View getControlView(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewControl = inflater.inflate(R.layout.filter_controls, null);
        viewControl.findViewById(R.id.reset_butt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilter();
            }
        });
        viewControl.findViewById(R.id.search_butt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeFilterState();
            }
        });
        return viewControl;
    }

    protected  abstract void initFilterItems();
}

