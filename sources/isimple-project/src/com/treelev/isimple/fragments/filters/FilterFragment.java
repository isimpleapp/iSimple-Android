package com.treelev.isimple.fragments.filters;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter_fragment.ItemFilter;
import com.treelev.isimple.utils.managers.ProxyManager;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterFragment extends Fragment {

    public enum FilterType{PortoHeres, Sake, Sparkling, Spirits, Water, Wine}

    public interface OnChangeStateListener{
        public void onChangeFilterState(String whereClause, boolean group);
    }

    protected OnChangeStateListener mListener;
    protected List<ItemFilter> mItems;
    protected LinearLayout mLayout;
    private int mSortBy;
    private int mCategory;

    public FilterFragment(){
        mItems = new ArrayList<ItemFilter>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_fragment_layout, container, false);
        mLayout = (LinearLayout)view;
        return view;
    }

    public void setOnChangeFilterListener(OnChangeStateListener listener){
        mListener = listener;
    }

    public void setCategory(int category){
        mCategory = category;
    }

    public int getCategory(){
        return mCategory;
    }

    public abstract void onChangeFilterState();

    public String getWhereClause(){
        StringBuilder sqlBuilder = new StringBuilder();
        for(ItemFilter item : mItems){
            if (!TextUtils.isEmpty(item.getWhereClause())) {
                if (sqlBuilder.length() > 0) {
                    sqlBuilder.append(" AND ");
                }
                sqlBuilder.append(item.getWhereClause());
            }
        }
        return sqlBuilder.toString();
    }

    public int getSortBy(){
        return mSortBy;
    }

    protected void addItemFilter(ItemFilter item){
        mLayout.addView(item.getView());
        mItems.add(item);
    }

    protected void addItemFilter(ItemFilter item, LinearLayout.LayoutParams params){
        mLayout.addView(item.getView(), params);
        mItems.add(item);
    }

    protected void resetFilter(){
        for(ItemFilter item : mItems){
            item.reset();
        }
    }

    private View getControlView(){
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

    private View getSortControl(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewSort = inflater.inflate(R.layout.sort_controls_layout, null);
        ((RadioGroup) viewSort.findViewById(R.id.sort_group)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int rgb) {
                switch (rgb) {
                    case R.id.alphabet_sort:
                        mSortBy = ProxyManager.SORT_NAME_AZ;
                        break;
                    case R.id.price_sort:
                        mSortBy = ProxyManager.SORT_PRICE_UP;
                        break;
                }
                onChangeFilterState();
            }
        });
        return viewSort;
    }

    protected void addSortControl(){
        mSortBy = ProxyManager.SORT_NAME_AZ;
        mLayout.addView(getSortControl(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getActivity().getResources().getDisplayMetrics())));
    }

    protected void addControlView(){
        mLayout.addView(getControlView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getActivity().getResources().getDisplayMetrics())));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for(ItemFilter item : mItems){
            item.onResult(requestCode, resultCode, data);
        }
    }
}

