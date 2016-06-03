package com.treelev.isimple.domain.ui.filter;

public class YearSqlWhereClauseBuilder implements DefaultActivityFilterItem.SqlWhereClauseBuilder {
    public static final YearSqlWhereClauseBuilder INSTANCE = new YearSqlWhereClauseBuilder();

    @Override
    public String buildClause(String filterName) {
        return String.format("item.year=%s", filterName);
    }
}
