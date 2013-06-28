package com.treelev.isimple.data;

import android.content.Context;
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

    public int getTableDataCount() {
        return getTableDataCount(DatabaseSqlHelper.ITEM_AVAILABILITY_TABLE);
    }

    public void insertListData(List<ItemAvailability> items) {
        open();
        getDatabase().beginTransaction();
        try {
            String insertSql = "INSERT INTO " + DatabaseSqlHelper.ITEM_AVAILABILITY_TABLE + " (" +
                    DatabaseSqlHelper.ITEM_AVAILABILITY_ITEM_ID + ", " +
                    DatabaseSqlHelper.ITEM_AVAILABILITY_LOCATION_ID + ", " +
                    DatabaseSqlHelper.ITEM_AVAILABILITY_CUSTOMER_ID + ", " +
                    DatabaseSqlHelper.ITEM_AVAILABILITY_SHIPTO_CODE_ID + ", " +
                    DatabaseSqlHelper.ITEM_AVAILABILITY_PRICE +
                    ") VALUES (?, ?, ?, ?, ?)";
            SQLiteStatement insertStatement = getDatabase().compileStatement(insertSql);
            for (ItemAvailability item : items) {
                insertStatement = bindString(insertStatement, 1, item.getItemID());
                insertStatement = bindString(insertStatement, 2, item.getLocationID());
                insertStatement = bindString(insertStatement, 3, item.getCustomerID());
                insertStatement = bindString(insertStatement, 4, item.getShiptoCodeID());
                insertStatement = bindFloat(insertStatement, 5, item.getPrice());
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
        String deleteSql = " DELETE FROM item_availability";
        getDatabase().execSQL(deleteSql);
        close();
    }
}
