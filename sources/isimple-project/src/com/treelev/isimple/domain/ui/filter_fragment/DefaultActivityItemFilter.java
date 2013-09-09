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

public abstract class DefaultActivityItemFilter extends ItemFilter {

    protected FilterItemData[] mFilterData;

    protected String mLabel;

    public DefaultActivityItemFilter(LayoutInflater inflater, FilterFragment filter, boolean interactive, FilterItemData[] filterData) {
        super(inflater, filter, interactive);
        mFilterData = filterData;
    }

    @Override
    public void reset() {
        for(FilterItemData itemData : mFilterData){
            itemData.setChecked(false);
        }
        ((Button)mView).setTextColor(mFilter.getResources().getColor(R.color.product_text_color));
        ((Button)mView).setText(mLabel);
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
        ((Button)mView).setText(mLabel);
    }


    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == mRequestCode){
            mFilterData = DefaultListFilterActivity.getFilterData(data);
            StringBuilder stringBuilder = new StringBuilder();
            for(FilterItemData itemData : mFilterData){
                if(itemData.isChecked()){
                    stringBuilder.append(itemData.getName());
                    stringBuilder.append(", ");
                }
            }
            String label = stringBuilder.toString();
            if(label.length() > 0){
                ((Button)mView).setText(label.substring(0, label.length() - 2)); //delete separator(comma and space)
                ((Button)mView).setTextColor(Color.BLACK);
            } else {
                ((Button)mView).setText(mLabel);
                ((Button)mView).setTextColor(mFilter.getResources().getColor(R.color.product_text_color));
            }
            onChangeStateItemFilter();
        }
    }

}
