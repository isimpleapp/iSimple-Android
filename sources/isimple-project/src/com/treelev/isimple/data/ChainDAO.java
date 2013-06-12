package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
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

    public int getTableDataCount() {
        return getTableDataCount(DatabaseSqlHelper.CHAIN_TABLE);
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

    public Cursor getChains(String itemId) {
        open();
        String selectSql = "select chain.chain_id as _id, chain.chain_name, chain.chain_type from chain where chain.chain_id in (" +
                "SELECT s.chain_id FROM shop as s inner join item_availability as a on a.location_id=s.location_id " +
                "WHERE a.item_id='%s') order by chain.chain_name";

        return getDatabase().rawQuery(String.format(selectSql, itemId), null);
    }


}
