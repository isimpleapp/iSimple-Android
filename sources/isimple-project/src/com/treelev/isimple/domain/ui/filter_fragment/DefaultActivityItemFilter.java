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

    public DefaultActivityItemFilter(LayoutInflater inflater, FilterFragment filter, boolean interactive, FilterItemData[] filterData) {
        super(inflater, filter, interactive);
        mFilterData = filterData;
        initControl();
    }

    @Override
    public void reset() {
        for(FilterItemData itemData : mFilterData){
            itemData.setChecked(false);
        }
        ((Button)mView).setTextColor(mFilter.getResources().getColor(R.color.product_text_color));
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
                ((Button)mView).setText(formatLabel(label)); //delete separator(comma and space)
                ((Button)mView).setTextColor(Color.BLACK);
            } else {
                ((Button)mView).setText(mFilter.getString(R.string.lbl_year_item));
                ((Button)mView).setTextColor(Color.LTGRAY);
            }
            onChangeStateItemFilter();
        }
    }

    private String formatLabel(String label){
//TODO
        int maxLength = 27;
        String result;
        if(label.length() > maxLength){
            result = label.substring(0, maxLength);
            int position = result.indexOf(",", result.length() - 2);
            result = position > 0 ?
                    String.format("%s", result.substring(0, position))
                    :
                    String.format("%s...", result);
        } else {
            int position = label.indexOf(",", label.length() - 2);
            result = String.format("%s", label.substring(0, position));
        }
        return result;
    }

}
