package com.treelev.isimple.domain.ui.filter;

import com.treelev.isimple.enumerable.item.ProductType;

public class ClassificationSqlWhereClauseBuilder implements ExpandableActivityFilterItem.SqlWhereClauseBuilder {

    public static final ClassificationSqlWhereClauseBuilder INSTANCE = new ClassificationSqlWhereClauseBuilder();

    @Override
    public String buildGroupClause(String groupName) {
        return String.format("item.product_type=%s", ProductType.getProductTypeByLabel(groupName).ordinal());
    }

    @Override
    public String buildChildClause(String childName, String groupName) {
        return String.format("(item.product_type=%1$s and item.classification='%2$s')", groupName, childName);
    }
}
