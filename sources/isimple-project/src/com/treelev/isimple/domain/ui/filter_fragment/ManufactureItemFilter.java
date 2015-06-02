package com.treelev.isimple.domain.ui.filter_fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.Button;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.fragments.filters.FilterFragment;

public class ManufactureItemFilter extends DefaultActivityItemFilter{


    public ManufactureItemFilter(LayoutInflater inflater, FilterFragment filter, boolean interactive, FilterItemData[] filterData) {
        super(inflater, filter, interactive, filterData);
        initControl();
    }

    @Override
    protected void initControl() {
        mLabel = mFilter.getString(R.string.lbl_manufacture_item);
        super.initControl();
    }

    @Override
    public void reset() {
        super.reset();
        ((Button)mView).setText(mFilter.getString(R.string.lbl_manufacture_item));
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
                    sqlBuilder.append(String.format("item.manufacturer LIKE '%%%s%%'", item.getName().replace("'", "''")));
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
