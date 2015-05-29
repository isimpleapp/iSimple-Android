package com.treelev.isimple.fragments.filters;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        public void onChangeFilterState(String whereClause, boolean group, boolean clickFind);
    }

    protected OnChangeStateListener mListener;
    protected List<ItemFilter> mItems;
    protected LinearLayout mLayout;
    protected LinearLayout mLayoutExtend;
    private int mSortBy;
    private int mCategory;
    private View mSortControl;
    private boolean mFind;

    protected abstract boolean isGroup();

    public void onChangeFilterState(){
        if(mListener != null){
            mListener.onChangeFilterState(getWhereClause(), isGroup(), mFind);
        }
    }


    public FilterFragment(){
        mItems = new ArrayList<ItemFilter>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_fragment_layout, container, false);
        mLayout = (LinearLayout)view;
        return view;
    }

    public void setVisibleSortControl(boolean visible){
        if(mSortControl != null){
            mSortControl.setVisibility( visible ? View.VISIBLE : View.GONE);
        }
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

    protected void addItemFilterExtend(ItemFilter item){
        mLayoutExtend.addView(item.getView());
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
                mFind = true;
                onChangeFilterState();
                mFind = false;
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
        mSortControl = getSortControl();
        mSortBy = ProxyManager.SORT_NAME_AZ;
        mLayout.addView(mSortControl, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getActivity().getResources().getDisplayMetrics())));
    }

    protected void addControlView(){
        mLayout.addView(getControlView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getActivity().getResources().getDisplayMetrics())));
    }


    protected void addControlViewExtendFilter(){
        mLayoutExtend.addView(getControlView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getActivity().getResources().getDisplayMetrics())));
    }

    protected void onShowExtendFilter(){

    }

    protected void initExtendFilter(){
        LinearLayout extendFilter = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.filter_extend_layout, null);
        mLayoutExtend = (LinearLayout)extendFilter.findViewById(R.id.content_extend_filter);
        final View separator = extendFilter.findViewById(R.id.separator_extend_filter);
        extendFilter.findViewById(R.id.show_extend_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayoutExtend.setVisibility(View.VISIBLE);
                view.setOnClickListener(null);
                view.setVisibility(View.GONE);
                separator.setVisibility(View.VISIBLE);
                onShowExtendFilter();
            }
        });
        mLayout.addView(extendFilter);
    }

    protected void addHorizontalSeparator(){
      mLayout.addView(getActivity().getLayoutInflater().inflate(R.layout.separator_view, null));
    }

    protected void addExtendHorizontalSeparator(){
        mLayoutExtend.addView(getActivity().getLayoutInflater().inflate(R.layout.separator_view, null));
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for(ItemFilter item : mItems){
            item.onResult(requestCode, resultCode, data);
        }
    }
}

