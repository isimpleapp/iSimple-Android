package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import com.treelev.isimple.domain.db.DeprecatedItem;

import java.util.List;

public class DeprecatedItemDAO extends BaseDAO {

    public DeprecatedItemDAO(Context context) {
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
        String formatSelectScript = "select count(*) from %s";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_DEPRECATED_TABLE);
        Cursor c = getDatabase().rawQuery(selectSql, null);
        if (c != null) {
            if (c.moveToNext()) {
                count = c.getInt(0);
            }
            c.close();
        }
        close();
        return count;
    }

    public void insertListData(List<DeprecatedItem> deprecatedItems) {
        open();
        getDatabase().beginTransaction();
        try {
            String insertSql = "INSERT OR REPLACE INTO " + DatabaseSqlHelper.ITEM_DEPRECATED_TABLE + " (" +
                    DatabaseSqlHelper.ITEM_ID + ", " +
                    DatabaseSqlHelper.ITEM_DRINK_ID + ", " +
                    DatabaseSqlHelper.ITEM_NAME + ", " +
                    DatabaseSqlHelper.ITEM_LOCALIZED_NAME + ", " +
                    DatabaseSqlHelper.ITEM_MANUFACTURER + ", " +
                    DatabaseSqlHelper.ITEM_LOCALIZED_MANUFACTURER + ", " +
                    DatabaseSqlHelper.ITEM_COUNTRY + ", " +
                    DatabaseSqlHelper.ITEM_REGION + ", " +
                    DatabaseSqlHelper.ITEM_BARCODE + ", " +
                    DatabaseSqlHelper.ITEM_PRODUCT_TYPE + ", " +
                    DatabaseSqlHelper.ITEM_CLASSIFICATION + ", " +
                    DatabaseSqlHelper.ITEM_DRINK_CATEGORY + ", " +
                    DatabaseSqlHelper.ITEM_DRINK_TYPE + ", " +
                    DatabaseSqlHelper.ITEM_VOLUME +
                    ") VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement insertStatement = getDatabase().compileStatement(insertSql);
            for (DeprecatedItem deprecatedItem : deprecatedItems) {
                insertStatement = bindString(insertStatement, 1, deprecatedItem.getItemID());
                insertStatement = bindString(insertStatement, 2, deprecatedItem.getDrinkID());
                insertStatement = bindString(insertStatement, 3, deprecatedItem.getName());
                insertStatement = bindString(insertStatement, 4, deprecatedItem.getLocalizedName());
                insertStatement = bindString(insertStatement, 5, deprecatedItem.getManufacturer());
                insertStatement = bindString(insertStatement, 6, deprecatedItem.getLocalizedManufacturer());
                insertStatement = bindString(insertStatement, 7, deprecatedItem.getCountry());
                insertStatement = bindString(insertStatement, 8, deprecatedItem.getRegion());
                insertStatement = bindString(insertStatement, 9, deprecatedItem.getBarcode());
                if (deprecatedItem.getProductType() != null) {
                    insertStatement = bindInteger(insertStatement, 10, deprecatedItem.getProductType().ordinal());
                }
                insertStatement = bindString(insertStatement, 11, deprecatedItem.getClassification());
                if (deprecatedItem.getDrinkCategory() != null) {
                    insertStatement = bindInteger(insertStatement, 12, deprecatedItem.getDrinkCategory().ordinal());
                }
                insertStatement = bindString(insertStatement, 13, deprecatedItem.getDrinkType());
                insertStatement = bindFloat(insertStatement, 14, deprecatedItem.getVolume());
                insertStatement.execute();
            }
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
        close();
    }
}
