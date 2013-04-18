package com.treelev.isimple.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseSqlHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "iSimple.db";
    public final static int DATABASE_VERSION = 1;

    public final static String ITEM_TABLE = "item";
    public final static String ITEM_ID = "item_id";
    public final static String ITEM_DRINK_ID = "drink_id";
    public final static String ITEM_NAME = "name";
    public final static String ITEM_LOCALIZED_NAME = "localized_name";
    public final static String ITEM_MANUFACTURER = "manufacturer";
    public final static String ITEM_LOCALIZED_MANUFACTURER = "localized_manufacturer";
    public final static String ITEM_PRICE = "price";
    public final static String ITEM_PRICE_MARKUP = "price_markup";
    public final static String ITEM_COUNTRY = "country";
    public final static String ITEM_REGION = "region";
    public final static String ITEM_BARCODE = "barcode";
    public final static String ITEM_DRINK_CATEGORY = "drink_category";
    public final static String ITEM_COLOR = "color";
    public final static String ITEM_STYLE = "style";
    public final static String ITEM_SWEETNESS = "sweetness";
    public final static String ITEM_YEAR = "year";
    public final static String ITEM_VOLUME = "volume";
    public final static String ITEM_DRINK_TYPE = "drink_type";
    public final static String ITEM_ALCOHOL = "alcohol";
    public final static String ITEM_BOTTLE_HI_RESOLUTION_IMAGE_FILENAME = "bottle_high_res";
    public final static String ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME = "bottle_low_resolution";
    public final static String ITEM_STYLE_DESCRIPTION = "style_description";
    public final static String ITEM_APPELATION = "appelation";
    public final static String ITEM_SERVING_TEMP_MIN = "serving_temp_min";
    public final static String ITEM_SERVING_TEMP_MAX = "serving_temp_max";
    public final static String ITEM_TASTE_QUALITIES = "taste_qualities";
    public final static String ITEM_VINTAGE_REPORT = "vintage_report";
    public final static String ITEM_AGING_PROCESS = "aging_process";
    public final static String ITEM_PRODUCTION_PROCESS = "production_process";
    public final static String ITEM_INTERESTING_FACTS = "interesting_facts";
    public final static String ITEM_LABEL_HISTORY = "label_history";
    public final static String ITEM_GASTRONOMY = "gastronomy";
    public final static String ITEM_VINEYARD = "vineyard";
    public final static String ITEM_GRAPES_USED = "grapes_used";
    public final static String ITEM_RATING = "rating";

    private final static String CREATE_TABLE_ITEM = "create table " + ITEM_TABLE + "( " +
            ITEM_ID + " text primary key, " +
            ITEM_DRINK_ID + " text, " +
            ITEM_NAME + " text, " +
            ITEM_LOCALIZED_NAME + " text, " +
            ITEM_MANUFACTURER + " text, " +
            ITEM_LOCALIZED_MANUFACTURER + " text, " +
            ITEM_PRICE + " text, " +
            ITEM_PRICE_MARKUP + " text, " +
            ITEM_COUNTRY + " text, " +
            ITEM_REGION + " text, " +
            ITEM_BARCODE + " text, " +
            ITEM_DRINK_CATEGORY + " double, " +
            ITEM_COLOR + " double, " +
            ITEM_STYLE + " double, " +
            ITEM_SWEETNESS + " double, " +
            ITEM_YEAR + " text, " +
            ITEM_VOLUME + " text, " +
            ITEM_DRINK_TYPE + " text, " +
            ITEM_ALCOHOL + " text, " +
            ITEM_BOTTLE_HI_RESOLUTION_IMAGE_FILENAME + " text, " +
            ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME + " text, " +
            ITEM_STYLE_DESCRIPTION + " text, " +
            ITEM_APPELATION + " text, " +
            ITEM_SERVING_TEMP_MIN + " text, " +
            ITEM_SERVING_TEMP_MAX + " text, " +
            ITEM_TASTE_QUALITIES + " text, " +
            ITEM_VINTAGE_REPORT + " text, " +
            ITEM_AGING_PROCESS + " text, " +
            ITEM_PRODUCTION_PROCESS + " text, " +
            ITEM_INTERESTING_FACTS + " text, " +
            ITEM_LABEL_HISTORY + " text, " +
            ITEM_GASTRONOMY + " text, " +
            ITEM_VINEYARD + " text, " +
            ITEM_GRAPES_USED + " text, " +
            ITEM_RATING + " text);";

    public final static String ITEM_AVAILABILITY_TABLE = "item_availability";
    public final static String ITEM_AVAILABILITY_ID = BaseColumns._ID;
    public final static String ITEM_AVAILABILITY_ITEM_ID = "item_id";
    public final static String ITEM_AVAILABILITY_LOCATION_ID = "location_id";

    private final static String CREATE_TABLE_ITEM_AVAILABILITY = "create table " + ITEM_AVAILABILITY_TABLE + " ( " +
            ITEM_AVAILABILITY_ID + " integer primary key autoincrement, " +
            ITEM_AVAILABILITY_ITEM_ID + " text, " +
            ITEM_AVAILABILITY_LOCATION_ID + " text);";

    public final static String SHOP_TABLE = "shop";
    public final static String SHOP_LOCATION_ID = "location_id";
    public final static String SHOP_LOCATION_NAME = "location_name";
    public final static String SHOP_LOCATION_ADDRESS = "location_address";
    public final static String SHOP_LONGITUDE = "location_longitude";
    public final static String SHOP_LANTITUDE = "location_lantitude";
    public final static String SHOP_WORKING_HOURS = "working_hours";
    public final static String SHOP_PHONE_NUMBER = "phone_number";
    public final static String SHOP_CHAIN_ID = "chain_id";
    public final static String SHOP_LOCATION_TYPE = "location_type";
    public final static String SHOP_PRESENCE_PERCENTAGE = "presence_percentage";

    private final static String CREATE_TABLE_SHOP = "create table " + SHOP_TABLE + " ( " +
            SHOP_LOCATION_ID + " text primary key, " +
            SHOP_LOCATION_NAME + " text, " +
            SHOP_LOCATION_ADDRESS + " text, " +
            SHOP_LONGITUDE + " double, " +
            SHOP_LANTITUDE + " double, " +
            SHOP_WORKING_HOURS + " integer, " +
            SHOP_PHONE_NUMBER + " text, " +
            SHOP_CHAIN_ID + " text, " +
            SHOP_LOCATION_TYPE + " integer, " +
            SHOP_PRESENCE_PERCENTAGE + " integer);";

    public final static String CHAIN_TABLE = "chain";
    public final static String CHAIN_ID = "chain_id";
    public final static String CHAIN_NAME = "chain_name";
    public final static String CHAIN_TYPE = "chain_type";

    private final static String CREATE_TABLE_CHAIN = "create table " + CHAIN_TABLE + " ( " +
            CHAIN_ID + " text primary key, " +
            CHAIN_NAME + " text, " +
            CHAIN_TYPE + " integer);";

    public DatabaseSqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ITEM);
        db.execSQL(CREATE_TABLE_CHAIN);
        db.execSQL(CREATE_TABLE_SHOP);
        db.execSQL(CREATE_TABLE_ITEM_AVAILABILITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_AVAILABILITY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CHAIN_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SHOP_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
        onCreate(db);
    }
}
