package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;
import com.treelev.isimple.domain.db.Chain;

import java.util.List;

public class ChainDAO extends BaseDAO {

    public final static int ID = 3;

    public ChainDAO(Context context) {
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
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.CHAIN_TABLE);
        Cursor c = getDatabase().rawQuery(selectSql, null);
        if (c != null) {
            count = c.getCount();
        }
        close();
        return count;
    }

    public void insertListData(List<Chain> items) {
        open();
        getDatabase().beginTransaction();
        try {
            String insertSql = "INSERT OR REPLACE INTO " + DatabaseSqlHelper.CHAIN_TABLE + " (" +
                    DatabaseSqlHelper.CHAIN_ID + ", " +
                    DatabaseSqlHelper.CHAIN_NAME + ", " +
                    DatabaseSqlHelper.CHAIN_TYPE + ") VALUES (?, ?, ?)";
            SQLiteStatement insertStatement = getDatabase().compileStatement(insertSql);
            for (Chain chain : items) {
                insertStatement = bindString(insertStatement, 1, chain.getChainID());
                insertStatement = bindString(insertStatement, 2, chain.getChainName());
                insertStatement = bindInteger(insertStatement, 3, chain.getChainType().ordinal());
                insertStatement.execute();
            }
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
        close();
    }

    public Cursor getChains() {
        open();
        String formatSelectScript = "SELECT %s, %s, %s  FROM %s ORDER BY %s";
        String selectSql = String.format(formatSelectScript,
                DatabaseSqlHelper.CHAIN_ID + " as _id", DatabaseSqlHelper.CHAIN_NAME, DatabaseSqlHelper.CHAIN_TYPE,
                DatabaseSqlHelper.CHAIN_TABLE, DatabaseSqlHelper.CHAIN_NAME);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getChains(String drinkId) {
        open();
//        String formatSelectScript = "SELECT %s, %s, %s  FROM %s ORDER BY %s";
        String formatSelectScript = "SELECT t3.%s, t3.%s, t3.%s FROM %s AS t1, %s AS t2, %s AS t3 WHERE (t1.%s = t2.%s AND t2.%s = t3.%s ) AND t1.%s = '%s' GROUP BY t3.%s ORDER BY %s";
        //SELECT t1.item_id FROM item_availability AS t1, shop AS t2, chain AS t3 WHERE (t1.location_id = t2.location_id AND t2.chain_id = t3.chain_id ) AND t1.item_id = ''
        String selectSql = String.format(formatSelectScript,
                DatabaseSqlHelper.CHAIN_ID + " as _id",
                DatabaseSqlHelper.CHAIN_NAME,
                DatabaseSqlHelper.CHAIN_TYPE,
                DatabaseSqlHelper.ITEM_AVAILABILITY_TABLE,
                DatabaseSqlHelper.SHOP_TABLE,
                DatabaseSqlHelper.CHAIN_TABLE,
                DatabaseSqlHelper.ITEM_AVAILABILITY_LOCATION_ID,
                DatabaseSqlHelper.SHOP_LOCATION_ID,
                DatabaseSqlHelper.SHOP_CHAIN_ID,
                DatabaseSqlHelper.CHAIN_ID,
                DatabaseSqlHelper.ITEM_AVAILABILITY_ITEM_ID,
                drinkId,
                DatabaseSqlHelper.CHAIN_ID,
                DatabaseSqlHelper.CHAIN_NAME);
        return getDatabase().rawQuery(selectSql, null);
    }


}
