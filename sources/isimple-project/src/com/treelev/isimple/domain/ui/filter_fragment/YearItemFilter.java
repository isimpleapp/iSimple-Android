package com.treelev.isimple.domain.ui.filter_fragment;

import android.view.LayoutInflater;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.fragments.filters.FilterFragment;

public class YearItemFilter extends DefaultActivityItemFilter {

    public YearItemFilter(LayoutInflater inflater, FilterFragment filter, FilterItemData[] filterData) {
        super(inflater, filter, filterData);
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
