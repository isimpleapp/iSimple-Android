package com.treelev.isimple.domain.ui.filter_fragment;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.filter.ExpandableListFilterActivity;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.fragments.filters.FilterFragment;

import java.util.Map;

public abstract class ExpandableActivityItemFilter extends ItemFilter{

    protected FilterItemData[] mGroupData;
    protected Map<String, FilterItemData[]> mChildData;

    public ExpandableActivityItemFilter(LayoutInflater inflater, FilterFragment filter, boolean interactive,Map<String, FilterItemData[]> childData) {
        super(inflater, filter, interactive);
        initControl();
        initData(childData);
    }

    private void initData(Map<String, FilterItemData[]> childData){
        String[] groups = childData.keySet().toArray(new String[0]);
        mGroupData = new FilterItemData[groups.length];
        for (int i = 0; i < mGroupData.length; i++) {
            mGroupData[i] = new FilterItemData(groups[i]);
        }
        mChildData = childData;
    }

    @Override
    public void reset() {
        for(FilterItemData item : mGroupData) {
            item.setChecked(false);
        }
        for(FilterItemData[] items : mChildData.values()){
            for(FilterItemData item : items) {
                item.setChecked(false);
            }
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
                Intent intent = new Intent(mFilter.getActivity(), ExpandableListFilterActivity.class);
                ExpandableListFilterActivity.putFilterData(intent, mGroupData, mChildData, mFilter.getCategory());
                mFilter.getActivity().startActivityForResult(intent, mRequestCode);
                mFilter.getActivity().overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);

            }
        });
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == mRequestCode){
            mGroupData = ExpandableListFilterActivity.getGroupData(data);
            mChildData = ExpandableListFilterActivity.getChildData(data, mGroupData);
//TODO
            ((Button)mView).setTextColor(Color.BLACK);
            onChangeStateItemFilter();
        }
    }
}
