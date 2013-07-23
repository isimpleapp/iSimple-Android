package com.treelev.isimple.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import com.treelev.isimple.domain.db.DeprecatedItem;
import com.treelev.isimple.domain.db.FeaturedItem;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.db.ItemPrice;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.ItemColor;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.enumerable.item.Sweetness;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

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
    private final static String FORMAT_ORDER_BY_PRICE = " ORDER BY %s";
    private final static String FORMAT_ORDER_BY_NAME = " ORDER BY %s";


    private final static String SELECT_ITEMS_FROM = "SELECT %s, %s, %s, %s, %s, %s, %s, %s, MIN(%s) as price, %s, %s, %s, %s, %s, COUNT(%s) FROM %s  WHERE %s GROUP BY %s  %s";
    private final static String SELECT_ITEMS_FROM_RANDOM = "SELECT %s, %s, %s, %s, %s, %s, %s, %s, MIN(%s) as price, %s, %s, %s, %s, COUNT(%s) FROM %s WHERE %s GROUP BY %s";
    private final static String SELECT_ITEMS_FROM_DRINK_ID = "SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,  %s, %s, %s, %s FROM %s WHERE %s %s";
    private final static String SELECT_ITEMS_FROM_BARCODE = "SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s %s";
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

    public int getTableDataCount() {
        return getTableDataCount(DatabaseSqlHelper.ITEM_TABLE);
    }

    public Cursor getFeaturedItemsByCategory(int categoryId, String orderByField) {
        open();
        String formatScript = "SELECT * " +
                "FROM " +
                    "(" +
                        "SELECT t0.*, MIN(price1) AS price, COUNT(t0.drink_id) AS count, MAX(item_left_overs1) as item_left_overs " +
                        "FROM " +
                        "(" +
                            "SELECT t1.item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, drink_category, " +
                           "(case when ifnull(price, '') = '' then (999999) else price end) as price1," +
                            " year, quantity, color, (case when ifnull(drink_id, '') = '' then ('e' || t1.item_id) else drink_id end) as drink_id, is_favourite, item_left_overs AS item_left_overs1 FROM item AS t1, (SELECT DISTINCT * FROM featured_item ) AS t2 WHERE t1.item_id = t2.item_id AND category_id = %s ORDER BY t1.item_left_overs " +
                        ") " +
                        "AS t0 " +
                        "GROUP BY t0.drink_id " +
                    ") " +
                "WHERE item_left_overs > 0 " +
                "ORDER BY %s";
        String selectSql = String.format(formatScript, categoryId, orderByField);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getAllItemsByCategory(int categoryId, String orderByField) {
        open();
        String formatScript = "SELECT * " +
                "FROM " +
                        "(" +
                        "SELECT t0.*, MIN(price1) AS price, COUNT(t0.drink_id) AS count, MAX(item_left_overs1) AS item_left_overs " +
                        "FROM " +
                            "(" +
                                "SELECT t1.item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, drink_category, " +
                                "(case when ifnull(price, '') = '' then (999999) else price end) as price1," +
                                " year, quantity, color, (case when ifnull(drink_id, '') = '' then ('e' || t1.item_id) else drink_id end) as drink_id, is_favourite, item_left_overs AS item_left_overs1 FROM item AS t1 WHERE category_id = %s  ORDER BY t1.item_left_overs" +
                            ") AS t0 " +
                        "GROUP BY t0.drink_id " +
                        ") " +
                "WHERE item_left_overs > 0 " +
                "ORDER BY %s";
        String selectSql = String.format(formatScript, categoryId, orderByField);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getFeaturedItemsByCategory(int categoryId, String locationId, String orderByField) {
        open();
        String formatScript = "SELECT * " +
                "FROM " +
                    "(" +
                        "SELECT t0.*, MIN(price1) AS price, COUNT(t0.drink_id) AS count, MAX(item_left_overs1) as item_left_overs " +
                        "FROM " +
                        "(" +
                            "SELECT t1.item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, t1.drink_category as drink_category, " +
                                "(case when ifnull(t1.price, '') = '' then (999999) else price end) as price1, " +
                                " year, quantity, color, (case when ifnull(drink_id, '') = '' then ('e' || t1.item_id) else drink_id end) as drink_id, is_favourite, item_left_overs AS item_left_overs1 FROM item AS t1 INNER JOIN (SELECT item_id, location_id FROM item_availability) AS t2 ON t1.item_id = t2.item_id WHERE t1.drink_category = %s AND location_id = '%s'  ORDER BY t1.item_left_overs" +
                        ") AS t0 " +
                        "GROUP BY t0.drink_id " +
                    ") " +
                "WHERE item_left_overs > 0 " +
                "ORDER BY %s";
        String selectSql = String.format(formatScript, categoryId, locationId, orderByField);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getItemsByDrinkId(String drinkId, String locationID, String query, boolean search, String orderByField) {
        open();
        String strLocationID = "";
        String strInnerJoin = "";
        if(locationID != null){
            strLocationID = String.format("AND location_id = '%s'", locationID);
            strInnerJoin = "INNER JOIN (SELECT item_id, location_id FROM item_availability) AS t0 ON item.item_id = t0.item_id ";
        }
        String searchWhere = "";
        if(search){
            searchWhere = getSearchWhereByDrinkID(query);
        }
        String orderBy = "";
        if (orderByField != null) {
            orderBy = "ORDER BY " + orderByField + ", year";
        }
        String formatScript = "SELECT item.item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, drink_category, " +
                "(case when ifnull(price, '') = '' then (999999) else price end) as price, "  +
                "year,  quantity, color, drink_id, is_favourite, " +
                "(case when ifnull(price, '') = '' then (0) else item_left_overs end) as item_left_overs " +
                "FROM item %s " +
                "WHERE drink_id = '%s' AND item_left_overs > 0" +
                " %s " + //locationID
                "  %s " + //search
                " %s";
        String selectSql = String.format(formatScript, strInnerJoin, drinkId, strLocationID, searchWhere, orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getItemsByDrinkId(String drinkId, String filterQuery, String locationID, String orderByField) {
        open();
        String strLocationID = "";
        String strInnerJoin = "";
        if(locationID != null){
            strLocationID = String.format("AND location_id = '%s'", locationID);
            strInnerJoin = "INNER JOIN (SELECT item_id, location_id FROM item_availability) AS t0 ON item.item_id = t0.item_id ";
        }

        String orderBy = "";
        if (orderByField != null) {
            orderBy = "ORDER BY " + orderByField + ", year";
        }
        String formatSelectScript = "SELECT item.item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, " +
                "drink_category, " +
                "(case when ifnull(price, '') = '' then (999999) else price end) as price, " +
                "year,  " +
                "quantity, color, drink_id, is_favourite, " +
                "(case when ifnull(price, '') = '' then (0) else item_left_overs end) as item_left_overs " +
                "FROM item %s " +
                "WHERE drink_id = '%s' AND (%s) AND item_left_overs > 0" +
                " %s " +
                " %s";
        String selectSql = String.format(formatSelectScript, strInnerJoin, drinkId, filterQuery, strLocationID, orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getItemsByDrinkIdPreOrder(String drinkId, String locationID, String query, boolean search, String orderByField) {
        open();
        String strLocationID = "";
        String strInnerJoin = "";
        if(locationID != null){
            strLocationID = String.format("AND location_id = '%s'", locationID);
            strInnerJoin = "INNER JOIN (SELECT item_id, location_id FROM item_availability) AS t0 ON item.item_id = t0.item_id ";
        }
        String orderBy = "";
        if (orderByField != null) {
            orderBy = "ORDER BY " + orderByField + ", year";
        }
        String searchWhere = "";
        if(search){
            searchWhere = searchWhere = getSearchWhereByDrinkID(query);
        }
        String formatScript = "SELECT item.item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, drink_category, " +
                "(case when ifnull(price, '') = '' then (999999) else price end) as price, " +
                "year,  quantity, color, drink_id, is_favourite, " +
                "(case when ifnull(price, '') = '' then (0) else item_left_overs end) as item_left_overs " +
                "FROM item %s " +
                "WHERE drink_id = '%s' AND item_left_overs = 0 " +
                " %s " + //locationID
                "  %s " + //search
                "%s";
        String selectSql = String.format(formatScript, strInnerJoin, drinkId, strLocationID, searchWhere, orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getItemsByDrinkIdPreOrder(String drinkId, String filterQuery, String locationID, String orderByField) {
            open();
            String strLocationID = "";
            String strInnerJoin = "";
            if(locationID != null){
                strLocationID = String.format("AND location_id = '%s'", locationID);
            strInnerJoin = "INNER JOIN (SELECT item_id, location_id FROM item_availability) AS t0 ON item.item_id = t0.item_id ";
        }

        String orderBy = "";
        if (orderByField != null) {
            orderBy = "ORDER BY " + orderByField + ", year";
        }
        String formatSelectScript = "SELECT item.item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, " +
                "drink_category, " +
                "(case when ifnull(price, '') = '' then (999999) else price end) as price, " +
                "year,  " +
                "quantity, color, drink_id, is_favourite, " +
                "(case when ifnull(price, '') = '' then (0) else item_left_overs end) as item_left_overs " +
                "FROM item %s " +
                "WHERE drink_id = '%s' AND (%s) AND item_left_overs = 0" +
                " %s " +
                " %s";
        String selectSql = String.format(formatSelectScript, strInnerJoin, drinkId, filterQuery, strLocationID, orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    private String getSearchWhereByDrinkID(String query){
        String searchWhere;
        switch (mSectionWhereForPreOrderSearch){
            case 0:
                searchWhere = String.format("AND (%s)", getWhereBySearchFirst(query));
                break;
            case 1:
                searchWhere = String.format("AND (%s)", getWhereBySearchSecond(query));
                break;
            case 2:
                searchWhere = String.format("AND (%s)", getWhereBySearchThird(query));
                break;
            case 3:
                searchWhere = String.format("AND (%s)", getWhereBySearchFourth(query));
                break;
            default:
                searchWhere = "";
        }
        return searchWhere;
    }

    public Cursor getFilteredItemsByCategory(Integer categoryId, String query, String orderByField) {
        return getFilteredItemsByCategory(categoryId, null, query, orderByField);
    }

    public Cursor getFilteredItemsByCategory(Integer categoryId, String locationId, String whereClause, String orderByField) {
        String strLocationID = "";
        String strInnerJoin = "";
        if(locationId != null){
            strLocationID = String.format("AND location_id = '%s'", locationId);
            strInnerJoin = "INNER JOIN (SELECT item_id, location_id FROM item_availability) AS t2 ON t1.item_id = t2.item_id ";
        }
        whereClause = whereClause.length() > 0 ? "AND " + whereClause : "AND price < 0";
        whereClause = whereClause.replace("item", "t1");
        String formatScript = "SELECT * " +
                "FROM " +
                    "(" +
                    "SELECT t0.*, MIN(price1) AS price, COUNT(t0.drink_id) AS count, MAX(item_left_overs1) as item_left_overs  " +
                    "FROM " +
                    "(" +
                        "SELECT t1.item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, drink_category, " +
                    "(case when ifnull(price, '') = '' then (999999) else price end) as price1, " +
                    " year, quantity, color, (case when ifnull(t1.drink_id, '') = '' then ('e' || t1.item_id) else t1.drink_id end) AS drink_id, is_favourite, item_left_overs AS item_left_overs1 FROM item AS t1 %4$s WHERE t1.drink_category=%1$s %2$s %5$s  ORDER BY t1.item_left_overs "+
                    ") AS t0 GROUP BY t0.drink_id " +
                    ")" +
                "WHERE item_left_overs > 0 " +
                "ORDER BY %3$s";
                String selectSql = String.format(formatScript,
                categoryId, whereClause, orderByField, strInnerJoin, strLocationID);
        open();
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getFilteredItemsByCategoryPreOrder(Integer categoryId, String locationId, String whereClause, String orderByField) {
        String strLocationID = "";
        String strInnerJoin = "";
        if(locationId != null){
            strLocationID = String.format("AND location_id = '%s'", locationId);
            strInnerJoin = "INNER JOIN (SELECT item_id, location_id FROM item_availability) AS t2 ON t1.item_id = t2.item_id ";
        }
        whereClause = whereClause.length() > 0 ? "AND " + whereClause : "AND price < 0";
        whereClause = whereClause.replace("item", "t1");
        String formatScript = "SELECT * " +
                "FROM " +
                "(" +
                    "SELECT t0.*, MIN(price1) AS price, COUNT(t0.drink_id) AS count, MAX(item_left_overs1) as item_left_overs " +
                    "FROM " +
                     "(" +
                        "SELECT t1.item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, drink_category, " +
                            "(case when ifnull(price, '') = '' then (999999) else price end) as price1, " +
                            "year, quantity, color, (case when ifnull(t1.drink_id, '') = '' then ('e' || t1.item_id) else t1.drink_id end) AS drink_id, is_favourite, item_left_overs as item_left_overs1 " +
                "FROM item AS t1 %4$s WHERE t1.drink_category=%1$s %2$s %5$s " +
                     ") AS t0 GROUP BY t0.drink_id " +
                ")" +
                "WHERE item_left_overs = 0 " +
                "ORDER BY %3$s";
        String selectSql = String.format(formatScript,
                categoryId, whereClause, orderByField, strInnerJoin, strLocationID);
        open();
        return getDatabase().rawQuery(selectSql, null);
    }

    public Integer getItemMaxPriceByCategory(Integer categoryId) {
        String selectSql = String.format(
                "SELECT max(price) FROM item WHERE drink_category = %s", categoryId);
        open();
        Cursor cursor = getDatabase().rawQuery(selectSql, null);
        Integer maxValuePrice = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                maxValuePrice = cursor.getInt(0);
            }
            cursor.close();
        }
        return maxValuePrice;
    }

    public Cursor getSearchItemsByCategory(Integer categoryId, String query, String orderByField) {
        open();
        String formatWhereScript = "%s";
        if(categoryId != null){
            formatWhereScript = String.format("drink_category = %s AND %s", categoryId,  "(%s)");
        }
        String formatScript = "SELECT * " +
                "FROM " +
                "(" +
                    "SELECT t0.*, MIN(price1) AS price, COUNT(t0.drink_id) AS count, MAX(item_left_overs1) as item_left_overs  " +
                    "FROM " +
                    "(" +
                        "SELECT item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, drink_category, 0 as image, " +
                        "(case when ifnull(price, '') = '' then (999999) else price end) as price1," +
                        " year, quantity, color, (case when ifnull(drink_id, '') = '' then ('e' || item_id) else drink_id end) AS drink_id, is_favourite, item_left_overs AS item_left_overs1, " +
                        "country, region, style, drink_type, style_description, grapes_used, taste_qualities, vintage_report, aging_process, label_history, gastronomy, vineyard " +
                "FROM item  WHERE %s " +
                    ") AS t0 GROUP BY t0.drink_id " +
                ")" +
                " WHERE item_left_overs > 0 " +
                "ORDER BY %s";
        String where = String.format(formatWhereScript, getWhereBySearchFirst(query));
        String selectSql = String.format(formatScript, where, orderByField);
        Cursor cursor = getDatabase().rawQuery(selectSql, null);
        mSectionWhereForPreOrderSearch = 0;
        if(cursor.getCount() == 0){
            mSectionWhereForPreOrderSearch = 1;
            where = String.format(formatWhereScript, getWhereBySearchSecond(query));
            selectSql = String.format(formatScript, where, orderByField);
            cursor = getDatabase().rawQuery(selectSql, null);
            if(cursor.getCount() == 0){
                mSectionWhereForPreOrderSearch = 2;
                where = String.format(formatWhereScript, getWhereBySearchThird(query));
                selectSql = String.format(formatScript, where, orderByField);
                cursor = getDatabase().rawQuery(selectSql, null);
                if(cursor.getCount() == 0) {
                    mSectionWhereForPreOrderSearch = 3;
                    where = String.format(formatWhereScript, getWhereBySearchFourth(query));
                    selectSql = String.format(formatScript, where, orderByField);
                    cursor = getDatabase().rawQuery(selectSql, null);
                }
            }

        }
        return cursor;
    }

    private static int mSectionWhereForPreOrderSearch;

    public Cursor getSearchItemsByCategory(Integer categoryId, String locationId, String query, String orderByField) {
        open();
        String formatWhereScript = String.format("t1.item_id = t2.item_id AND t1.drink_category = %s AND location_id = '%s' AND (%s)", categoryId, locationId, "%s");
        String formatScript = "SELECT * " +
                "FROM " +
                    "(SELECT t0.*, MIN(t0.price1) AS price, COUNT(t0.drink_id) AS count, MAX(item_left_overs1) AS item_left_overs " +
                    "FROM " +
                    "(" +
                        "SELECT t1.item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, drink_category, 0 as image, " +
                        "(case when ifnull(t1.price, '') = '' then (999999) else t1.price end) as price1," +
                        " year, quantity, color, (case when ifnull(drink_id, '') = '' then ('e' || t1.item_id) else drink_id end) as drink_id, is_favourite, item_left_overs AS item_left_overs1, " +
                        "country, region, style, drink_type, style_description, grapes_used, taste_qualities, vintage_report, aging_process, label_history, gastronomy, vineyard " +
                " FROM item AS t1, item_availability AS t2 WHERE %s " +
                    ") AS t0 GROUP BY t0.drink_id " +
                ")" +
                " WHERE item_left_overs > 0 " +
                "ORDER BY %s";
        String where = String.format(formatWhereScript, getWhereBySearchFirst(query).replace("item", "t1"));
        String selectSql = String.format(formatScript, where, orderByField);
        Cursor cursor = getDatabase().rawQuery(selectSql, null);
        mSectionWhereForPreOrderSearch = 0;
        if(cursor.getCount() == 0){
            mSectionWhereForPreOrderSearch = 1;
            where = String.format(formatWhereScript, getWhereBySearchSecond(query).replace("item", "t1"));
            selectSql = String.format(formatScript, where, orderByField);
            cursor = getDatabase().rawQuery(selectSql, null);
            if(cursor.getCount() == 0){
                mSectionWhereForPreOrderSearch = 2;
                where = String.format(formatWhereScript, getWhereBySearchThird(query).replace("item", "t1"));
                selectSql = String.format(formatScript, where, orderByField);
                cursor = getDatabase().rawQuery(selectSql, null);
                if(cursor.getCount() == 0) {
                    mSectionWhereForPreOrderSearch = 3;
                    where = String.format(formatWhereScript, getWhereBySearchFourth(query).replace("item", "t1"));
                    selectSql = String.format(formatScript, where, orderByField);
                    cursor = getDatabase().rawQuery(selectSql, null);
                }
            }
        }
        return cursor;
    }

    public Cursor getSearchItemsByCategoryPreOrder(Integer categoryId, String query, String orderByField) {
        open();
        String formatWhereScript = "%s";
        if(categoryId != null){
            formatWhereScript = String.format("drink_category = %s AND %s", categoryId,  "(%s)");
        }
        String formatScript = "SELECT * " +
                        "FROM " +
                        "(" +
                        "SELECT t0.*, MIN(price1) AS price, COUNT(t0.drink_id) AS count, MAX(item_left_overs1) AS item_left_overs  " +
                        "FROM " +
                        "(" +
                            "SELECT item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, drink_category, 0 as image, " +
                            "(case when ifnull(price, '') = '' then (999999) else price end) as price1," +
                            " year, quantity, color, (case when ifnull(drink_id, '') = '' then ('e' || item_id) else drink_id end) as drink_id, is_favourite, item_left_overs AS item_left_overs1, " +
                            "country, region, style, drink_type, style_description, grapes_used, taste_qualities, vintage_report, aging_process, label_history, gastronomy, vineyard " +
                        "FROM item  WHERE %s " +
                        ") AS t0 GROUP BY t0.drink_id " +
                        ")" +
                        " WHERE item_left_overs = 0 " +
                        "ORDER BY %s";
        String where = "";
        switch (mSectionWhereForPreOrderSearch){
            case 0:
                where = String.format(formatWhereScript, getWhereBySearchFirst(query));
                break;
            case 1:
                where = String.format(formatWhereScript, getWhereBySearchSecond(query));
                break;
            case 2:
                where = String.format(formatWhereScript, getWhereBySearchThird(query));
                break;
            case 3:
                where = String.format(formatWhereScript, getWhereBySearchFourth(query));
                break;
        }
        String selectSql = String.format(formatScript, where, orderByField);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getSearchItemsByCategoryPreOrder(Integer categoryId, String locationId, String query, String orderByField) {
        open();
        String formatWhereScript = String.format("t1.item_id = t2.item_id AND t1.drink_category = %s AND location_id = '%s' AND (%s)", categoryId, locationId, "%s");
        String formatScript = "SELECT * " +
                "FROM " +
                    "(" +
                    "SELECT t0.*, MIN(t0.price1) AS price, COUNT(t0.drink_id) AS count, MAX(item_left_overs1) as item_left_overs  " +
                    "FROM " +
                        "(SELECT t1.item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, drink_category, 0 as image, " +
                        "(case when ifnull(t1.price, '') = '' then (999999) else t1.price end) as price1," +
                        " year, quantity, color, (case when ifnull(drink_id, '') = '' then ('e' || t1.item_id) else drink_id end) as drink_id, is_favourite, item_left_overs AS item_left_overs1, " +
                        "country, region, style, drink_type, style_description, grapes_used, taste_qualities, vintage_report, aging_process, label_history, gastronomy, vineyard " +
                "FROM item AS t1, item_availability AS t2 WHERE %s " +
                    ") AS t0 " +
                    "GROUP BY t0.drink_id " +
                    ")" +
                " WHERE item_left_overs = 0 " +
                "ORDER BY %s";
        String where = "";
        switch (mSectionWhereForPreOrderSearch){
            case 0:
                where = String.format(formatWhereScript, getWhereBySearchFirst(query).replace("item", "t1"));
                break;
            case 1:
                where = String.format(formatWhereScript, getWhereBySearchSecond(query).replace("item", "t1"));
                break;
            case 2:
                where = String.format(formatWhereScript, getWhereBySearchThird(query).replace("item", "t1"));
                break;
            case 3:
                where = String.format(formatWhereScript, getWhereBySearchFourth(query).replace("item", "t1"));
                break;
        }
        String selectSql = String.format(formatScript, where, orderByField);
        return getDatabase().rawQuery(selectSql, null);
    }

    public List<Integer> getYearsByCategory(int categoryId) {
        open();
        List<Integer> years = new ArrayList<Integer>();
        String sqlQueryString = String.format("select distinct %1$s from %2$s where drink_category=%3$s order by %1$s desc",
                DatabaseSqlHelper.ITEM_YEAR,
                DatabaseSqlHelper.ITEM_TABLE,
                categoryId);
        Cursor cursor = getDatabase().rawQuery(sqlQueryString, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int yearValue = cursor.getInt(0);
                if (yearValue != 0) {
                    years.add(yearValue);
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

    public Map<String, Integer> getProductCountsByCountries(int categoryId) {
        open();
        Map<String, Integer> countries = new HashMap<String, Integer>();
        String sqlQueryString = "SELECT country, count(item_id) FROM item WHERE drink_category = " + categoryId + " GROUP BY country ORDER BY count(item_id) DESC";
        Cursor cursor = getDatabase().rawQuery(sqlQueryString, null);
        if (cursor != null) {
            String country;
            while (cursor.moveToNext()) {
                country = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_COUNTRY));
                countries.put(country, cursor.getInt(1));
            }
            cursor.close();
        }
        close();
        Map<String, Integer> sortedCountry = new ConcurrentSkipListMap<String, Integer>(new IntegerValueComparator(countries));
        sortedCountry.putAll(countries);
        return sortedCountry;
    }

    private class IntegerValueComparator implements Comparator<String> {

        private Map<String, Integer> countries;

        private IntegerValueComparator(Map<String, Integer> countries) {
            this.countries = countries;
        }

        @Override
        public int compare(String lhs, String rhs) {
            Integer i1 = countries.get(lhs);
            Integer i2 = countries.get(rhs);
            int compareValues = i2.compareTo(i1);
            return compareValues == 0 ? rhs.compareTo(lhs) : compareValues;
        }
    }

    public List<String> getRegionsByCountriesCategory(String country, int categoryId) {
        open();
        List<String> regions = new ArrayList<String>();
        String sqlQueryString = "SELECT DISTINCT region FROM [item] WHERE [drink_category] = " + categoryId + " AND [country] = '" + country + "' ORDER BY region";
        Cursor cursor = getDatabase().rawQuery(sqlQueryString, null);
        if (cursor != null) {
            String region;
            while (cursor.moveToNext()) {
                region = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_REGION));
                regions.add(region);
            }
            cursor.close();
        }
        close();
        return regions;
    }

    public Map<Integer, List<String>> getClassificationsByCategory(int categoryId) {
        open();
        Map<Integer, List<String>> classificationsByProductType = new HashMap<Integer, List<String>>();
        String sqlQueryString = String.format("select distinct %1$s, %2$s from %3$s where drink_category=%4$s order by %2$s desc",
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
                    DatabaseSqlHelper.ITEM_QUANTITY + ", " +
                    DatabaseSqlHelper.ITEM_IS_FAVOURITE + ", " +
                    DatabaseSqlHelper.ITEM_LEFT_OVERS +
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
                insertStatement = bindBoolean(insertStatement, 39, item.getFavourite());
                insertStatement = bindInteger(insertStatement, 40, item.getLeftOvers());
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
            String updateSqlFormat = "UPDATE item " +
                    "SET price = " +
                    "(CASE WHEN (SELECT drink_category FROM item WHERE item_id = '%1$s') = 5 THEN %2$s*(SELECT quantity FROM item WHERE item_id = '%1$s') ELSE %2$s END), " +
                    "price_markup = %3$s, item_left_overs = %4$s " +
                    "WHERE item_id = '%1$s'";
            String updateSql;
            for (ItemPrice price : priceList) {
                updateSql = String.format(updateSqlFormat, price.getItemID(), price.getPrice(), price.getPriceMarkup(), price.getLeftOvers());
                getDatabase().execSQL(updateSql);
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

    public void deleteAllFeaturedItemData() {
        open();
        String deleteSql = " DELETE FROM featured_item";
        getDatabase().execSQL(deleteSql);
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
        Item item = createItem(cursor);
        close();
        return item;
    }

    public Cursor getItemByBarcode(String itemBarcode, String orderByFiled) {
        open();
        String orderBy = String.format(FORMAT_ORDER_BY, orderByFiled);
        String formatScript = "SELECT item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, " +
                "drink_category, price, year, " +
                "quantity, color, drink_id, is_favourite " +
                "FROM item " +
                "WHERE barcode = '%s' %s";
        String selectSql = String.format(formatScript,
                itemBarcode,
                orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getItemDeprecatedByBarcode(String itemBarcode, String orderByFiled) {
        open();
        String orderBy = String.format(FORMAT_ORDER_BY, orderByFiled);
        String from = DatabaseSqlHelper.ITEM_DEPRECATED_TABLE;
        String formatScript = "SELECT item_id as _id, name, localized_name, volume, 0 AS bottle_high_res, 0 AS bottle_low_resolution, product_type, drink_category, 0 AS image, 0 AS price, 0 AS year,  0 AS quantity, 0 AS color, drink_id, 0 AS is_favourite " +
                "FROM item_deprecated WHERE barcode = '%s' %s";
        String selectSql = String.format(formatScript,
                itemBarcode,
                orderBy);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Item getItemByBarcodeTypeItem(String itemBarcode) {
        open();
        String formatSelectScript = "select * from %1$s where %2$s = '%3$s'";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_TABLE, DatabaseSqlHelper.ITEM_BARCODE, itemBarcode);
        Cursor cursor = getDatabase().rawQuery(selectSql, null);
        Item item = createItem(cursor);
        close();
        return item;
    }

    public Item getItemDeprecatedByBarcodeTypeItem(String itemBarcode) {
        open();
        String formatSelectScript = "select * from %1$s where %2$s = '%3$s'";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_DEPRECATED_TABLE, DatabaseSqlHelper.ITEM_BARCODE, itemBarcode);
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
            item.setCountry(cursor.getString(6));
            item.setRegion(cursor.getString(7));
            item.setBarcode(cursor.getString(8));
            item.setProductType(ProductType.getProductType(cursor.getInt(9)));
            item.setClassification(cursor.getString(10));
            item.setDrinkCategory(DrinkCategory.getDrinkCategory(cursor.getInt(11)));
            item.setVolume(cursor.getFloat(13));
            item.setDrinkType(cursor.getString(12));
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

    public int getCountBarcodeInDeprecatedTable(String barcode) {
        int count = 0;
        open();
        String formatSelectScript = "SELECT count(%2$s) FROM %1$s GROUP BY %2$s HAVING %2$s = '%3$s'";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_DEPRECATED_TABLE, DatabaseSqlHelper.ITEM_BARCODE, barcode);
        Cursor c = getDatabase().rawQuery(selectSql, null);
        if (c != null) {
            c.moveToNext();
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
        String selectSql = "SELECT * " +
                "FROM " +
                "(" +
                    "SELECT t0.*, MIN(price1) as price, COUNT(t0.drink_id) AS count, MAX(item_left_overs1) as item_left_overs " +
                     "FROM " +
                     "(" +
                        "SELECT t1.item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, drink_category, " +
                        "(case when ifnull(t1.price, '') = '' then (999999) else t1.price end) as price1," +
                        "year, quantity, color,(case when ifnull(drink_id, '') = '' then ('e' || t1.item_id) else drink_id end) as drink_id, is_favourite, item_left_overs AS item_left_overs1  FROM item AS t1, (SELECT DISTINCT *  FROM featured_item ) AS t2 WHERE t1.item_id = t2.item_id AND category_id = -1 ORDER BY t1.item_left_overs" +
                    ") AS t0 " +
                    "GROUP BY t0.drink_id ) " +
                "WHERE item_left_overs > 0 ";
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getAllItems(String orderByField) {
        String formatScript = "SELECT * " +
                "FROM " +
                    "(" +
                    "SELECT t0.*, MIN(price1) as price, COUNT(t0.drink_id) AS count, MAX(item_left_overs1) AS item_left_overs " +
                    "FROM " +
                        "(SELECT item_id as _id, name, localized_name, volume, bottle_high_res, bottle_low_resolution, product_type, drink_category, " +
                        "(case when ifnull(price, '') = '' then (999999) else price end) AS price1," +
                        " year, quantity, color, (case when ifnull(drink_id, '') = '' then ('e' || item_id) else drink_id end) as drink_id, is_favourite, item_left_overs AS item_left_overs1 " +
                        "FROM item ) " +
                    " AS t0 " +
                    "GROUP BY t0.drink_id " +
                ") " +
                "WHERE item_left_overs > 0 " +
                "ORDER BY %s";
        open();
        String selectSql = String.format(formatScript, orderByField);
        return getDatabase().rawQuery(selectSql, null);
    }

    public Cursor getAllItemsByCategory(Integer categoryId, String orderByField) {
        String formatScript = "SELECT * " +
                "FROM " +
                    "(" +
                        "SELECT t0.*, MIN(price1) AS price, COUNT(t0.drink_id) AS count, MAX(item_left_overs1) as item_left_overs " +
                        "FROM " +
                        "(" +
                            "SELECT t1.item_id as _id, t1.name, t1.localized_name, t1.volume, t1.bottle_high_res, t1.bottle_low_resolution, t1.product_type, t1.drink_category, " +
                        "(case when ifnull(t1.price, '') = '' then (999999) else t1.price end) AS price1," +
                        "t1.year, t1.quantity, t1.color, (case when ifnull(t1.drink_id, '') = '' then ('e' || t1.item_id) else t1.drink_id end) as drink_id, t1.is_favourite, item_left_overs AS item_left_overs1 FROM item AS t1 WHERE t1.drink_category = %s " +
                        ") AS t0 " +
                        "GROUP BY t0.drink_id " +
                    ") " +
                "WHERE item_left_overs > 0 " +
                "ORDER BY %s";
        open();
        String selectSql = String.format(formatScript, categoryId, orderByField);
        return getDatabase().rawQuery(selectSql, null);
    }

    public void setFavourite(List<String> itemsId, boolean state) {
        ContentValues values;
        String whereClause;
        open();
        for (String itemId : itemsId) {
            values = new ContentValues();
            values.put(DatabaseSqlHelper.ITEM_IS_FAVOURITE, state ? 1 : 0);
            whereClause = String.format("item_id = '%s'", itemId);
            int count = getDatabase().update(DatabaseSqlHelper.ITEM_TABLE, values, whereClause, null);
        }
        close();
    }

    public List<Boolean> getCountItemsByCategoryByShop(String locationID){
        ArrayList<Boolean> enableList = null;
        String formatSrctip = "SELECT " +
            "(SELECT COUNT(item.item_id) " +
            "FROM item INNER JOIN item_availability ON item.item_id = item_availability.item_id " +
            "WHERE drink_category = 0 AND location_id = '%1$s') AS C0, " +
            "(SELECT COUNT(item.item_id) " +
            "FROM item INNER JOIN item_availability ON item.item_id = item_availability.item_id " +
            "WHERE drink_category = 1 AND location_id = '%1$s') AS C1, " +
            "(SELECT COUNT(item.item_id) " +
            "FROM item INNER JOIN item_availability ON item.item_id = item_availability.item_id " +
            "WHERE drink_category = 2 AND location_id = '%1$s') AS C2, " +
            "(SELECT COUNT(item.item_id) " +
            "FROM item INNER JOIN item_availability ON item.item_id = item_availability.item_id " +
            "WHERE drink_category = 3 AND location_id = '%1$s') AS C3, " +
            "(SELECT COUNT(item.item_id) " +
            "FROM item INNER JOIN item_availability ON item.item_id = item_availability.item_id " +
            "WHERE drink_category = 4 AND location_id = '%1$s') AS C4, " +
            "(SELECT COUNT(item.item_id) " +
            "FROM item INNER JOIN item_availability ON item.item_id = item_availability.item_id " +
            "WHERE drink_category = 5 AND location_id = '%1$s') AS C5";
        String selectSql = String.format(formatSrctip,  locationID);
        open();
        Cursor cursor = getDatabase().rawQuery(selectSql, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                enableList = new ArrayList<Boolean>(6);
                enableList.add(new Boolean(cursor.getInt(0) > 0));
                enableList.add(new Boolean(cursor.getInt(1) > 0));
                enableList.add(new Boolean(cursor.getInt(2) > 0));
                enableList.add(new Boolean(cursor.getInt(3) > 0));
                enableList.add(new Boolean(cursor.getInt(4) > 0));
                enableList.add(new Boolean(cursor.getInt(5) > 0));
            }
        }
        close();
        return enableList;
    }

    public Cursor getItems(){
        String selectSql = "SELECT " +
                "item_id, name, localized_name, manufacturer, localized_manufacturer, " +
                "country, region, style, drink_type, style_description, taste_qualities, " +
                "vintage_report, aging_process, production_process, interesting_facts, " +
                "label_history, gastronomy, vineyard, grapes_used " +
                "FROM item";
        open();
        return getDatabase().rawQuery(selectSql, null);
    }

    private Item createItem(Cursor cursor) {
        Item item = null;
        int indexItemID = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_ID);
        int indexDrinkID = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_ID);
        int indexName = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_NAME);
        int indexLocalizeName = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_LOCALIZED_NAME);
        int indexManufacturer = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_MANUFACTURER);
        int indexLocalizedManufacturer = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_LOCALIZED_MANUFACTURER);
        int indexPrice = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRICE);
        int indexPriceMarkup = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRICE_MARKUP);
        int indexCountry = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_COUNTRY);
        int indexRegion = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_REGION);
        int indexBarcode = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_BARCODE);
        int indexProductType = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRODUCT_TYPE);
        int indexClassification = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_CLASSIFICATION);
        int indexDrinkCategory = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_CATEGORY);
        int indexColor = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_COLOR);
        int indexStyle = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_STYLE);
        int indexSweetness = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_SWEETNESS);
        int indexYear = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_YEAR);
        int indexVolume = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_VOLUME);
        int indexDrinkType = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_TYPE);
        int indexAlcohol = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_ALCOHOL);
        int indexBottleHiResolutionImageFilename = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_BOTTLE_HI_RESOLUTION_IMAGE_FILENAME);
        int indexBottleLowResolutionImageFilename = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME);
        int indexStyleDescrition = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_STYLE_DESCRIPTION);
        int indexAppelation = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_APPELATION);
        int indexServingTempMin = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_SERVING_TEMP_MIN);
        int indexServingTempMax = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_SERVING_TEMP_MAX);
        int indexTasteQualities = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_TASTE_QUALITIES);
        int indexVintageReport = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_VINTAGE_REPORT);
        int indexAgingProcess = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_AGING_PROCESS);
        int indexProductionProcess = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRODUCTION_PROCESS);
        int indexInterestingFacts = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_INTERESTING_FACTS);
        int indexLabelHistory = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_LABEL_HISTORY);
        int indexGastronomy = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_GASTRONOMY);
        int indexVineyard = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_VINEYARD);
        int indexGrapesUsed = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_GRAPES_USED);
        int indexRating = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_RATING);
        int indexQuantity = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_QUANTITY);
        int indexFavourite = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_IS_FAVOURITE);
        if (cursor != null && cursor.moveToFirst()) {
            item = new Item();
            item.setItemID(cursor.getString(indexItemID));
            item.setDrinkID(cursor.getString(indexDrinkID));
            item.setName(cursor.getString(indexName));
            item.setLocalizedName(cursor.getString(indexLocalizeName));
            item.setManufacturer(cursor.getString(indexManufacturer));
            item.setLocalizedManufacturer(cursor.getString(indexLocalizedManufacturer));
            item.setPrice(cursor.getFloat(indexPrice));
            item.setPriceMarkup(cursor.getFloat(indexPriceMarkup));
            item.setCountry(cursor.getString(indexCountry));
            item.setRegion(cursor.getString(indexRegion));
            item.setBarcode(cursor.getString(indexBarcode));
            item.setProductType(ProductType.getProductType(cursor.getInt(indexProductType)));
            item.setClassification(cursor.getString(indexClassification));
            item.setDrinkCategory(DrinkCategory.getDrinkCategory(cursor.getInt(indexDrinkCategory)));
            item.setColor(ItemColor.getColor(cursor.getInt(indexColor)));
            item.setStyle(cursor.getString(indexStyle));
            item.setSweetness(Sweetness.getSweetness(cursor.getInt(indexSweetness)));
            item.setYear(cursor.getInt(indexYear));
            item.setVolume(cursor.getFloat(indexVolume));
            item.setDrinkType(cursor.getString(indexDrinkType));
            item.setAlcohol(cursor.getString(indexAlcohol));
            item.setBottleHiResolutionImageFilename(cursor.getString(indexBottleHiResolutionImageFilename));
            item.setBottleLowResolutionImageFilename(cursor.getString(indexBottleLowResolutionImageFilename));
            item.setStyleDescription(cursor.getString(indexStyleDescrition));
            item.setAppelation(cursor.getString(indexAppelation));
            item.setServingTempMin(cursor.getString(indexServingTempMin));
            item.setServingTempMax(cursor.getString(indexServingTempMax));
            item.setTasteQualities(cursor.getString(indexTasteQualities));
            item.setVintageReport(cursor.getString(indexVintageReport));
            item.setAgingProcess(cursor.getString(indexAgingProcess));
            item.setProductionProcess(cursor.getString(indexProductionProcess));
            item.setInterestingFacts(cursor.getString(indexInterestingFacts));
            item.setLabelHistory(cursor.getString(indexLabelHistory));
            item.setGastronomy(cursor.getString(indexGastronomy));
            item.setVineyard(cursor.getString(indexVineyard));
            item.setGrapesUsed(cursor.getString(indexGrapesUsed));
            item.setRating(cursor.getString(indexRating));
            item.setQuantity(cursor.getFloat(indexQuantity));
            item.setFavourite(cursor.getInt(indexFavourite) == 1);
            cursor.close();
        }
        return item;
    }

    //TODO refactor: переименовать метод и переписать через String.format
    private String getWhereBySearch(String query) {
//        return String.format("item.item_id IN (%s)", rangeItemsID);
        String formatQuery = "%" + query + "%";
        String onePart = String.format(LIKE, DatabaseSqlHelper.ITEM_LOCALIZED_NAME, formatQuery);
        String twoPart = String.format(LIKE, DatabaseSqlHelper.ITEM_NAME, formatQuery);
        return String.format(OR, onePart, twoPart);
    }

    private String getUpperCaseQuery(String query){
        String queryLowerCase = query.toLowerCase();
        StringBuilder queryRes = new StringBuilder();
        String[] words = queryLowerCase.split(" ");
        for(String word: words){
            queryRes.append("%");
            queryRes.append(getUpFirstChar(word));
        }
        queryRes.append("%");
        return  queryRes.toString();
    }

    private String getUpFirstChar(String word){
        char[] charQuery = word.toCharArray();
        charQuery[0] = Character.toUpperCase(charQuery[0]);
        return new String(charQuery);
    }

    private String getLowerCaseQuery(String query){
        String queryLowerCase = query.toLowerCase();
        StringBuilder queryRes = new StringBuilder();
        String[] words = queryLowerCase.split(" ");
        for(String word: words){
            queryRes.append("%");
            queryRes.append(word);
        }
        queryRes.append("%");
        return  queryRes.toString();
    }

    private String getWhereBySearchFirst(String query){
        String queryLowCase = getLowerCaseQuery(query);
        String queryUpFirstChar = getUpperCaseQuery(query);
        return String.format("item.name LIKE '%%%1$s%%' OR item.manufacturer LIKE '%%%1$s%%' " +
                "OR item.localized_name LIKE '%%%1$s%%' OR item.localized_name LIKE '%%%2$s%%' " +
                "OR item.localized_manufacturer LIKE '%%%1$s%%' OR item.localized_manufacturer LIKE '%%%2$s%%'",
                queryLowCase,
                queryUpFirstChar);
    }

    private String getWhereBySearchSecond(String query){
        String queryLowCase = getLowerCaseQuery(query);
        String queryUpFirstChar = getUpperCaseQuery(query);
        return String.format("item.country LIKE '%%%1$s%%' OR item.country LIKE '%%%2$s%%' " +
                "OR item.region LIKE '%%%1$s%%' OR item.region LIKE '%%%2$s%%'",
                queryLowCase,
                queryUpFirstChar);
    }

    private String getWhereBySearchThird(String query){
        String queryLowCase = getLowerCaseQuery(query);
        String queryUpFirstChar = getUpperCaseQuery(query);
        return String.format("style LIKE '%%%1$s%%' OR style LIKE '%%%2$s%%' " +
                "OR drink_type LIKE '%%%1$s%%' OR drink_type LIKE '%%%2$s%%' " +
                "OR style_description LIKE '%%%1$s%%' OR style_description LIKE '%%%2$s%%' " +
                "OR grapes_used LIKE '%%%1$s%%' OR grapes_used LIKE '%%%2$s%%'",
                queryLowCase,
                queryUpFirstChar);
    }

    private String getWhereBySearchFourth(String query){
        String queryLowCase = getLowerCaseQuery(query);
        String queryUpFirstChar = getUpperCaseQuery(query);
        return String.format("taste_qualities LIKE '%%%1$s%%' OR taste_qualities LIKE '%%%2$s%%' " +
                "OR vintage_report LIKE '%%%1$s%%' OR vintage_report LIKE '%%%2$s%%' " +
                "OR aging_process LIKE '%%%1$s%%' OR aging_process LIKE '%%%2$s%%' " +
                "OR interesting_facts LIKE '%%%1$s%%' OR interesting_facts LIKE '%%%2$s%%' " +
                "OR label_history LIKE '%%%1$s%%' OR label_history LIKE '%%%2$s%%' " +
                "OR gastronomy LIKE '%%%1$s%%' OR gastronomy LIKE '%%%2$s%%' " +
                "OR vineyard LIKE '%%%1$s%%' OR vineyard LIKE '%%%2$s%%'",
                queryLowCase,
                queryUpFirstChar);
    }

    public void deleteAllData() {
        open();
        String deleteSql = " DELETE FROM item";
        getDatabase().execSQL(deleteSql);
        close();
    }

}
