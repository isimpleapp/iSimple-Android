package com.treelev.isimple.domain.ui.filter;

public class RegionSqlWhereClauseBuilder implements ExpandableActivityFilterItem.SqlWhereClauseBuilder {

    public static final RegionSqlWhereClauseBuilder INSTANCE = new RegionSqlWhereClauseBuilder();

    @Override
    public String buildGroupClause(String groupName) {
        return String.format("item.country='%s'", groupName);
    }

    @Override
    public String buildChildClause(String childName, String groupName) {
        return String.format("(item.country='%1$s' and item.region='%2$s')", groupName, childName);
    }
}
