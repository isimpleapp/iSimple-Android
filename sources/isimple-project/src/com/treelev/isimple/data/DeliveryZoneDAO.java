package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import com.treelev.isimple.domain.db.DeliveryZone;

import java.util.List;

public class DeliveryZoneDAO extends BaseDAO {

    public static final int ID = 16;

    private final static String START_DESC_FORMAT = "Минимальная сумма заказа - %d рублей";

    public DeliveryZoneDAO(Context context) {
        super(context);
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    public int getTableDataCount() {
        return -1;
    }

    public void insertListData(List<DeliveryZone> deliveryZones) {
        open();
        getDatabase().beginTransaction();
        try {
            String insertSql = "INSERT OR REPLACE INTO " + DatabaseSqlHelper.DELIVERY_ITEM_TABLE + " (" +
                    DatabaseSqlHelper.DELIVERY_NAME + ", " +
                    DatabaseSqlHelper.DELIVERY_MIN_CONDITION + ", " +
                    DatabaseSqlHelper.DELIVERY_MAX_CONDITION + ", " +
                    DatabaseSqlHelper.DELIVERY_DESC +
                    ") VALUES " +
                    "(?, ?, ?, ?)";
            SQLiteStatement insertStatement = getDatabase().compileStatement(insertSql);
            for (DeliveryZone deliveryZone : deliveryZones) {
                insertStatement = bindString(insertStatement, 1, deliveryZone.getName());
                insertStatement = bindInteger(insertStatement, 2, 0);
                insertStatement = bindInteger(insertStatement, 3, deliveryZone.getPickupCondition());
                insertStatement = bindString(insertStatement, 4, getStartDescByName(deliveryZone.getPickupCondition()));
                insertStatement.execute();
                insertStatement = bindString(insertStatement, 1, deliveryZone.getName());
                insertStatement = bindInteger(insertStatement, 2, deliveryZone.getPickupCondition() + 1);
                insertStatement = bindInteger(insertStatement, 3, deliveryZone.getDeliveryCondition());
                insertStatement = bindString(insertStatement, 4, deliveryZone.getPickupDesc());
                insertStatement.execute();
                insertStatement = bindString(insertStatement, 1, deliveryZone.getName());
                insertStatement = bindInteger(insertStatement, 2, deliveryZone.getDeliveryCondition() + 1);
                insertStatement = bindInteger(insertStatement, 3, deliveryZone.getSpecialCondition());
                insertStatement = bindString(insertStatement, 4, deliveryZone.getDeliveryDesc());
                insertStatement.execute();
                insertStatement = bindString(insertStatement, 1, deliveryZone.getName());
                insertStatement = bindInteger(insertStatement, 2, deliveryZone.getSpecialCondition() + 1);
                insertStatement = bindInteger(insertStatement, 3, null);
                insertStatement = bindString(insertStatement, 4, deliveryZone.getSpecialDesc());
                insertStatement.execute();
            }
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
        close();
    }

    public String getFirstCountryLabel() {
        String formatQuery = "SELECT %s FROM %s";
        String query = String.format(formatQuery, DatabaseSqlHelper.DELIVERY_NAME, DatabaseSqlHelper.DELIVERY_ITEM_TABLE);
        open();
        Cursor cursor = getDatabase().rawQuery(query, null);
        String countryLabel = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                countryLabel = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.DELIVERY_NAME));
            }
            cursor.close();
        }
        close();
        return countryLabel;
    }

    public String getMessage(String country, int price) {
        String formatQuery = "SELECT %1$s FROM %2$s WHERE (%3$s = '%4$s') AND (%5$s >= %6$d OR %5$s IS NULL) AND (%7$s <= %6$d)";
        String query = String.format(formatQuery, DatabaseSqlHelper.DELIVERY_DESC, DatabaseSqlHelper.DELIVERY_ITEM_TABLE,
                DatabaseSqlHelper.DELIVERY_NAME, country, DatabaseSqlHelper.DELIVERY_MAX_CONDITION, price, DatabaseSqlHelper.DELIVERY_MIN_CONDITION);
        open();
        Cursor cursor = getDatabase().rawQuery(query, null);
        String desc = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                desc = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.DELIVERY_DESC));
            }
            cursor.close();
        }
        close();
        return desc;
    }

    private String getStartDescByName(int minPrice) {
        return String.format(START_DESC_FORMAT, minPrice);
    }
}
