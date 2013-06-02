package com.treelev.isimple.filter;

import android.content.Context;
import android.text.TextUtils;
import com.treelev.isimple.domain.ui.filter.FilterItem;

import java.util.List;

public abstract class Filter {

    private Context context;

    protected Filter(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    public abstract List<FilterItem> getFilterContent();

    public String getSQLWhereClause() {
        StringBuilder sqlBuilder = new StringBuilder();
        List<FilterItem> filterItems = getFilterContent();
        for(FilterItem item : filterItems) {
            String itemWhereClause = item.getSQLWhereClause();
            if (!TextUtils.isEmpty(itemWhereClause)) {
                if (sqlBuilder.length() > 0) {
                    sqlBuilder.append(" and ");
                }
                sqlBuilder.append(itemWhereClause);
            }
        }
        return sqlBuilder.toString();
    }
}
