package com.treelev.isimple.fragments.filters;


import android.support.v4.app.Fragment;

public abstract class FilterFragment extends Fragment {

    public enum FilterType{PortoHeres, Sake, Sparking, Spirits, Water, Wine}

    public interface OnChangeStateListener{
        public void onChangeFilterState(String whereClause);
    }

    public static FilterFragment newInstance(FilterType type){
        FilterFragment filter = null;
        switch (type){
            case PortoHeres:
                filter = new PortoHeresFilter();
                break;
            case Sake:
                filter = new SakeFilter();
                break;
            case Sparking:
                filter = new SparkingFilter();
                break;
            case Spirits:
                filter = new SparkingFilter();
                break;
            case Water:
                filter = new WaterFilter();
                break;
            case Wine:
                filter = new WineFilter();
                break;
        }
        return filter;
    }

    private OnChangeStateListener mListener;

    protected void onChangeFilterState(){
        if(mListener != null){
            mListener.onChangeFilterState(getWhereClause());
        }
    }

    protected abstract String getWhereClause();

    protected abstract void resetFilter();
}

