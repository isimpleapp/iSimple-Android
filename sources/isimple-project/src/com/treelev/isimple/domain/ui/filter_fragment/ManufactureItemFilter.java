package com.treelev.isimple.domain.ui.filter_fragment;

import android.view.LayoutInflater;
import android.widget.Button;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.fragments.filters.FilterFragment;

public class ManufactureItemFilter extends DefaultActivityItemFilter{


    public ManufactureItemFilter(LayoutInflater inflater, FilterFragment filter, FilterItemData[] filterData) {
        super(inflater, filter, filterData);
    }

    @Override
    protected void initControl() {
        super.initControl();
        ((Button)mView).setText(mFilter.getString(R.string.lbl_manufacture_item));
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
                    sqlBuilder.append(String.format("item.manufacturer=%s", item.getName()));
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
