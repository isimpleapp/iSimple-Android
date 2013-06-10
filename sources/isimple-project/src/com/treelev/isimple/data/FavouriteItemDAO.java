package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;

public class FavouriteItemDAO extends BaseDAO {
    protected FavouriteItemDAO(Context context) {
        super(context);
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public int getTableDataCount() {
        int count = -1;
        open();
        String formatSelectScript = "select * from %s";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.FAVOURITE_ITEM_TABLE);
        Cursor c = getDatabase().rawQuery(selectSql, null);
        if (c != null) {
            count = c.getCount();
        }
        close();
        return count;
    }
}
