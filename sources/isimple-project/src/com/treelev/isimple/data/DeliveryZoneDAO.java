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
                    DatabaseSqlHelper.DELIVERY_DESC + ", " +
                    DatabaseSqlHelper.DELIVERY_ADDRESS + ", " +
                    DatabaseSqlHelper.DELIVERY_LONGITUDE + ", " +
                    DatabaseSqlHelper.DELIVERY_LATITUDE +
                    ") VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement insertStatement = getDatabase().compileStatement(insertSql);
            for (DeliveryZone deliveryZone : deliveryZones) {
                insertStatement = bindString(insertStatement, 1, deliveryZone.getName());
                insertStatement = bindInteger(insertStatement, 2, 0);
                insertStatement = bindInteger(insertStatement, 3, deliveryZone.getPickupCondition() - 1);
                insertStatement = bindString(insertStatement, 4, getStartDescByName(deliveryZone.getPickupCondition()));
                insertStatement = bindString(insertStatement, 5, deliveryZone.getAddress());
                insertStatement = bindFloat(insertStatement, 6, deliveryZone.getLongitude());
                insertStatement = bindFloat(insertStatement, 7, deliveryZone.getLatitude());
                insertStatement.execute();
                insertStatement = bindString(insertStatement, 1, deliveryZone.getName());
                insertStatement = bindInteger(insertStatement, 2, deliveryZone.getPickupCondition());
                insertStatement = bindInteger(insertStatement, 3, deliveryZone.getDeliveryCondition() - 1);
                insertStatement = bindString(insertStatement, 4, deliveryZone.getPickupDesc());
                insertStatement = bindString(insertStatement, 5, deliveryZone.getAddress());
                insertStatement = bindFloat(insertStatement, 6, deliveryZone.getLongitude());
                insertStatement = bindFloat(insertStatement, 7, deliveryZone.getLatitude());
                insertStatement.execute();
                insertStatement = bindString(insertStatement, 1, deliveryZone.getName());
                insertStatement = bindInteger(insertStatement, 2, deliveryZone.getDeliveryCondition());
                insertStatement = bindInteger(insertStatement, 3, deliveryZone.getSpecialCondition() - 1);
                insertStatement = bindString(insertStatement, 4, deliveryZone.getDeliveryDesc());
                insertStatement = bindString(insertStatement, 5, deliveryZone.getAddress());
                insertStatement = bindFloat(insertStatement, 6, deliveryZone.getLongitude());
                insertStatement = bindFloat(insertStatement, 7, deliveryZone.getLatitude());
                insertStatement.execute();
                insertStatement = bindString(insertStatement, 1, deliveryZone.getName());
                insertStatement = bindInteger(insertStatement, 2, deliveryZone.getSpecialCondition());
                insertStatement = bindInteger(insertStatement, 3, null);
                insertStatement = bindString(insertStatement, 4, deliveryZone.getSpecialDesc());
                insertStatement = bindString(insertStatement, 5, deliveryZone.getAddress());
                insertStatement = bindFloat(insertStatement, 6, deliveryZone.getLongitude());
                insertStatement = bindFloat(insertStatement, 7, deliveryZone.getLatitude());
                insertStatement.execute();
            }
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
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
        return countryLabel;
    }

    public String[] getCountries() {
        String formatQuery = "SELECT DISTINCT %s FROM %s";
        String query = String.format(formatQuery, DatabaseSqlHelper.DELIVERY_NAME, DatabaseSqlHelper.DELIVERY_ITEM_TABLE);
        open();
        Cursor cursor = getDatabase().rawQuery(query, null);
        String[] countries = null;
        if (cursor != null) {
            countries = new String[cursor.getCount()];
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                countries[i] = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.DELIVERY_NAME));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return countries;
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
        return desc;
    }

    public void deleteAllData() {
        open();
        String deleteSql = " DELETE FROM delivery";
        getDatabase().execSQL(deleteSql);
    }

    public int getMinPriceByCountry(String country) {
        String formatQuery = "SELECT MIN(max_condition) as min_price FROM delivery WHERE [name] = '%s'";
        String query = String.format(formatQuery, country);
        open();
        Cursor cursor = getDatabase().rawQuery(query, null);
        int minPrice = -1;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                minPrice = cursor.getInt(0);
            }
            cursor.close();
        }
        return minPrice;
    }

    private String getStartDescByName(int minPrice) {
        return String.format(START_DESC_FORMAT, minPrice);
    }

    public DeliveryZone getDeliveryZone(String name){
        DeliveryZone deliveryZone = null;
        open();
        String formatQuery = "SELECT * FROM delivery WHERE name = '%s' LIMIT 0,1";
        Cursor cursor = getDatabase().rawQuery(String.format(formatQuery, name), null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                deliveryZone = new DeliveryZone();
                deliveryZone.setName(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.DELIVERY_NAME)));
                deliveryZone.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.DELIVERY_ADDRESS)));
                deliveryZone.setLatitude(cursor.getFloat(cursor.getColumnIndex(DatabaseSqlHelper.DELIVERY_LATITUDE)));
                deliveryZone.setLongitude(cursor.getFloat(cursor.getColumnIndex(DatabaseSqlHelper.DELIVERY_LONGITUDE)));
            }
        }
        return deliveryZone;
    }


}
