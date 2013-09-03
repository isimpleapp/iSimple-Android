package com.treelev.isimple.domain.ui.filter_fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.filter.DefaultListFilterActivity;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.fragments.filters.FilterFragment;

public class YearItemFilter extends ItemFilter {

    private FilterItemData[] mFilterData;


    protected YearItemFilter(LayoutInflater inflater, FilterFragment filter, FilterItemData[] filterData) {
        super(inflater, filter);
        mFilterData = filterData;
    }

    @Override
    public void reset() {
//TODO
        ((Button)mView).setText(mFilter.getString(R.string.lbl_year_item));
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
                if(mFilterData != null) {
                    Intent intent = new Intent(mFilter.getActivity(), DefaultListFilterActivity.class);
                    DefaultListFilterActivity.putFilterData(intent, mFilterData, mFilter.getCategory());
                    mFilter.getActivity().startActivityForResult(intent, mRequestCode);
                    mFilter.getActivity().overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
                }
            }
        });
        ((Button)mView).setText(mFilter.getString(R.string.lbl_year_item));
    }

    @Override
    public String getWhereClause() {
        StringBuilder sqlBuilder = new StringBuilder();
        if (mFilterData != null) {
            for (FilterItemData item : mFilterData) {
                if (item.isChecked()) {
                    if(sqlBuilder.length() > 0) {
                        sqlBuilder.append(" OR ");
                    }
                    sqlBuilder.append(String.format("item.year=%s", item.getName()));
                }
            }
        }
        if (sqlBuilder.length() > 0) {
            sqlBuilder.insert(0, '(');
            sqlBuilder.append(')');
        }

        return sqlBuilder.toString();
    }


}
