package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.DeprecatedItem;
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
    private final static String FORMAT_ORDER_BY_PRICE_MIN = " ORDER BY MIN(%s)";
    private final static String FORMAT_ORDER_BY_PRICE = " ORDER BY %s";
    private final static String FORMAT_ORDER_BY_NAME = " ORDER BY %s";

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

    public Cursor getItemsByCategory(int categoryId, String orderByField) {
        open();
        String formatSelectScript = getSelectCategoryStringByCategoryId(categoryId);
        if (formatSelectScript != null) {
            String selectSql = String.format(formatSelectScript,
                    DatabaseSqlHelper.ITEM_ID + " as _id",
                    DatabaseSqlHelper.ITEM_NAME,
                    DatabaseSqlHelper.ITEM_LOCALIZED_NAME,
                    DatabaseSqlHelper.ITEM_VOLUME,
                    DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME,
                    DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
                    "0 as image",
                    DatabaseSqlHelper.ITEM_PRICE,
                    DatabaseSqlHelper.ITEM_DRINK_ID,
                    DatabaseSqlHelper.ITEM_DRINK_ID,
                    DatabaseSqlHelper.ITEM_TABLE);
            if (orderByField != null) {
                String formatOrder = FORMAT_ORDER_BY_PRICE_MIN;
                if (orderByField.equals(DatabaseSqlHelper.ITEM_NAME)) {

                    formatOrder = FORMAT_ORDER_BY_NAME;
                }
                selectSql += String.format(formatOrder, orderByField);
            }
            return getDatabase().rawQuery(selectSql, null);
        } else {
            return null;
        }
    }

    public Cursor getItemsByDrinkId(String drinkId, String orderByField) {
        open();
        String selectSql = String.format(SCRIPT_SELECT_DRINK_ID,
                DatabaseSqlHelper.ITEM_ID + " as _id",
                DatabaseSqlHelper.ITEM_NAME,
                DatabaseSqlHelper.ITEM_LOCALIZED_NAME,
                DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME,
                DatabaseSqlHelper.ITEM_DRINK_CATEGORY, "0 as image",
                DatabaseSqlHelper.ITEM_PRICE,
                DatabaseSqlHelper.ITEM_DRINK_ID,
                DatabaseSqlHelper.ITEM_TABLE,
                DatabaseSqlHelper.ITEM_DRINK_ID, drinkId);
        if (orderByField != null) {
            String formatOrder = FORMAT_ORDER_BY_PRICE;
            if (orderByField.equals(DatabaseSqlHelper.ITEM_NAME)) {
                formatOrder = FORMAT_ORDER_BY_NAME;
            }
            selectSql += String.format(formatOrder, orderByField);
        }
        return getDatabase().rawQuery(selectSql, null);
    }

    //TODO refactor: переименовать, заменить конкантенацию на String.format, метод дублируется с getItemsByCategory
    public Cursor getSearchItemsByCategory(Integer categoryId, String query, String orderByField) {
        open();
        String formatSelectScript;
        if (categoryId == null) {
            formatSelectScript = FIRST_PART_SELECT_SCRIPT
                    + " WHERE %s GROUP BY " + DatabaseSqlHelper.ITEM_DRINK_ID;
        } else {
            formatSelectScript = getSelectCategoryStringByCategoryIdSearch(categoryId, getSelectByQuery(categoryId, query));
        }
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_ID + " as _id", DatabaseSqlHelper.ITEM_NAME,
                DatabaseSqlHelper.ITEM_LOCALIZED_NAME, DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME, DatabaseSqlHelper.ITEM_DRINK_CATEGORY, "0 as image",
                DatabaseSqlHelper.ITEM_PRICE, DatabaseSqlHelper.ITEM_DRINK_ID, DatabaseSqlHelper.ITEM_DRINK_ID,
                DatabaseSqlHelper.ITEM_TABLE, getSelectByQuery(categoryId, query));
        if (orderByField != null) {
            String formatOrder = FORMAT_ORDER_BY_PRICE_MIN;
            if (orderByField.equals(DatabaseSqlHelper.ITEM_NAME)) {

                formatOrder = FORMAT_ORDER_BY_NAME;
            }
            selectSql += String.format(formatOrder, orderByField);
        }
        return getDatabase().rawQuery(selectSql, null);
    }

    public List<String> getYearsByCategory(int categoryId) {
        open();
        List<String> years = new ArrayList<String>();
        String selectString = String.format("select distinct %s from %s where", DatabaseSqlHelper.ITEM_YEAR, DatabaseSqlHelper.ITEM_TABLE);
        String formatScript = "%s %s %s";
        if (categoryId == R.id.category_wine_butt || categoryId == 0) {
            formatScript = "%s %s %s %s %s %s %s";
        }
        String sqlQueryString = String.format(formatScript, getSelectParams(selectString, categoryId));
        Cursor cursor = getDatabase().rawQuery(sqlQueryString, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String str = cursor.getString(0);
                if (!TextUtils.isEmpty(str)) {
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
        String selectString = String.format("select distinct %s, %s from %s where", DatabaseSqlHelper.ITEM_REGION, DatabaseSqlHelper.ITEM_COUNTRY, DatabaseSqlHelper.ITEM_TABLE);
        String formatScript = "%s %s %s";
        if (categoryId == R.id.category_wine_butt) {
            formatScript = "%s %s %s %s %s %s %s";
        }
        String sqlQueryString = String.format(formatScript, getSelectParams(selectString, categoryId));
        Cursor cursor = getDatabase().rawQuery(sqlQueryString, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String region = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_REGION));
                String country = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_COUNTRY));
                List<String> regions;
                if (!TextUtils.isEmpty(region)) {
                    if (regionsGroupByCountry.containsKey(country)) {
                        regions = regionsGroupByCountry.get(country);
                        if (!regions.contains(region)) {
                            regions.add(region);
                        }
                    } else {
                        regions = new ArrayList<String>();
                        regions.add(region);
                        regionsGroupByCountry.put(country, regions);
                    }
                }
            }
            cursor.close();
        }
        close();
        return regionsGroupByCountry;
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
                    DatabaseSqlHelper.ITEM_QUANTITY + ", " +
                    DatabaseSqlHelper.ITEM_MAIN_FEATURED + ", " +
                    DatabaseSqlHelper.ITEM_FEATURED +
                    ") VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                insertStatement = bindInteger(insertStatement, 39, item.isMainFeatured() ? 1 : 0);
                insertStatement = bindInteger(insertStatement, 40, item.isFeatured() ? 1 : 0);
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

    public List<String> getCountries() {
        open();
        String formatSelectScript = "select distinct %1$s from %2$s order by %1$s";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_COUNTRY, DatabaseSqlHelper.ITEM_TABLE);
        Cursor c = getDatabase().rawQuery(selectSql, null);
        List<String> countries = null;
        if (c != null) {
            countries = new ArrayList<String>();
            while (c.moveToNext()) {
                countries.add(c.getString(0));
            }
            c.close();
        }
        close();
        return countries;
    }

    public List<String> getRegionsByCountry(String country) {
        open();
        String formatSelectScript = "select distinct %1$s from %2$s where %3$s = '%4$s' order by %1$s";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_REGION, DatabaseSqlHelper.ITEM_TABLE, DatabaseSqlHelper.ITEM_COUNTRY, country);
        Cursor c = getDatabase().rawQuery(selectSql, null);
        List<String> regions = null;
        if (c != null) {
            regions = new ArrayList<String>();
            while (c.moveToNext()) {
                regions.add(c.getString(0));
            }
            c.close();
        }
        close();
        return regions;
    }

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
            item.setMainFeatured(cursor.getInt(38) > 0);
            item.setFeatured(cursor.getInt(39) > 0);
            cursor.close();
        }
        close();
        return item;
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

    public Cursor getRandomItems() {
        open();
//        String formatSelectScript = "SELECT %s, %s, %s, %s, MIN(%s) AS min_price, %s, %s, COUNT(%s) AS count_bottle FROM %s GROUP BY %s LIMIT 10 ";
//        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_ID, DatabaseSqlHelper.ITEM_NAME,
//                DatabaseSqlHelper.ITEM_LOCALIZED_NAME, DatabaseSqlHelper.ITEM_VOLUME, DatabaseSqlHelper.ITEM_PRICE,
//                DatabaseSqlHelper.ITEM_BOTTLE_HI_RESOLUTION_IMAGE_FILENAME, DatabaseSqlHelper.ITEM_DRINK_CATEGORY,
//                DatabaseSqlHelper.ITEM_DRINK_ID, DatabaseSqlHelper.ITEM_TABLE, DatabaseSqlHelper.ITEM_DRINK_ID);
        String selectSql = String.format(SCRIPT_SELECT_RANDOM, DatabaseSqlHelper.ITEM_ID + " as _id", DatabaseSqlHelper.ITEM_NAME,
                DatabaseSqlHelper.ITEM_LOCALIZED_NAME, DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME, DatabaseSqlHelper.ITEM_DRINK_CATEGORY, "0 as image",
                DatabaseSqlHelper.ITEM_PRICE, DatabaseSqlHelper.ITEM_DRINK_ID, DatabaseSqlHelper.ITEM_DRINK_ID,
                DatabaseSqlHelper.ITEM_TABLE,
                DatabaseSqlHelper.ITEM_DRINK_ID);
        return getDatabase().rawQuery(selectSql, null);
//        List<Item> itemList = null;
//        if (cursor != null) {
//            itemList = new ArrayList<Item>();
//            while (cursor.moveToNext()) {
//                Item item = new Item();
//                item.setItemID(cursor.getString(0));
//                item.setName(cursor.getString(1));
//                item.setLocalizedName(cursor.getString(2));
//                item.setVolume(cursor.getString(3));
//                item.setPrice(cursor.getString(4));
//                item.setBottleHiResolutionImageFilename(cursor.getString(5));
//                item.setDrinkCategory(DrinkCategory.values()[Integer.parseInt(cursor.getString(6))]);
//                item.setDrinkID(cursor.getString(7));
//                itemList.add(item);
//            }
//            cursor.close();
//        }
//        close();
//        return itemList;
    }

    private String getSelectCategoryStringByCategoryId(Integer categoryId) {
        int scriptType = SCRIPT_TYPE_OTHERS;
        if (categoryId == R.id.category_wine_butt) {
            scriptType = SCRIPT_TYPE_WINE;
        }
        return createSelectScript(scriptType, getSelectParams(FIRST_PART_SELECT_SCRIPT, categoryId));
    }

    private Object[] getSelectParams(String firstPartSelectScript, Integer categoryId) {
        switch (categoryId) {
            case R.id.category_wine_butt:
            case 0:
                return new String[]{firstPartSelectScript, DatabaseSqlHelper.ITEM_DRINK_CATEGORY, " = 0 and ",
                        DatabaseSqlHelper.ITEM_STYLE, " <> 1 and ", DatabaseSqlHelper.ITEM_STYLE, " <> 3"};
            case R.id.category_sparkling_butt:
            case 1:
                return new String[]{firstPartSelectScript, DatabaseSqlHelper.ITEM_STYLE, " = 1"};
            case R.id.category_porto_heres_butt:
                return new String[]{firstPartSelectScript, DatabaseSqlHelper.ITEM_STYLE, " = 3"};
            case R.id.category_water_butt:
                return null;
            case R.id.category_spirits_butt:
                return new String[]{firstPartSelectScript, DatabaseSqlHelper.ITEM_DRINK_CATEGORY, " = 1"};
            case R.id.category_sake_butt:
                return new String[]{firstPartSelectScript, DatabaseSqlHelper.ITEM_DRINK_CATEGORY, " = 2"};
            default:
                return null;
        }
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

    private String getSelectCategoryStringByCategoryIdSearch(Integer categoryId, String query) {
        int scriptType = SCRIPT_TYPE_OTHERS;
        if (categoryId == R.id.category_wine_butt) {
            scriptType = SCRIPT_TYPE_WINE;
        }
        return createSelectScriptSearch(scriptType, getSelectParamsSearch(categoryId, query));
    }

    private Object[] getSelectParamsSearch(Integer categoryId, String query) {
        switch (categoryId) {
            case R.id.category_wine_butt:
                return new String[]{FIRST_PART_SELECT_SCRIPT, DatabaseSqlHelper.ITEM_DRINK_CATEGORY, " = 0 and ",
                        DatabaseSqlHelper.ITEM_STYLE, " <> 1 and ", DatabaseSqlHelper.ITEM_STYLE, " <> 3 ", "%s"};
            case R.id.category_sparkling_butt:
                return new String[]{FIRST_PART_SELECT_SCRIPT, DatabaseSqlHelper.ITEM_STYLE, " = 1 ", "%s"};
            case R.id.category_porto_heres_butt:
                return new String[]{FIRST_PART_SELECT_SCRIPT, DatabaseSqlHelper.ITEM_STYLE, " = 3 ", "%s"};
            case R.id.category_water_butt:
                return null;
            case R.id.category_spirits_butt:
                return new String[]{FIRST_PART_SELECT_SCRIPT, DatabaseSqlHelper.ITEM_DRINK_CATEGORY, " = 1 ", "%s"};
            case R.id.category_sake_butt:
                return new String[]{FIRST_PART_SELECT_SCRIPT, DatabaseSqlHelper.ITEM_DRINK_CATEGORY, " = 2 ", "%s"};
            default:
                return null;
        }
    }

    private String createSelectScriptSearch(int scriptType, Object[] scriptParams) {
        String result = null;
        String formatQuery = FORMAT_QUERY_OTHER_SEARCH;
        if (scriptType == SCRIPT_TYPE_WINE) {
            formatQuery = FORMAT_QUERY_WINE_SEARCH;
        }
        if (scriptParams != null) {
            result = String.format(formatQuery, scriptParams);
        }
        return result;
    }

    //TODO refactor: переименовать метод и переписать через String.format
    private String getSelectByQuery(Integer categoryId, String query) {
        String prepareQuery = " '%" + query + "%'";
        String result = "(" + DatabaseSqlHelper.ITEM_LOCALIZED_NAME + " LIKE" + prepareQuery + " or "
                + DatabaseSqlHelper.ITEM_NAME + " LIKE" + prepareQuery + ")";
        if (categoryId != null) {
            result = " and " + result;
        }
        return result;
    }

}
