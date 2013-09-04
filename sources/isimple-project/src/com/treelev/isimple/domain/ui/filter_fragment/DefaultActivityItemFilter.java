package com.treelev.isimple.domain.ui.filter_fragment;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.filter.DefaultListFilterActivity;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.fragments.filters.FilterFragment;

import java.util.List;

public abstract class DefaultActivityItemFilter extends ItemFilter {

    protected FilterItemData[] mFilterData;

    public DefaultActivityItemFilter(LayoutInflater inflater, FilterFragment filter, FilterItemData[] filterData) {
        super(inflater, filter);
        mFilterData = filterData;
        initControl();
    }

    @Override
    public void reset() {
        for(FilterItemData itemData : mFilterData){
            itemData.setChecked(false);
        }
        ((Button)mView).setText(mFilter.getString(R.string.lbl_year_item));
        ((Button)mView).setTextColor(Color.LTGRAY);
    }

    @Override
    protected View createView() {
        return mInflater.inflate(R.layout.button_item_filter, null);
    }

    @Override
    protected void initControl() {
        mView.findViewById(R.id.button_item_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(mFilter.getActivity(), DefaultListFilterActivity.class);
                    DefaultListFilterActivity.putFilterData(intent, mFilterData, mFilter.getCategory());
                    mFilter.getActivity().startActivityForResult(intent, mRequestCode);
                    mFilter.getActivity().overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
            }
        });
        ((Button)mView).setText(mFilter.getString(R.string.lbl_year_item));
    }


    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == mRequestCode){
            mFilterData = DefaultListFilterActivity.getFilterData(data);
            StringBuilder stringBuilder = new StringBuilder();
            boolean isChecked = false;
            for(FilterItemData itemData : mFilterData){
                if(itemData.isChecked()){
                    stringBuilder.append(itemData.getName());
                    stringBuilder.append(", ");
                    isChecked = true;
                }
            }
            String label = stringBuilder.toString();
            if(isChecked && label.length() > 0){
                ((Button)mView).setText(formatLabel(label)); //delete separator(comma and space)
                ((Button)mView).setTextColor(Color.BLACK);
            } else {
                ((Button)mView).setText(mFilter.getString(R.string.lbl_year_item));
                ((Button)mView).setTextColor(Color.LTGRAY);
            }
        }
    }

    private String formatLabel(String label){
//TODO
        int maxLength = 36;
        String result;
        if(label.length() > maxLength){
            result = String.format("%s...", label.substring(0, maxLength));
        } else {
            result = label;
        }
        return result;
    }

}
