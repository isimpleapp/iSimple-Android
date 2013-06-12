package com.treelev.isimple.data;

import android.content.Context;

public class ShoppingCartDAO extends BaseDAO {

    protected ShoppingCartDAO(Context context) {
        super(context);
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    public int getTableDataCount() {
        return getTableDataCount(DatabaseSqlHelper.SHOPPING_CART_ITEM_TABLE);
    }
}
