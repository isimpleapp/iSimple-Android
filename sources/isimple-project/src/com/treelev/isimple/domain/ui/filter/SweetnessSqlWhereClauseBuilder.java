package com.treelev.isimple.domain.ui.filter;

import com.treelev.isimple.enumerable.item.Sweetness;

public class SweetnessSqlWhereClauseBuilder implements DefaultActivityFilterItem.SqlWhereClauseBuilder {

    public static final SweetnessSqlWhereClauseBuilder INSTANCE = new SweetnessSqlWhereClauseBuilder();

    @Override
    public String buildClause(String filterName) {
        return String.format("item.sweetness=%s", Sweetness.getSweetness(filterName).ordinal());
    }
}
