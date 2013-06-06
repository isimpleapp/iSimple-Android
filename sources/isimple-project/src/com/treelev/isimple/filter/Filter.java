package com.treelev.isimple.filter;

import android.content.Context;
import android.text.TextUtils;
import com.treelev.isimple.domain.ui.filter.FilterItem;

import java.util.List;

public abstract class Filter {

    protected int currentCategory;
    protected List<FilterItem> filterItemList;
    private Context context;

    protected Filter(Context context, int currentCategory) {
        this.context = context;
        this.currentCategory = currentCategory;
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

    public void reset(){
       for(FilterItem item : filterItemList) {
           item.reset();
       }
    }
}
