package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import com.treelev.isimple.domain.db.Chain;

import java.util.ArrayList;
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
                insertStatement = bindString(insertStatement, 1, chain.getChainId());
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
//        close();
        return getDatabase().rawQuery(selectSql, null);

    }



}
