package com.treelev.isimple.domain.ui.filter_fragment;


import android.view.LayoutInflater;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.fragments.filters.FilterFragment;

import java.util.Map;

public class CountryRegionItemFilter extends ExpandableActivityItemFilter {

    public CountryRegionItemFilter(LayoutInflater inflater, FilterFragment filter, Map<String, FilterItemData[]> childData) {
        super(inflater, filter, childData);
    }

    @Override
    public String getWhereClause() {
        StringBuilder sqlBuilder = new StringBuilder();
        if (mGroupData != null) {
            for (FilterItemData item : mGroupData) {
                if (item.isChecked()) {
                    if(sqlBuilder.length() > 0) {
                        sqlBuilder.append(" OR ");
                    }
                    sqlBuilder.append(String.format("item.country='%s'", item.getName()));
                }
            }
        }

        if (mChildData != null) {
            for (String groupName : mChildData.keySet()) {
                for (FilterItemData item : mChildData.get(groupName)) {
                    if (item.isChecked()) {
                        if(sqlBuilder.length() > 0) {
                            sqlBuilder.append(" OR ");
                        }
                        sqlBuilder.append(String.format("(item.country='%1$s' and item.region='%2$s')", item.getName(), groupName));
                    }
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
