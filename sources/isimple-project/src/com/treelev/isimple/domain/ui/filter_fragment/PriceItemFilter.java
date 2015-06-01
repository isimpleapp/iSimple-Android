package com.treelev.isimple.domain.ui.filter_fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.treelev.isimple.R;
import com.treelev.isimple.views.RangeSeekBar;
import com.treelev.isimple.views.RangeSeekBarBlue;

public class PriceItemFilter extends ItemFilter{

    private int mMin;
    private int mMax;
    private int mMinCurrent;
    private int mMaxCurrent;
    private boolean mEnable;
    RangeSeekBar<Integer> mRangeSeekBarr;
    RangeSeekBarBlue<Integer> mRangeSeekBarBlue;
    private boolean mReset;
    private boolean isWater = false;

    public PriceItemFilter(LayoutInflater inflater, int min, int max) {
        super(inflater);
        mMin = min;
        mMax = max;
        mMaxCurrent = mMax;
        mMinCurrent = mMin;
        initControl();
    }
    
    public PriceItemFilter(LayoutInflater inflater, int min, int max, boolean isWater) {
        super(inflater);
        mMin = min;
        mMax = max;
        mMaxCurrent = mMax;
        mMinCurrent = mMin;
        this.isWater = isWater;
        initControl();
    }

    @Override
    public void reset() {
        mRangeSeekBarr.setSelectedMinValue(mMin);
        mRangeSeekBarr.setSelectedMaxValue(mMax);
        mRangeSeekBarr.invalidate();
        mReset = true;
    }
    
    public void setIsWater(boolean isWater){
    	this.isWater = isWater;
    }

    @Override
    protected View createView() {
    	if (isWater){
    		return mInflater.inflate(R.layout.category_filter_seekbar_item_water_layout, null);
    	} else {
    		return mInflater.inflate(R.layout.category_filter_seekbar_item_layout, null);
    	}
    }
    
    @Override
    protected void initControl() {
    	if (isWater){
        	mRangeSeekBarBlue = new RangeSeekBarBlue<Integer>(mMin, mMax, mInflater.getContext());
        	mRangeSeekBarBlue.setOnRangeSeekBarChangeListener(new RangeSeekBarBlue.OnRangeSeekBarChangeListener<Integer>() {
                @Override
                public void onRangeSeekBarValuesChanged(RangeSeekBarBlue<?> bar, Integer minValue, Integer maxValue) {
                    mMinCurrent = minValue;
                    mMaxCurrent = maxValue;
                    mReset = false;
                }
            });
            ((LinearLayout)mView).addView(mRangeSeekBarBlue, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                            mInflater.getContext().getResources().getDisplayMetrics())));
            mReset = true;
    	} else {
    		mRangeSeekBarr = new RangeSeekBar(mMin, mMax, mInflater.getContext());
    		mRangeSeekBarr.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
    			@Override
    			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
    				mMinCurrent = minValue;
    				mMaxCurrent = maxValue;
    				mReset = false;
    			}
    		});
    		((LinearLayout)mView).addView(mRangeSeekBarr, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
    				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
    						mInflater.getContext().getResources().getDisplayMetrics())));
    		mReset = true;
    	}
    }

    @Override
    public String getWhereClause() {
        return mEnable && !mReset ? String.format("(item.price >= %d AND item.price <= %d)", mMinCurrent, mMaxCurrent) : "";
    }

    public boolean isReset(){
        return mReset;
    }

    public void setEnable(boolean enable){
        mEnable = enable;
    }
}
