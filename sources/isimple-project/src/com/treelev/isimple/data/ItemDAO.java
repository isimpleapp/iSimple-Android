package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.DeprecatedItem;
import com.treelev.isimple.domain.db.FeaturedItem;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.db.ItemPrice;
import com.treelev.isimple.enumerable.item.ItemColor;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.enumerable.item.Sweetness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDAO extends BaseDAO {

//    public final static int ID = 1;
//    private final static String FIRST_PART_SELECT_SCRIPT = "SELECT %s, %s, %s, %s, %s, %s, %s, MIN(%s) as price, COUNT(%s), %s FROM %s ";
//    private final static String SCRIPT_SELECT_RANDOM = "SELECT %s, %s, %s, %s, %s, %s, %s, MIN(%s) as price, COUNT(%s), %s FROM %s GROUP BY %s LIMIT 10 ";
//    private final static String SCRIPT_SELECT_DRINK_ID = "SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = %s";
//    private final static int SCRIPT_TYPE_WINE = 2;
//    private final static int SCRIPT_TYPE_OTHERS = 3;
//
//    private final static String FORMAT_QUERY_WINE = " %s WHERE %s %s %s %s %s %s GROUP BY " + DatabaseSqlHelper.ITEM_DRINK_ID;
//    private final static String FORMAT_QUERY_OTHER = " %s WHERE %s %s  GROUP BY " + DatabaseSqlHelper.ITEM_DRINK_ID;
//    private final static String FORMAT_QUERY_WINE_SEARCH = " %s WHERE %s %s %s %s %s %s %s GROUP BY " + DatabaseSqlHelper.ITEM_DRINK_ID;
//    private final static String FORMAT_QUERY_OTHER_SEARCH = " %s WHERE %s %s %s GROUP BY " + DatabaseSqlHelper.ITEM_DRINK_ID;
//    private final static String FORMAT_ORDER_BY_PRICE_MIN = " ORDER BY MIN(%s)";
//    private final static String FORMAT_ORDER_BY_PRICE = " ORDER BY %s";
//    private final static String FORMAT_ORDER_BY_NAME = " ORDER BY %s";

    public final static int ID = 1;
    private final static String FIRST_PART_SELECT_SCRIPT = "SELECT %s, %s, %s, %s, %s, %s, %s, MIN(%s) as price, COUNT(%s), %s FROM %s ";
    private final static String SCRIPT_SELECT_RANDOM = "SELECT %s, %s, %s, %s, %s, %s, %s, MIN(%s) as price, COUNT(%s), %s FROM %s GROUP BY %s LIMIT 10 ";
    private final static String SCRIPT_SELECT_DRINK_ID = "SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = %s";
    private final static int SCRIPT_TYPE_WINE = 2;
    private final static int SCRIPT_TYPE_OTHERS = 3;

    private final static String FORMAT_QUERY_WINE = " %s WHERE %s %s %s %s %s %s GROUP BY " + DatabaseSqlHelper.ITEM_DRINK_ID;
    private final static String FORMAT_QUERY_OTHER = " %s WHERE %s %s  GROUP BY " + DatabaseSqlHelper.ITEM_DRINK_ID;
    private final static String FORMAT_QUERY_WINE_SEARCH = " %s WHERE %s %s %s %s %s %s %s GROUP BY " + DatabaseSqlHelper.ITEM_DRINK_ID;
    private final static String FORMAT_QUERY_OTHER_SEARCH = " %s WHERE %s %s %s GROUP BY " + DatabaseSqlHelper.ITEM_DRINK_ID;
    private final static String FORMAT_ORDER_BY_PRICE = " ORDER BY %s";
    private final static String FORMAT_ORDER_BY_NAME = " ORDER BY %s";


    private final static int RANDOM = -1;
    private final static String SELECT_ITEMS_FROM = "SELECT %s, %s, %s, %s, %s, %s, %s, %s, MIN(%s) as price, %s, %s, %s, %s,COUNT(%s) FROM %s  WHERE %s GROUP BY %s  %s";
    private final static String SELECT_ITEMS_FROM_RANDOM = "SELECT %s, %s, %s, %s, %s, %s, %s, %s, MIN(%s) as price, %s, %s, %s, %s, COUNT(%s) FROM %s WHERE %s GROUP BY %s";
    private final static String SELECT_ITEMS_FROM_DRINK_ID = "SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,  %s, %s, %s FROM %s WHERE %s %s";
    private final static String FORMAT_ORDER_BY_MIN = "ORDER BY MIN(%s)";
    private final static String FORMAT_ORDER_BY = "ORDER BY %s";
    private final static String FORMAT_FROM_TWO_TABLE = "%s AS %s, %s AS %s";
    private final static String FORMAT_JOIN_TWO_TABLE = "%s.%s = %s.%s"; //[name table].[filed]
    private final static String TABLE_ONE = "t1";
    private final static String TABLE_TWO = "t2";
    private final static String COMPARE = "%s = %s";
    private final static String COMPARE_STRING = "%s = '%s'";
    private final static String AND = "%s AND %s";
    private final static String OR = "%s OR %s";
    private final static String HOOKS = "(%s)";
    private final static String LIKE = "%s LIKE '%s'";
    private final static String DISTINC = "DISTINC %s";

    public ItemDAO(Context context) {
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
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_TABLE);
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

    public Cursor getFeaturedItemsByCategory(int categoryId, String orderByField) {
        open();
        String orderBy = "";
        if (orderByField != null) {
            String formatOrder = orderByField.equals(DatabaseSqlHelper.ITEM_NAME) ? FORMAT_ORDER_BY : FORMAT_ORDER_BY_MIN;
            orderBy = String.format(formatOrder, orderByField);
        }
        String from = String.format(FORMAT_FROM_TWO_TABLE,
                DatabaseSqlHelper.ITEM_TABLE,
                TABLE_ONE,
                DatabaseSqlHelper.FEATURED_ITEM_TABLE,
                TABLE_TWO);
        String join = String.format(FORMAT_JOIN_TWO_TABLE,
                TABLE_ONE,
                DatabaseSqlHelper.ITEM_ID,
                TABLE_TWO,
                DatabaseSqlHelper.FEATURED_ITEM_ID);
        String whereCategory = String.format(COMPARE,
                DatabaseSqlHelper.FEATURED_ITEM_CATEGORY_ID,
                categoryId);
        String where = String.format(AND, join, whereCategory);
        String selectSql = String.format(SELECT_ITEMS_FROM,
                TABLE_ONE + "." + DatabaseSqlHelper.ITEM_ID + " as _id",
                DatabaseSqlHelper.ITEM_NAME,
                DatabaseSqlHelper.ITEM_LOCALIZED_NAME,
                DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME,
                DatabaseSqlHelper.ITEM_PRODUCT_TYPE,
                DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
                "0 as image",
                DatabaseSqlHelper.ITEM_PRICE,
                DatabaseSqlHelper.ITEM_YEAR,
                DatabaseSqlHelper.ITEM_QUANTITY,
                DatabaseSqlHelper.ITEM_COLOR,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                from,
                where,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getFeaturedItemsByCategory(int categoryId, String locationId, String orderByField) {
        open();
        String orderBy = "";
        if(orderByField != null) {
            String formatOrder = orderByField.equals(DatabaseSqlHelper.ITEM_NAME) ? FORMAT_ORDER_BY : FORMAT_ORDER_BY_MIN;
            orderBy = String.format(formatOrder, TABLE_ONE + "." + orderByField);
        }
        String from = String.format(FORMAT_FROM_TWO_TABLE,
                DatabaseSqlHelper.ITEM_TABLE,
                TABLE_ONE,
                DatabaseSqlHelper.ITEM_AVAILABILITY_TABLE,
                TABLE_TWO);
        String join = String.format(FORMAT_JOIN_TWO_TABLE,
                TABLE_ONE,
                DatabaseSqlHelper.ITEM_ID,
                TABLE_TWO,
                DatabaseSqlHelper.ITEM_ID);
        String whereCategory = String.format(COMPARE,
                TABLE_ONE + "." +DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
                categoryId);
        String whereShop = String.format(COMPARE_STRING,
                DatabaseSqlHelper.SHOP_LOCATION_ID,
                locationId);
        String where = String.format(AND, join, whereCategory);
        where = String.format(AND, where, whereShop);
        String selectSql = String.format(SELECT_ITEMS_FROM,
                TABLE_ONE + "." + DatabaseSqlHelper.ITEM_ID + " as _id",
                DatabaseSqlHelper.ITEM_NAME,
                DatabaseSqlHelper.ITEM_LOCALIZED_NAME,
                DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME,
                DatabaseSqlHelper.ITEM_PRODUCT_TYPE,
                TABLE_ONE + "." + DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
                "0 as image",
                TABLE_ONE + "." + DatabaseSqlHelper.ITEM_PRICE,
                DatabaseSqlHelper.ITEM_YEAR,
                DatabaseSqlHelper.ITEM_QUANTITY,
                DatabaseSqlHelper.ITEM_COLOR,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                from,
                where,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getItemsByDrinkId(String drinkId, String orderByField) {
        open();
        String orderBy = "";
        if (orderByField != null) {
            orderBy = String.format(FORMAT_ORDER_BY, orderByField);
        }
        String from = DatabaseSqlHelper.ITEM_TABLE;
        String where = String.format(COMPARE_STRING,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                drinkId);
        String selectSql = String.format(SELECT_ITEMS_FROM_DRINK_ID,
                DatabaseSqlHelper.ITEM_ID + " as _id",
                DatabaseSqlHelper.ITEM_NAME,
                DatabaseSqlHelper.ITEM_LOCALIZED_NAME,
                DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME,
                DatabaseSqlHelper.ITEM_PRODUCT_TYPE,
                DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
                "0 as image",
                DatabaseSqlHelper.ITEM_PRICE,
                DatabaseSqlHelper.ITEM_YEAR,
                DatabaseSqlHelper.ITEM_QUANTITY,
                DatabaseSqlHelper.ITEM_COLOR,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                from,
                where,
                orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getFilteredItemsByCategory(Integer categoryId, String query, String orderByField) {
        return getFilteredItemsByCategory(categoryId, null, query, orderByField);
    }

    public Cursor getFilteredItemsByCategory(Integer categoryId, String locationId, String whereClause, String orderByField) {
        String selectSql = String.format(
                "SELECT item_id as _id, name, localized_name, volume, bottle_low_resolution, product_type, " +
                    "drink_category, 0 as image, MIN(price) as price, year, quantity, color, drink_id, COUNT(drink_id) " +
                    "FROM item WHERE drink_category=%1$s and %2$s GROUP BY drink_id ORDER BY %3$s",
                categoryId, whereClause, orderByField);
        open();
//        String orderBy = "";
//        if(orderByField != null) {
//            String formatOrder = orderByField.equals(DatabaseSqlHelper.ITEM_NAME) ? FORMAT_ORDER_BY : FORMAT_ORDER_BY_MIN;
//            orderBy = String.format(formatOrder, TABLE_ONE + "." + orderByField);
//        }
//        String from = String.format(FORMAT_FROM_TWO_TABLE,
//                DatabaseSqlHelper.ITEM_TABLE,
//                TABLE_ONE,
//                DatabaseSqlHelper.ITEM_AVAILABILITY_TABLE,
//                TABLE_TWO);
//        String join = String.format(FORMAT_JOIN_TWO_TABLE,
//                TABLE_ONE,
//                DatabaseSqlHelper.ITEM_ID,
//                TABLE_TWO,
//                DatabaseSqlHelper.ITEM_ID);
//        String whereCategory = String.format(COMPARE,
//                TABLE_ONE + "." +DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
//                categoryId);
//        String whereShop = String.format(COMPARE_STRING,
//                DatabaseSqlHelper.SHOP_LOCATION_ID,
//                locationId);
//        String where = String.format(AND, join, whereCategory);
//        where = String.format(AND, where, whereShop);
//        String whereSearch = String.format(HOOKS, getWhereBySearch(query));
//        where = String.format(AND, where, whereSearch);
//        String selectSql = String.format(SELECT_ITEMS_FROM,
//                TABLE_ONE + "." + DatabaseSqlHelper.ITEM_ID + " as _id",
//                DatabaseSqlHelper.ITEM_NAME,
//                DatabaseSqlHelper.ITEM_LOCALIZED_NAME,
//                DatabaseSqlHelper.ITEM_VOLUME,
//                DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME,
//                DatabaseSqlHelper.ITEM_PRODUCT_TYPE,
//                DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
//                "0 as image",
//                TABLE_ONE + "." + DatabaseSqlHelper.ITEM_PRICE,
//                DatabaseSqlHelper.ITEM_YEAR,
//                DatabaseSqlHelper.ITEM_QUANTITY,
//                DatabaseSqlHelper.ITEM_COLOR,
//                DatabaseSqlHelper.ITEM_DRINK_ID,
//                DatabaseSqlHelper.ITEM_DRINK_ID,
//                from,
//                where,
//                DatabaseSqlHelper.ITEM_DRINK_ID,
//                orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    //TODO refactor: переименовать, заменить конкантенацию на String.format, метод дублируется с getItemsByCategory
    public Cursor getSearchItemsByCategory(Integer categoryId, String query, String orderByField) {
        open();
        String orderBy = "";
        if (orderByField != null) {
            String formatOrder = orderByField.equals(DatabaseSqlHelper.ITEM_NAME) ? FORMAT_ORDER_BY : FORMAT_ORDER_BY_MIN;
            orderBy = String.format(formatOrder, orderByField);
        }
        String from = DatabaseSqlHelper.ITEM_TABLE;
        String whereCategory = "";
        String where = getWhereBySearch(query);
        if (categoryId != null) {
            whereCategory = String.format(COMPARE,
                    DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
                    categoryId);
            where = String.format(HOOKS, where);
            where = String.format(AND, whereCategory, where);
        }
        String selectSql = String.format(SELECT_ITEMS_FROM,
                DatabaseSqlHelper.ITEM_ID + " as _id",
                DatabaseSqlHelper.ITEM_NAME,
                DatabaseSqlHelper.ITEM_LOCALIZED_NAME,
                DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME,
                DatabaseSqlHelper.ITEM_PRODUCT_TYPE,
                DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
                "0 as image",
                DatabaseSqlHelper.ITEM_PRICE,
                DatabaseSqlHelper.ITEM_YEAR,
                DatabaseSqlHelper.ITEM_QUANTITY,
                DatabaseSqlHelper.ITEM_COLOR,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                from,
                where,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getSearchItemsByCategory(Integer categoryId, String locationId, String query, String orderByField) {
        open();
        String orderBy = "";
        if(orderByField != null) {
            String formatOrder = orderByField.equals(DatabaseSqlHelper.ITEM_NAME) ? FORMAT_ORDER_BY : FORMAT_ORDER_BY_MIN;
            orderBy = String.format(formatOrder, TABLE_ONE + "." + orderByField);
        }
        String from = String.format(FORMAT_FROM_TWO_TABLE,
                DatabaseSqlHelper.ITEM_TABLE,
                TABLE_ONE,
                DatabaseSqlHelper.ITEM_AVAILABILITY_TABLE,
                TABLE_TWO);
        String join = String.format(FORMAT_JOIN_TWO_TABLE,
                TABLE_ONE,
                DatabaseSqlHelper.ITEM_ID,
                TABLE_TWO,
                DatabaseSqlHelper.ITEM_ID);
        String whereCategory = String.format(COMPARE,
                TABLE_ONE + "." +DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
                categoryId);
        String whereShop = String.format(COMPARE_STRING,
                DatabaseSqlHelper.SHOP_LOCATION_ID,
                locationId);
        String where = String.format(AND, join, whereCategory);
        where = String.format(AND, where, whereShop);
        String whereSearch = String.format(HOOKS, getWhereBySearch(query));
        where = String.format(AND, where, whereSearch);
        String selectSql = String.format(SELECT_ITEMS_FROM,
                TABLE_ONE + "." + DatabaseSqlHelper.ITEM_ID + " as _id",
                DatabaseSqlHelper.ITEM_NAME,
                DatabaseSqlHelper.ITEM_LOCALIZED_NAME,
                DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME,
                DatabaseSqlHelper.ITEM_PRODUCT_TYPE,
                DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
                "0 as image",
                TABLE_ONE + "." + DatabaseSqlHelper.ITEM_PRICE,
                DatabaseSqlHelper.ITEM_YEAR,
                DatabaseSqlHelper.ITEM_QUANTITY,
                DatabaseSqlHelper.ITEM_COLOR,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                from,
                where,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    public List<String> getYearsByCategory(int categoryId) {
        open();
        List<String> years = new ArrayList<String>();
        String sqlQueryString = String.format("select distinct %1$s from %2$s where drink_category=%3$s order by %1$s",
                DatabaseSqlHelper.ITEM_YEAR,
                DatabaseSqlHelper.ITEM_TABLE,
                categoryId);
        Cursor cursor = getDatabase().rawQuery(sqlQueryString, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String str = cursor.getString(0);
                if (str != null && !TextUtils.isEmpty(str.trim())) {
                    years.add(str);
                }
            }
            cursor.close();
        }
        close();
        return years;
    }

    public Map<String, List<String>> getRegionsByCategory(int categoryId) {
        open();
        Map<String, List<String>> regionsGroupByCountry = new HashMap<String, List<String>>();
        String sqlQueryString = String.format("select distinct %1$s, %2$s from %3$s where drink_category=%4$s order by %2$s",
                DatabaseSqlHelper.ITEM_REGION,
                DatabaseSqlHelper.ITEM_COUNTRY,
                DatabaseSqlHelper.ITEM_TABLE,
                categoryId);

        Cursor cursor = getDatabase().rawQuery(sqlQueryString, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String region = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_REGION));
                String country = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_COUNTRY));
                List<String> regions;
                if (regionsGroupByCountry.containsKey(country)) {
                    regions = regionsGroupByCountry.get(country);
                    if (region != null && !TextUtils.isEmpty(region.trim()) && !regions.contains(region)) {
                        regions.add(region);
                    }
                } else {
                    regions = new ArrayList<String>();
                    if (region != null && !TextUtils.isEmpty(region.trim())) {
                        regions.add(region);
                    }
                    regionsGroupByCountry.put(country, regions);
                }
            }
            cursor.close();
        }
        close();
        return regionsGroupByCountry;
    }

    public Map<Integer, List<String>> getClassificationsByCategory(int categoryId) {
        open();
        Map<Integer, List<String>> classificationsByProductType = new HashMap<Integer, List<String>>();
        String sqlQueryString = String.format("select distinct %1$s, %2$s from %3$s where drink_category=%4$s order by %2$s",
                DatabaseSqlHelper.ITEM_CLASSIFICATION,
                DatabaseSqlHelper.ITEM_PRODUCT_TYPE,
                DatabaseSqlHelper.ITEM_TABLE,
                categoryId);

        Cursor cursor = getDatabase().rawQuery(sqlQueryString, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String classification = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_CLASSIFICATION));
                Integer productType = cursor.getInt(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRODUCT_TYPE));
                List<String> classifications;
                if (classificationsByProductType.containsKey(productType)) {
                    classifications = classificationsByProductType.get(productType);
                    if (classification != null && !TextUtils.isEmpty(classification.trim()) && !classifications.contains(classification)) {
                        classifications.add(classification);
                    }
                } else {
                    classifications = new ArrayList<String>();
                    if (classification != null && !TextUtils.isEmpty(classification.trim())) {
                        classifications.add(classification);
                    }
                    classificationsByProductType.put(productType, classifications);
                }
            }
            cursor.close();
        }
        close();
        return classificationsByProductType;
    }

    public void insertListData(List<Item> items) {
        open();
        getDatabase().beginTransaction();
        try {
            String insertSql = "INSERT OR REPLACE INTO " + DatabaseSqlHelper.ITEM_TABLE + " (" +
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
            for (Item item : items) {
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
            }
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
        close();
    }

    public void insertListDeprecatedData(List<DeprecatedItem> items) {
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
                    DatabaseSqlHelper.ITEM_DRINK_CATEGORY + ", " +
                    DatabaseSqlHelper.ITEM_PRODUCT_TYPE + ", " +
                    DatabaseSqlHelper.ITEM_CLASSIFICATION + ", " +
                    DatabaseSqlHelper.ITEM_DRINK_TYPE + ", " +
                    DatabaseSqlHelper.ITEM_VOLUME + ")" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement insertStatement = getDatabase().compileStatement(insertSql);
            for (DeprecatedItem item : items) {
                insertStatement = bindString(insertStatement, 1, item.getItemID());
                insertStatement = bindString(insertStatement, 2, item.getDrinkID());
                insertStatement = bindString(insertStatement, 3, item.getName());
                insertStatement = bindString(insertStatement, 4, item.getLocalizedName());
                insertStatement = bindString(insertStatement, 5, item.getManufacturer());
                insertStatement = bindString(insertStatement, 6, item.getLocalizedManufacturer());
                insertStatement = bindString(insertStatement, 7, item.getCountry());
                insertStatement = bindString(insertStatement, 8, item.getRegion());
                insertStatement = bindString(insertStatement, 9, item.getBarcode());
                insertStatement = bindInteger(insertStatement, 10, item.getDrinkCategory().ordinal());
                insertStatement = bindInteger(insertStatement, 11, item.getProductType().ordinal());
                insertStatement = bindString(insertStatement, 12, item.getClassification());
                insertStatement = bindString(insertStatement, 13, item.getDrinkType());
                insertStatement = bindFloat(insertStatement, 14, item.getVolume());
                insertStatement.execute();
            }
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
        close();
    }

    public void updatePriceList(List<ItemPrice> priceList) {
        open();
        getDatabase().beginTransaction();
        try {
            String updateSql = "UPDATE " + DatabaseSqlHelper.ITEM_TABLE + " SET " +
                    DatabaseSqlHelper.ITEM_PRICE + " = ?, " +
                    DatabaseSqlHelper.ITEM_PRICE_MARKUP + " = ? WHERE " +
                    DatabaseSqlHelper.ITEM_ID + " = ?";
            SQLiteStatement updateStatement = getDatabase().compileStatement(updateSql);
            for (ItemPrice price : priceList) {
                updateStatement = bindFloat(updateStatement, 1, price.getPrice());
                updateStatement = bindFloat(updateStatement, 2, price.getPriceMarkup());
                updateStatement = bindString(updateStatement, 3, price.getItemID());
                updateStatement.execute();
            }
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
        close();
    }

    public void insertListFeaturedData(List<FeaturedItem> items) {
        open();
        getDatabase().beginTransaction();
        try {
            String insertSql = "INSERT OR REPLACE INTO " + DatabaseSqlHelper.FEATURED_ITEM_TABLE + " (" +
                    DatabaseSqlHelper.FEATURED_ITEM_ID + ", " +
                    DatabaseSqlHelper.FEATURED_ITEM_CATEGORY_ID + ") VALUES (?, ?)";

            SQLiteStatement insertStatement = getDatabase().compileStatement(insertSql);
            for (FeaturedItem item : items) {
                insertStatement = bindString(insertStatement, 1, item.getItemID());
                insertStatement = bindInteger(insertStatement, 2, item.getCategoryID());
                insertStatement.execute();
            }
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
        close();
    }

//    public void updateFeaturedList(List<FeaturedItem> featuredList) {
//        open();
//        getDatabase().beginTransaction();
//        try {
//            String updateSql = "UPDATE " + DatabaseSqlHelper.ITEM_TABLE + " SET " +
//                    DatabaseSqlHelper.ITEM_MAIN_FEATURED + " = ?, " +
//                    DatabaseSqlHelper.ITEM_FEATURED + " = ? WHERE " +
//                    DatabaseSqlHelper.ITEM_ID + " = ?";
//            SQLiteStatement updateStatement = getDatabase().compileStatement(updateSql);
//            for (FeaturedItem featured : featuredList) {
//                updateStatement = bindInteger(updateStatement, 1, featured.isMainFeatured() ? 1 : 0);
//                updateStatement = bindInteger(updateStatement, 2, featured.isFeatured() ? 1 : 0);
//                updateStatement = bindString(updateStatement, 3, featured.getItemID());
//                updateStatement.execute();
//            }
//            getDatabase().setTransactionSuccessful();
//        } finally {
//            getDatabase().endTransaction();
//        }
//        close();
//    }

    public Item getItemById(String itemId) {
        open();
        String formatSelectScript = "select * from %1$s where %2$s = '%3$s'";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_TABLE, DatabaseSqlHelper.ITEM_ID, itemId);
        Cursor cursor = getDatabase().rawQuery(selectSql, null);
        Item item = null;
        if (cursor != null && cursor.moveToFirst()) {
            item = new Item();
            item.setItemID(cursor.getString(0));
            item.setDrinkID(cursor.getString(1));
            item.setName(cursor.getString(2));
            item.setLocalizedName(cursor.getString(3));
            item.setManufacturer(cursor.getString(4));
            item.setLocalizedManufacturer(cursor.getString(5));
            item.setPrice(cursor.getFloat(6));
            item.setPriceMarkup(cursor.getFloat(7));
            item.setCountry(cursor.getString(8));
            item.setRegion(cursor.getString(9));
            item.setBarcode(cursor.getString(10));
            item.setProductType(ProductType.getProductType(cursor.getInt(11)));
            item.setClassification(cursor.getString(12));
            item.setDrinkCategory(DrinkCategory.getDrinkCategory(cursor.getInt(13)));
            item.setColor(ItemColor.getColor(cursor.getInt(14)));
            item.setStyle(cursor.getString(15));
            item.setSweetness(Sweetness.getSweetness(cursor.getInt(16)));
            item.setYear(cursor.getInt(17));
            item.setVolume(cursor.getFloat(18));
            item.setDrinkType(cursor.getString(19));
            item.setAlcohol(cursor.getString(20));
            item.setBottleHiResolutionImageFilename(cursor.getString(21));
            item.setBottleLowResolutionImageFilename(cursor.getString(22));
            item.setStyleDescription(cursor.getString(23));
            item.setAppelation(cursor.getString(24));
            item.setServingTempMin(cursor.getString(25));
            item.setServingTempMax(cursor.getString(26));
            item.setTasteQualities(cursor.getString(27));
            item.setVintageReport(cursor.getString(28));
            item.setAgingProcess(cursor.getString(29));
            item.setProductionProcess(cursor.getString(30));
            item.setInterestingFacts(cursor.getString(31));
            item.setLabelHistory(cursor.getString(32));
            item.setGastronomy(cursor.getString(33));
            item.setVineyard(cursor.getString(34));
            item.setGrapesUsed(cursor.getString(35));
            item.setRating(cursor.getString(36));
            item.setQuantity(cursor.getFloat(37));
            cursor.close();
        }
        close();
        return item;
    }

    public Cursor getItemByBarcode(String itemBarcode) {
        open();
        String orderBy = "";
        String from = DatabaseSqlHelper.ITEM_TABLE;
        String where = String.format(COMPARE_STRING,
                DatabaseSqlHelper.ITEM_BARCODE,
                itemBarcode);
        String selectSql = String.format(SELECT_ITEMS_FROM_DRINK_ID,
                DatabaseSqlHelper.ITEM_ID + " as _id",
                DatabaseSqlHelper.ITEM_NAME,
                DatabaseSqlHelper.ITEM_LOCALIZED_NAME,
                DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME,
                DatabaseSqlHelper.ITEM_PRODUCT_TYPE,
                DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
                "0 as image",
                DatabaseSqlHelper.ITEM_PRICE,
                DatabaseSqlHelper.ITEM_YEAR,
                DatabaseSqlHelper.ITEM_QUANTITY,
                DatabaseSqlHelper.ITEM_COLOR,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                from,
                where,
                orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Item getItemByBarcodeTypeItem(String itemBarcode) {
        open();
//        String formatSelectScript = "select %1$s, %2$s, %3$s, %4$s, %5$s, %6$s,%7$s from %8$s where %9$s = '%10$s'";
//        String selectSql = String.format(formatSelectScript,
//                DatabaseSqlHelper.ITEM_ID + " as _id", DatabaseSqlHelper.ITEM_DRINK_ID, DatabaseSqlHelper.ITEM_NAME,
//                DatabaseSqlHelper.ITEM_LOCALIZED_NAME, DatabaseSqlHelper.ITEM_PRICE,
//                DatabaseSqlHelper.ITEM_VOLUME, DatabaseSqlHelper.ITEM_DRINK_CATEGORY, DatabaseSqlHelper.ITEM_TABLE,
//                DatabaseSqlHelper.ITEM_BARCODE, itemBarcode);
        String formatSelectScript = "select * from %1$s where %2$s = '%3$s'";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_TABLE, DatabaseSqlHelper.ITEM_BARCODE, itemBarcode);
        Cursor cursor = getDatabase().rawQuery(selectSql, null);
        Item item = null;
        if (cursor != null && cursor.moveToFirst()) {
            item = new Item();
            item.setItemID(cursor.getString(0));
            item.setDrinkID(cursor.getString(1));
            item.setName(cursor.getString(2));
            item.setLocalizedName(cursor.getString(3));
            item.setManufacturer(cursor.getString(4));
            item.setLocalizedManufacturer(cursor.getString(5));
            item.setPrice(cursor.getFloat(6));
            item.setPriceMarkup(cursor.getFloat(7));
            item.setCountry(cursor.getString(8));
            item.setRegion(cursor.getString(9));
            item.setBarcode(cursor.getString(10));
            item.setProductType(ProductType.getProductType(cursor.getInt(11)));
            item.setClassification(cursor.getString(12));
            item.setDrinkCategory(DrinkCategory.getDrinkCategory(cursor.getInt(13)));
            item.setColor(ItemColor.getColor(cursor.getInt(14)));
            item.setStyle(cursor.getString(15));
            item.setSweetness(Sweetness.getSweetness(cursor.getInt(16)));
            item.setYear(cursor.getInt(17));
            item.setVolume(cursor.getFloat(18));
            item.setDrinkType(cursor.getString(19));
            item.setAlcohol(cursor.getString(20));
            item.setBottleHiResolutionImageFilename(cursor.getString(21));
            item.setBottleLowResolutionImageFilename(cursor.getString(22));
            item.setStyleDescription(cursor.getString(23));
            item.setAppelation(cursor.getString(24));
            item.setServingTempMin(cursor.getString(25));
            item.setServingTempMax(cursor.getString(26));
            item.setTasteQualities(cursor.getString(27));
            item.setVintageReport(cursor.getString(28));
            item.setAgingProcess(cursor.getString(29));
            item.setProductionProcess(cursor.getString(30));
            item.setInterestingFacts(cursor.getString(31));
            item.setLabelHistory(cursor.getString(32));
            item.setGastronomy(cursor.getString(33));
            item.setVineyard(cursor.getString(34));
            item.setGrapesUsed(cursor.getString(35));
            item.setRating(cursor.getString(36));
            item.setQuantity(cursor.getFloat(37));
            cursor.close();
        }
        close();
        return item;
    }

    public int getCountBarcode(String barcode) {
        int count = 0;
        open();
        String formatSelectScript = "SELECT count(%2$s) FROM %1$s WHERE %2$s = '%3$s' GROUP BY %2$s";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_TABLE, DatabaseSqlHelper.ITEM_BARCODE, barcode);
        Cursor c = getDatabase().rawQuery(selectSql, null);
        if (c != null) {
            if (c.moveToFirst()) {
            count = c.getInt(0);
            }
            c.close();
        }
        close();
        return count;
    }

    public Map<String, List<String>> getWinesGroupByDrinkId() {
        open();
        String formatSelectScript = "select %1$s, %2$s from %3$s order by %2$s";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_NAME, DatabaseSqlHelper.ITEM_DRINK_ID,
                DatabaseSqlHelper.ITEM_TABLE);
        Cursor cursor = getDatabase().rawQuery(selectSql, null);
        Map<String, List<String>> wines = null;
        if (cursor != null) {
            wines = new HashMap<String, List<String>>();
            while (cursor.moveToNext()) {
                String itemName = cursor.getString(0);
                String drinkId = cursor.getString(1);
                if (!wines.containsKey(drinkId)) {
                    wines.put(drinkId, new ArrayList<String>());
                }
                wines.get(drinkId).add(itemName);
            }
            cursor.close();
        }
        close();
        return wines;
    }

    public Cursor getFeaturedMainItems() {
        open();
        String from = String.format(FORMAT_FROM_TWO_TABLE,
                DatabaseSqlHelper.ITEM_TABLE,
                TABLE_ONE,
                DatabaseSqlHelper.FEATURED_ITEM_TABLE,
                TABLE_TWO);
        String join = String.format(FORMAT_JOIN_TWO_TABLE,
                TABLE_ONE,
                DatabaseSqlHelper.ITEM_ID,
                TABLE_TWO,
                DatabaseSqlHelper.FEATURED_ITEM_ID);
        String whereCategory = String.format(COMPARE,
                DatabaseSqlHelper.FEATURED_ITEM_CATEGORY_ID,
                RANDOM);
        String where = String.format(AND, join, whereCategory);
        String selectSql = String.format(SELECT_ITEMS_FROM_RANDOM,
                TABLE_ONE + "." + DatabaseSqlHelper.ITEM_ID + " as _id",
                DatabaseSqlHelper.ITEM_NAME,
                DatabaseSqlHelper.ITEM_LOCALIZED_NAME,
                DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME,
                DatabaseSqlHelper.ITEM_PRODUCT_TYPE,
                DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
                "0 as image",
                DatabaseSqlHelper.ITEM_PRICE,
                DatabaseSqlHelper.ITEM_YEAR,
                DatabaseSqlHelper.ITEM_QUANTITY,
                DatabaseSqlHelper.ITEM_COLOR,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                from,
                where,
                DatabaseSqlHelper.ITEM_DRINK_ID);
        return getDatabase().rawQuery(selectSql, null);
    }

    private String createSelectScript(int scriptType, Object[] scriptParams) {
        String result = null;
        String formatQuery = FORMAT_QUERY_OTHER;
        if (scriptType == SCRIPT_TYPE_WINE) {
            formatQuery = FORMAT_QUERY_WINE;
        }
        if (scriptParams != null) {
            result = String.format(formatQuery, scriptParams);
        }
        return result;
    }

    //TODO refactor: переименовать метод и переписать через String.format
    private String getWhereBySearch(String query) {
//        String prepareQuery = " '%" + query + "%'";
//        String result = "(" + DatabaseSqlHelper.ITEM_LOCALIZED_NAME + " LIKE" + prepareQuery + " or "
//                + DatabaseSqlHelper.ITEM_NAME + " LIKE" + prepareQuery + ")";
//        if (categoryId != null) {
//            result = " and " + result;
//        }
        String formatQuery = "%" + query + "%";
        String onePart = String.format(LIKE, DatabaseSqlHelper.ITEM_LOCALIZED_NAME, formatQuery);
        String twoPart = String.format(LIKE, DatabaseSqlHelper.ITEM_NAME, formatQuery);
        return String.format(OR, onePart, twoPart);
    }


}
