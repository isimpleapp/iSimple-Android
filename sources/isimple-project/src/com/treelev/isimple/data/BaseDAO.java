package com.treelev.isimple.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public abstract class BaseDAO {

    private DatabaseSqlHelper databaseSqlHelper;
    private SQLiteDatabase database;

    protected BaseDAO(Context context) {
        databaseSqlHelper = new DatabaseSqlHelper(context);
    }

    public void open() {
        database = databaseSqlHelper.getWritableDatabase();
    }

    public void close() {
        if (database.isOpen()) {
            database.close();
        }
        databaseSqlHelper.close();
    }

    protected SQLiteStatement bindString(SQLiteStatement sqLiteStatement, int index, String value) {
        if (value != null) {
            sqLiteStatement.bindString(index, value);
        } else {
            sqLiteStatement.bindNull(index);
        }
        return sqLiteStatement;
    }

    protected SQLiteStatement bindInteger(SQLiteStatement sqLiteStatement, int index, Integer value) {
        if (value != null) {
            sqLiteStatement.bindDouble(index, value);
        } else {
            sqLiteStatement.bindNull(index);
        }
        return sqLiteStatement;
    }

    protected SQLiteStatement bindFloat(SQLiteStatement sqLiteStatement, int index, Float value) {
        if (value != null) {
            sqLiteStatement.bindDouble(index, value.doubleValue());
        } else {
            sqLiteStatement.bindNull(index);
        }
        return sqLiteStatement;
    }

    protected SQLiteStatement bindBoolean(SQLiteStatement sqLiteStatement, int index, Boolean value) {
        if (value != null) {
            sqLiteStatement.bindLong(index, value ? 1 : 0);
        } else {
            sqLiteStatement.bindNull(index);
        }
        return sqLiteStatement;
    }

    protected SQLiteDatabase getDatabase() {
        return database;
    }

    public abstract String getClassName();

    public abstract int getTableDataCount();
}
