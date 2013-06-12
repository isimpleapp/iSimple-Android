package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import com.treelev.isimple.domain.db.Item;

import java.util.List;

public class FavouriteItemDAO extends BaseDAO {

    public static final int ID = 12;

    public FavouriteItemDAO(Context context) {
        super(context);
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    public int getTableDataCount() {
        return getTableDataCount(DatabaseSqlHelper.FAVOURITE_ITEM_TABLE);
    }

    public boolean addFavoutites(Item item){
        open();
        getDatabase().beginTransaction();
        try {
            String insertSql = "INSERT OR REPLACE INTO " + DatabaseSqlHelper.FAVOURITE_ITEM_TABLE + " (" +
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
                    DatabaseSqlHelper.ITEM_QUANTITY +
                    ") VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement insertStatement = getDatabase().compileStatement(insertSql);
            insertStatement = bindString(insertStatement, 1, item.getItemID());
            insertStatement = bindString(insertStatement, 2, item.getDrinkID());
            insertStatement = bindString(insertStatement, 3, item.getName());
            insertStatement = bindString(insertStatement, 4, item.getLocalizedName());
            insertStatement = bindString(insertStatement, 5, item.getManufacturer());
            insertStatement = bindString(insertStatement, 6, item.getLocalizedManufacturer());
            insertStatement = bindFloat(insertStatement, 7, item.getPrice());
            insertStatement = bindFloat(insertStatement, 8, item.getPriceMarkup());
            insertStatement = bindString(insertStatement, 9, item.getCountry());
            insertStatement = bindString(insertStatement, 10, item.getRegion());
            insertStatement = bindString(insertStatement, 11, item.getBarcode());
            insertStatement = bindInteger(insertStatement, 12, item.getDrinkCategory().ordinal());
            insertStatement = bindInteger(insertStatement, 13, item.getProductType().ordinal());
            insertStatement = bindString(insertStatement, 14, item.getClassification());
            insertStatement = bindInteger(insertStatement, 15, item.getColor().ordinal());
            insertStatement = bindString(insertStatement, 16, item.getStyle());
            insertStatement = bindInteger(insertStatement, 17, item.getSweetness().ordinal());
            insertStatement = bindInteger(insertStatement, 18, item.getYear());
            insertStatement = bindFloat(insertStatement, 19, item.getVolume());
            insertStatement = bindString(insertStatement, 20, item.getDrinkType());
            insertStatement = bindString(insertStatement, 21, item.getAlcohol());
            insertStatement = bindString(insertStatement, 22, item.getBottleHiResolutionImageFilename());
            insertStatement = bindString(insertStatement, 23, item.getBottleLowResolutionImageFilename());
            insertStatement = bindString(insertStatement, 24, item.getStyleDescription());
            insertStatement = bindString(insertStatement, 25, item.getAppelation());
            insertStatement = bindString(insertStatement, 26, item.getServingTempMin());
            insertStatement = bindString(insertStatement, 27, item.getServingTempMax());
            insertStatement = bindString(insertStatement, 28, item.getTasteQualities());
            insertStatement = bindString(insertStatement, 29, item.getVintageReport());
            insertStatement = bindString(insertStatement, 30, item.getAgingProcess());
            insertStatement = bindString(insertStatement, 31, item.getProductionProcess());
            insertStatement = bindString(insertStatement, 32, item.getInterestingFacts());
            insertStatement = bindString(insertStatement, 33, item.getLabelHistory());
            insertStatement = bindString(insertStatement, 34, item.getGastronomy());
            insertStatement = bindString(insertStatement, 35, item.getVineyard());
            insertStatement = bindString(insertStatement, 36, item.getGrapesUsed());
            insertStatement = bindString(insertStatement, 37, item.getRating());
            insertStatement = bindFloat(insertStatement, 38, item.getQuantity());
            insertStatement.execute();
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
            close();
        }
        return false;
    }

    public boolean delFavouriteItems(List<String> listItemsId){
        String formatScript = "%s = '%s'";
        String whereClause = "";
        open();
        boolean result = false;
        for(String itemId: listItemsId){
            whereClause = String.format(formatScript, DatabaseSqlHelper.ITEM_ID, itemId);
            result |= getDatabase().delete(DatabaseSqlHelper.FAVOURITE_ITEM_TABLE, whereClause, null) > 0;
        }
        close();
        return result;
    }

    public boolean isFavourites(String itemId) {
        String formatScript = "SELECT item_id FROM favourite_item WHERE item_id = '%s'";
        String sqlSelect = String.format(formatScript, itemId);
        open();
        Cursor cursor = getDatabase().rawQuery(sqlSelect, null);
        boolean result = false;
        if (cursor != null){
            result = cursor.moveToFirst();
        }
        close();
        return result;
    }

    public Cursor getFavouriteItems(){
//        String sqlSelect = "SELECT item_id as _id, name, localized_name, volume, bottle_low_resolution, product_type, drink_category, 0 as image, price, year, quantity, color, (case when ifnull(drink_id, '') = '' then ('e' || t1.item_id) else drink_id end) as drink_id, 0 as tmp2 " +
//                "FROM favourite_item";
        String sqlSelect ="SELECT item_id as _id, name, localized_name, volume, bottle_low_resolution, product_type, drink_category, 0 as image, price, year, quantity, color,  drink_id, 1 as is_favourite, 0 as tmp2 FROM favourite_item";
        open();
        return getDatabase().rawQuery(sqlSelect, null);
    }
}
