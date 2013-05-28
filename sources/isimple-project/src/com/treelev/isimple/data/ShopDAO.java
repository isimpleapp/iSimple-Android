package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.domain.ui.DistanceShop;
import com.treelev.isimple.domain.db.Shop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopDAO extends BaseDAO {

    public final static int ID = 2;
    public final static  int SHOP_LIMIT = 100;

    public ShopDAO(Context context) {
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
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.SHOP_TABLE);
        Cursor c = getDatabase().rawQuery(selectSql, null);
        if (c != null) {
            count = c.getCount();
        }
        close();
        return count;
    }

    public void insertListData(List<Shop> items) {
        open();
        getDatabase().beginTransaction();
        try {
            String insertSql = "INSERT OR REPLACE INTO " + DatabaseSqlHelper.SHOP_TABLE + " (" +
                    DatabaseSqlHelper.SHOP_LOCATION_ID + ", " +
                    DatabaseSqlHelper.SHOP_LOCATION_NAME + ", " +
                    DatabaseSqlHelper.SHOP_LOCATION_ADDRESS + ", " +
                    DatabaseSqlHelper.SHOP_LATITUDE + ", " +
                    DatabaseSqlHelper.SHOP_LONGITUDE + ", " +
                    DatabaseSqlHelper.SHOP_WORKING_HOURS + ", " +
                    DatabaseSqlHelper.SHOP_PHONE_NUMBER + ", " +
                    DatabaseSqlHelper.SHOP_CHAIN_ID + ", " +
                    DatabaseSqlHelper.SHOP_LOCATION_TYPE + ", " +
                    DatabaseSqlHelper.SHOP_PRESENCE_PERCENTAGE + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement insertStatement = getDatabase().compileStatement(insertSql);
            for (Shop shop : items) {
                insertStatement = bindString(insertStatement, 1, shop.getLocationID());
                insertStatement = bindString(insertStatement, 2, shop.getLocationName());
                insertStatement = bindString(insertStatement, 3, shop.getLocationAddress());
                insertStatement = bindFloat(insertStatement, 5, shop.getLatitude());
                insertStatement = bindFloat(insertStatement, 4, shop.getLongitude());
                insertStatement = bindString(insertStatement, 6, shop.getWorkingHours());
                insertStatement = bindString(insertStatement, 7, shop.getPhoneNumber());
                insertStatement = bindString(insertStatement, 8, shop.getChainID());
                insertStatement = bindInteger(insertStatement, 9, shop.getLocationType().ordinal());
                insertStatement = bindFloat(insertStatement, 10, shop.getPresencePercentage());
                insertStatement.execute();
            }
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
        close();
    }

    public List<String> getShopsWithWine(String wineId) {
        open();
        String formatSelectScript = "select %1$s from %2$s join %3$s on %2$s.%4$s = %3$s.%5$s and %3$s.%6$s = %7$s";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.SHOP_LOCATION_NAME, DatabaseSqlHelper.SHOP_TABLE,
                DatabaseSqlHelper.ITEM_AVAILABILITY_TABLE, DatabaseSqlHelper.SHOP_LOCATION_ID, DatabaseSqlHelper.ITEM_AVAILABILITY_LOCATION_ID,
                DatabaseSqlHelper.ITEM_AVAILABILITY_ITEM_ID, wineId);
        Cursor cursor = getDatabase().rawQuery(selectSql, null);
        List<String> winesList = null;
        if (cursor != null) {
            winesList = new ArrayList<String>();
            while (cursor.moveToNext()) {
                winesList.add(cursor.getString(0));
            }
            cursor.close();
        }
        close();
        return winesList;
    }

    public List<AbsDistanceShop> getNearestShops(Location currentLocation) {
        open();
        String formatSelectScript = "select %s, %s, %s, %s, %s from %s";
        String selectSql = String.format(formatSelectScript,
                DatabaseSqlHelper.SHOP_LOCATION_ID,
                DatabaseSqlHelper.SHOP_LOCATION_NAME,
                DatabaseSqlHelper.SHOP_LOCATION_ADDRESS,
                DatabaseSqlHelper.SHOP_LATITUDE,
                DatabaseSqlHelper.SHOP_LONGITUDE,
                DatabaseSqlHelper.SHOP_TABLE);
        Cursor cursor = getDatabase().rawQuery(selectSql, null);
        List<AbsDistanceShop> distanceShopList = new ArrayList<AbsDistanceShop>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Shop shop = new Shop();
                shop.setLocationID(cursor.getString(0));
                shop.setLocationName(cursor.getString(1));
                shop.setLocationAddress(cursor.getString(2));
                shop.setLatitude(cursor.getFloat(3));
                shop.setLongitude(cursor.getFloat(4));
                Location shopLocation = shop.createShopLocation();
                DistanceShop distanceShop = new DistanceShop();
                distanceShop.setShop(shop);
                distanceShop.setDistance(currentLocation.distanceTo(shopLocation));
                distanceShopList.add(distanceShop);
            }
            Collections.sort(distanceShopList);
            cursor.close();
        }
        close();
        int limit = SHOP_LIMIT < distanceShopList.size() ? distanceShopList.size() : SHOP_LIMIT;
        return distanceShopList.subList(0, limit);
    }

    public List<AbsDistanceShop> getShopByChain(Location currentLocation, String chainID) {
        open();
        String formatSelectScript = "SELECT %s, %s, %s, %s, %s FROM %s WHERE %s = '%s'";
        String selectSql = String.format(formatSelectScript,
                DatabaseSqlHelper.SHOP_LOCATION_ID,
                DatabaseSqlHelper.SHOP_LOCATION_NAME,
                DatabaseSqlHelper.SHOP_LOCATION_ADDRESS,
                DatabaseSqlHelper.SHOP_LATITUDE,
                DatabaseSqlHelper.SHOP_LONGITUDE,
                DatabaseSqlHelper.SHOP_TABLE,
                DatabaseSqlHelper.CHAIN_ID, chainID);
        Cursor cursor = getDatabase().rawQuery(selectSql, null);
        List<AbsDistanceShop> distanceShopList = new ArrayList<AbsDistanceShop>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Shop shop = new Shop();
                shop.setLocationID(cursor.getString(0));
                shop.setLocationName(cursor.getString(1));
                shop.setLocationAddress(cursor.getString(2));
                shop.setLatitude(cursor.getFloat(3));
                shop.setLongitude(cursor.getFloat(4));
                Location shopLocation = shop.createShopLocation();
                DistanceShop distanceShop = new DistanceShop();
                distanceShop.setShop(shop);
                distanceShop.setDistance(currentLocation.distanceTo(shopLocation));
                distanceShopList.add(distanceShop);
            }
            Collections.sort(distanceShopList);
            cursor.close();
        }
        close();

        return distanceShopList;
    }

}
