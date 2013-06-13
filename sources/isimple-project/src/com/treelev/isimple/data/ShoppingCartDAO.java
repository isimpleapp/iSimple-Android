package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import com.treelev.isimple.domain.db.Item;

public class ShoppingCartDAO extends BaseDAO {

    public static final int ID = 15;

    public ShoppingCartDAO(Context context) {
        super(context);
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    public int getTableDataCount() {
        return getTableDataCount(DatabaseSqlHelper.SHOPPING_CART_ITEM_TABLE);
    }

    public long insertItem(Item product) {
        open();
        String insertSql = "INSERT OR REPLACE INTO " + DatabaseSqlHelper.SHOPPING_CART_ITEM_TABLE + " (" +
                DatabaseSqlHelper.ITEM_ID + ", " +
                DatabaseSqlHelper.ITEM_DRINK_ID + ", " +
                DatabaseSqlHelper.ITEM_NAME + ", " +
                DatabaseSqlHelper.ITEM_LOCALIZED_NAME + ", " +
                DatabaseSqlHelper.ITEM_MANUFACTURER + ", " +
                DatabaseSqlHelper.ITEM_LOCALIZED_MANUFACTURER + ", " +
                DatabaseSqlHelper.ITEM_PRICE + ", " +
                DatabaseSqlHelper.ITEM_PRICE_MARKUP + ", " +
                DatabaseSqlHelper.ITEM_COUNTRY + ", " +
                DatabaseSqlHelper.ITEM_REGION + ", " +
                DatabaseSqlHelper.ITEM_BARCODE + ", " +
                DatabaseSqlHelper.ITEM_DRINK_CATEGORY + ", " +
                DatabaseSqlHelper.ITEM_PRODUCT_TYPE + ", " +
                DatabaseSqlHelper.ITEM_CLASSIFICATION + ", " +
                DatabaseSqlHelper.ITEM_COLOR + ", " +
                DatabaseSqlHelper.ITEM_STYLE + ", " +
                DatabaseSqlHelper.ITEM_SWEETNESS + ", " +
                DatabaseSqlHelper.ITEM_YEAR + ", " +
                DatabaseSqlHelper.ITEM_VOLUME + ", " +
                DatabaseSqlHelper.ITEM_DRINK_TYPE + ", " +
                DatabaseSqlHelper.ITEM_ALCOHOL + ", " +
                DatabaseSqlHelper.ITEM_BOTTLE_HI_RESOLUTION_IMAGE_FILENAME + ", " +
                DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME + ", " +
                DatabaseSqlHelper.ITEM_STYLE_DESCRIPTION + ", " +
                DatabaseSqlHelper.ITEM_APPELATION + ", " +
                DatabaseSqlHelper.ITEM_SERVING_TEMP_MIN + ", " +
                DatabaseSqlHelper.ITEM_SERVING_TEMP_MAX + ", " +
                DatabaseSqlHelper.ITEM_TASTE_QUALITIES + ", " +
                DatabaseSqlHelper.ITEM_VINTAGE_REPORT + ", " +
                DatabaseSqlHelper.ITEM_AGING_PROCESS + ", " +
                DatabaseSqlHelper.ITEM_PRODUCTION_PROCESS + ", " +
                DatabaseSqlHelper.ITEM_INTERESTING_FACTS + ", " +
                DatabaseSqlHelper.ITEM_LABEL_HISTORY + ", " +
                DatabaseSqlHelper.ITEM_GASTRONOMY + ", " +
                DatabaseSqlHelper.ITEM_VINEYARD + ", " +
                DatabaseSqlHelper.ITEM_GRAPES_USED + ", " +
                DatabaseSqlHelper.ITEM_RATING + ", " +
                DatabaseSqlHelper.ITEM_QUANTITY + ", " +
                DatabaseSqlHelper.ITEM_SHOPPING_CART_COUNT +
                ") VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement insertStatement = getDatabase().compileStatement(insertSql);
        insertStatement = bindString(insertStatement, 1, product.getItemID());
        insertStatement = bindString(insertStatement, 2, product.getDrinkID());
        insertStatement = bindString(insertStatement, 3, product.getName());
        insertStatement = bindString(insertStatement, 4, product.getLocalizedName());
        insertStatement = bindString(insertStatement, 5, product.getManufacturer());
        insertStatement = bindString(insertStatement, 6, product.getLocalizedManufacturer());
        insertStatement = bindFloat(insertStatement, 7, product.getPrice());
        insertStatement = bindFloat(insertStatement, 8, product.getPriceMarkup());
        insertStatement = bindString(insertStatement, 9, product.getCountry());
        insertStatement = bindString(insertStatement, 10, product.getRegion());
        insertStatement = bindString(insertStatement, 11, product.getBarcode());
        insertStatement = bindInteger(insertStatement, 12, product.getDrinkCategory().ordinal());
        insertStatement = bindInteger(insertStatement, 13, product.getProductType().ordinal());
        insertStatement = bindString(insertStatement, 14, product.getClassification());
        insertStatement = bindInteger(insertStatement, 15, product.getColor().ordinal());
        insertStatement = bindString(insertStatement, 16, product.getStyle());
        insertStatement = bindInteger(insertStatement, 17, product.getSweetness().ordinal());
        insertStatement = bindInteger(insertStatement, 18, product.getYear());
        insertStatement = bindFloat(insertStatement, 19, product.getVolume());
        insertStatement = bindString(insertStatement, 20, product.getDrinkType());
        insertStatement = bindString(insertStatement, 21, product.getAlcohol());
        insertStatement = bindString(insertStatement, 22, product.getBottleHiResolutionImageFilename());
        insertStatement = bindString(insertStatement, 23, product.getBottleLowResolutionImageFilename());
        insertStatement = bindString(insertStatement, 24, product.getStyleDescription());
        insertStatement = bindString(insertStatement, 25, product.getAppelation());
        insertStatement = bindString(insertStatement, 26, product.getServingTempMin());
        insertStatement = bindString(insertStatement, 27, product.getServingTempMax());
        insertStatement = bindString(insertStatement, 28, product.getTasteQualities());
        insertStatement = bindString(insertStatement, 29, product.getVintageReport());
        insertStatement = bindString(insertStatement, 30, product.getAgingProcess());
        insertStatement = bindString(insertStatement, 31, product.getProductionProcess());
        insertStatement = bindString(insertStatement, 32, product.getInterestingFacts());
        insertStatement = bindString(insertStatement, 33, product.getLabelHistory());
        insertStatement = bindString(insertStatement, 34, product.getGastronomy());
        insertStatement = bindString(insertStatement, 35, product.getVineyard());
        insertStatement = bindString(insertStatement, 36, product.getGrapesUsed());
        insertStatement = bindString(insertStatement, 37, product.getRating());
        insertStatement = bindFloat(insertStatement, 38, product.getQuantity());
        insertStatement = bindInteger(insertStatement, 39, 1);
        long id = insertStatement.executeInsert();
        insertStatement.execute();
        close();
        return id;
    }

    public void addItemCount(String itemId) {
        open();
        String updateScript = "UPDATE %1$s SET %2$s = ((SELECT %2$s FROM %1$s WHERE %3$s = %4$s) + 1) WHERE %3$s = %4$s";
        String updateSql = String.format(updateScript, DatabaseSqlHelper.SHOPPING_CART_ITEM_TABLE,
                DatabaseSqlHelper.ITEM_SHOPPING_CART_COUNT, DatabaseSqlHelper.ITEM_ID, itemId);
        getDatabase().execSQL(updateSql);
        close();
    }

    public boolean isProductExistShoppingCart(String itemId) {
        boolean result = false;
        open();
        String formatSelectScript = "SELECT %1$s FROM %2$s WHERE %1$s = '%3$s'";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_ID, DatabaseSqlHelper.SHOPPING_CART_ITEM_TABLE, itemId);
        Cursor cursor = getDatabase().rawQuery(selectSql, null);
        if (cursor != null && cursor.moveToFirst()) {
            result = true;
        }
        close();
        return result;
    }

    public Cursor getShoppingCartItems() {
        open();
        String selectScript = "SELECT item_id as _id, name, localized_name, volume, year, price, item_count FROM shopping_cart_item";
        return getDatabase().rawQuery(selectScript, null);
    }
}
