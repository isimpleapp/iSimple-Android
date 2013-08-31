package com.treelev.isimple.domain.ui.filter_fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.treelev.isimple.R;
import com.treelev.isimple.views.RangeSeekBar;

public class PriceItemFilter extends ItemFilter{

    private int mMin;
    private int mMax;
    private int mMinCurrent;
    private int mMaxCurrent;
    RangeSeekBar<Integer> mRangeSeekBarr;

    public PriceItemFilter(LayoutInflater inflater, int min, int max) {
        super(inflater);
    }

    @Override
    public void reset() {
        mRangeSeekBarr.setSelectedMinValue(mMin);
        mRangeSeekBarr.setSelectedMaxValue(mMax);
        mRangeSeekBarr.invalidate();
    }

    @Override
    protected View createView() {
        return mInflater.inflate(R.layout.category_filter_seekbar_item_layout, null);
    }

    @Override
    protected void initControl() {
        mRangeSeekBarr = new RangeSeekBar(mMin, mMax, mInflater.getContext());
        mRangeSeekBarr.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                mMinCurrent = minValue;
                mMaxCurrent = maxValue;
            }
        });
        ((LinearLayout)mView).addView(mRangeSeekBarr, new org.holoeverywhere.widget.LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        mInflater.getContext().getResources().getDisplayMetrics())));
    }

    @Override
    public String getWhereClause() {
        return String.format("(item.price >= %d AND item.price <= %d)", mMinCurrent, mMaxCurrent);
    }
}
