package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import com.treelev.isimple.domain.db.ItemAvailability;

import java.util.List;

public class ItemAvailabilityDAO extends BaseDAO {

    public final static int ID = 4;

    public ItemAvailabilityDAO(Context context) {
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
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_AVAILABILITY_TABLE);
        Cursor c = getDatabase().rawQuery(selectSql, null);
        if (c != null) {
            count = c.getCount();
        }
        close();
        return count;
    }

    public void insertListData(List<ItemAvailability> items) {
        open();
        getDatabase().beginTransaction();
        try {
            String insertSql = "INSERT INTO " + DatabaseSqlHelper.ITEM_AVAILABILITY_TABLE + " (" +
                    DatabaseSqlHelper.ITEM_AVAILABILITY_ITEM_ID + ", " +
                    DatabaseSqlHelper.ITEM_AVAILABILITY_LOCATION_ID + ") VALUES (?, ?)";
            SQLiteStatement insertStatement = getDatabase().compileStatement(insertSql);
            for (ItemAvailability item : items) {
                insertStatement = bindString(insertStatement, 1, item.getItemId());
                insertStatement = bindString(insertStatement, 2, item.getLocationId());
                insertStatement.execute();
            }
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
        close();
    }

    public void deleteAllData() {
        open();
        getDatabase().beginTransaction();
        try {
            String deleteSql = "DELETE FROM " + DatabaseSqlHelper.ITEM_AVAILABILITY_TABLE;
            SQLiteStatement deleteStatement = getDatabase().compileStatement(deleteSql);
            deleteStatement.execute();
            getDatabase().delete(DatabaseSqlHelper.ITEM_AVAILABILITY_TABLE, null, null);
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
        close();
    }
}
