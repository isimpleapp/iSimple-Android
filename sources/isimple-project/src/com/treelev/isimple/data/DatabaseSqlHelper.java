package com.treelev.isimple.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DatabaseSqlHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "iSimple.db";
    public final static int DATABASE_VERSION = 3;

    public final static String ITEM_DEPRECATED_TABLE = "item_deprecated";
    public final static String ITEM_TABLE = "item";
    public final static String FAVOURITE_ITEM_TABLE = "favourite_item";
    public final static String SHOPPING_CART_ITEM_TABLE = "shopping_cart_item";

    public final static String ITEM_ID = "item_id";
    public final static String ITEM_DRINK_ID = "drink_id";
    public final static String ITEM_NAME = "name";
    public final static String ITEM_LOCALIZED_NAME = "localized_name";
    public final static String ITEM_MANUFACTURER = "manufacturer";
    public final static String ITEM_LOCALIZED_MANUFACTURER = "localized_manufacturer";
    public final static String ITEM_PRICE = "price";
    public final static String ITEM_OLD_PRICE = "old_price";
    public final static String ITEM_PRICE_MARKUP = "price_markup";
    public final static String ITEM_COUNTRY = "country";
    public final static String ITEM_REGION = "region";
    public final static String ITEM_BARCODE = "barcode";
    public final static String ITEM_PRODUCT_TYPE = "product_type";
    public final static String ITEM_CLASSIFICATION = "classification";
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
    public final static String ITEM_QUANTITY = "quantity";
    public final static String ITEM_IS_FAVOURITE = "is_favourite";
    public final static String ITEM_SHOPPING_CART_COUNT = "item_count";
    public final static String ITEM_LEFT_OVERS = "item_left_overs";

    private final static String CREATE_TABLE_ITEM_DEPRECATED = "create table " + ITEM_DEPRECATED_TABLE + "( " +
            ITEM_ID + " text primary key, " +
            ITEM_DRINK_ID + " text, " +
            ITEM_NAME + " text, " +
            ITEM_LOCALIZED_NAME + " text, " +
            ITEM_MANUFACTURER + " text, " +
            ITEM_LOCALIZED_MANUFACTURER + " text, " +
            ITEM_COUNTRY + " text, " +
            ITEM_REGION + " text, " +
            ITEM_BARCODE + " text, " +
            ITEM_PRODUCT_TYPE + " integer, " +
            ITEM_CLASSIFICATION + " text, " +
            ITEM_DRINK_CATEGORY + " integer, " +
            ITEM_DRINK_TYPE + " text, " +
            ITEM_VOLUME + " float);";

    private final static String CREATE_TABLE_ITEM = "create table " + ITEM_TABLE + "( " +
            ITEM_ID + " text primary key, " +
            ITEM_DRINK_ID + " text, " +
            ITEM_NAME + " text, " +
            ITEM_LOCALIZED_NAME + " text, " +
            ITEM_MANUFACTURER + " text, " +
            ITEM_LOCALIZED_MANUFACTURER + " text, " +
            ITEM_PRICE + " float, " +
            ITEM_OLD_PRICE + " float, " +
            ITEM_PRICE_MARKUP + " float, " +
            ITEM_COUNTRY + " text, " +
            ITEM_REGION + " text, " +
            ITEM_BARCODE + " text, " +
            ITEM_PRODUCT_TYPE + " integer, " +
            ITEM_CLASSIFICATION + " text, " +
            ITEM_DRINK_CATEGORY + " integer, " +
            ITEM_COLOR + " integer, " +
            ITEM_STYLE + " text, " +
            ITEM_SWEETNESS + " integer, " +
            ITEM_YEAR + " integer, " +
            ITEM_VOLUME + " float, " +
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
            ITEM_RATING + " text, " +
            ITEM_QUANTITY + " float, " +
            ITEM_IS_FAVOURITE + " integer, " +
            ITEM_LEFT_OVERS + " integer);";

    private final static String CREATE_TABLE_FAVOURITE_ITEM = "create table " + FAVOURITE_ITEM_TABLE + "( " +
            ITEM_ID + " text primary key, " +
            ITEM_DRINK_ID + " text, " +
            ITEM_NAME + " text, " +
            ITEM_LOCALIZED_NAME + " text, " +
            ITEM_MANUFACTURER + " text, " +
            ITEM_LOCALIZED_MANUFACTURER + " text, " +
            ITEM_PRICE + " float, " +
            ITEM_OLD_PRICE + " float, " +
            ITEM_PRICE_MARKUP + " float, " +
            ITEM_COUNTRY + " text, " +
            ITEM_REGION + " text, " +
            ITEM_BARCODE + " text, " +
            ITEM_PRODUCT_TYPE + " integer, " +
            ITEM_CLASSIFICATION + " text, " +
            ITEM_DRINK_CATEGORY + " integer, " +
            ITEM_COLOR + " integer, " +
            ITEM_STYLE + " text, " +
            ITEM_SWEETNESS + " integer, " +
            ITEM_YEAR + " integer, " +
            ITEM_VOLUME + " float, " +
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
            ITEM_RATING + " text, " +
            ITEM_QUANTITY + " float);";

    private final static String CREATE_TABLE_SHOPPING_CART_ITEM = "create table " + SHOPPING_CART_ITEM_TABLE + "( " +
            ITEM_ID + " text primary key, " +
            ITEM_DRINK_ID + " text, " +
            ITEM_NAME + " text, " +
            ITEM_LOCALIZED_NAME + " text, " +
            ITEM_MANUFACTURER + " text, " +
            ITEM_LOCALIZED_MANUFACTURER + " text, " +
            ITEM_PRICE + " float, " +
            ITEM_OLD_PRICE + " float, " +
            ITEM_PRICE_MARKUP + " float, " +
            ITEM_COUNTRY + " text, " +
            ITEM_REGION + " text, " +
            ITEM_BARCODE + " text, " +
            ITEM_PRODUCT_TYPE + " integer, " +
            ITEM_CLASSIFICATION + " text, " +
            ITEM_DRINK_CATEGORY + " integer, " +
            ITEM_COLOR + " integer, " +
            ITEM_STYLE + " text, " +
            ITEM_SWEETNESS + " integer, " +
            ITEM_YEAR + " integer, " +
            ITEM_VOLUME + " float, " +
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
            ITEM_RATING + " text, " +
            ITEM_QUANTITY + " float, " +
            ITEM_SHOPPING_CART_COUNT + " integer);";

    public final static String ITEM_AVAILABILITY_TABLE = "item_availability";
    public final static String ITEM_AVAILABILITY_ID = BaseColumns._ID;
    public final static String ITEM_AVAILABILITY_ITEM_ID = "item_id";
    public final static String ITEM_AVAILABILITY_LOCATION_ID = "location_id";
    public final static String ITEM_AVAILABILITY_CUSTOMER_ID = "customer_id";
    public final static String ITEM_AVAILABILITY_SHIPTO_CODE_ID = "shipto_code_id";
    public final static String ITEM_AVAILABILITY_PRICE = "price";

    private final static String CREATE_TABLE_ITEM_AVAILABILITY = "create table " + ITEM_AVAILABILITY_TABLE + " ( " +
            ITEM_AVAILABILITY_ID + " integer primary key autoincrement, " +
            ITEM_AVAILABILITY_ITEM_ID + " text, " +
            ITEM_AVAILABILITY_LOCATION_ID + " text, " +
            ITEM_AVAILABILITY_CUSTOMER_ID + " text, " +
            ITEM_AVAILABILITY_SHIPTO_CODE_ID + " text, " +
            ITEM_AVAILABILITY_PRICE + " float);";

    public final static String SHOP_TABLE = "shop";
    public final static String SHOP_LOCATION_ID = "location_id";
    public final static String SHOP_LOCATION_NAME = "location_name";
    public final static String SHOP_LOCATION_ADDRESS = "location_address";
    public final static String SHOP_LATITUDE = "location_latitude";
    public final static String SHOP_LONGITUDE = "location_longitude";
    public final static String SHOP_WORKING_HOURS = "working_hours";
    public final static String SHOP_PHONE_NUMBER = "phone_number";
    public final static String SHOP_CHAIN_ID = "chain_id";
    public final static String SHOP_LOCATION_TYPE = "location_type";
    public final static String SHOP_PRESENCE_PERCENTAGE = "presence_percentage";

    private final static String CREATE_TABLE_SHOP = "create table " + SHOP_TABLE + " ( " +
            SHOP_LOCATION_ID + " text primary key, " +
            SHOP_LOCATION_NAME + " text, " +
            SHOP_LOCATION_ADDRESS + " text, " +
            SHOP_LATITUDE + " float, " +
            SHOP_LONGITUDE + " float, " +
            SHOP_WORKING_HOURS + " text, " +
            SHOP_PHONE_NUMBER + " text, " +
            SHOP_CHAIN_ID + " text, " +
            SHOP_LOCATION_TYPE + " integer, " +
            SHOP_PRESENCE_PERCENTAGE + " float);";

    public final static String CHAIN_TABLE = "chain";
    public final static String CHAIN_ID = "chain_id";
    public final static String CHAIN_NAME = "chain_name";
    public final static String CHAIN_TYPE = "chain_type";

    private final static String CREATE_TABLE_CHAIN = "create table " + CHAIN_TABLE + " ( " +
            CHAIN_ID + " text primary key, " +
            CHAIN_NAME + " text, " +
            CHAIN_TYPE + " integer);";

    public final static String FEATURED_ITEM_TABLE = "featured_item";
    public final static String FEATURED_ITEM_ID = "item_id";
    public final static String FEATURED_ITEM_CATEGORY_ID = "category_id";

    private final static String CREATE_TABLE_FEATURED_ITEM = "create table " + FEATURED_ITEM_TABLE + " ( " +
            FEATURED_ITEM_ID + " text, " +
            FEATURED_ITEM_CATEGORY_ID + " integer);";

    public final static String DELIVERY_ITEM_TABLE = "delivery";
    public final static String DELIVERY_NAME = "name";
    public final static String DELIVERY_MIN_CONDITION = "min_condition";
    public final static String DELIVERY_MAX_CONDITION = "max_condition";
    public final static String DELIVERY_DESC = "pickup_desc";
    public final static String DELIVERY_ADDRESS = "address";
    public final static String DELIVERY_LONGITUDE = "longitude";
    public final static String DELIVERY_LATITUDE = "latitude";

    private final static String CREATE_TABLE_DELIVERY_ITEM = "create table " + DELIVERY_ITEM_TABLE + " ( " +
            BaseColumns._ID + " integer primary key autoincrement, " +
            DELIVERY_NAME + " text, " +
            DELIVERY_MIN_CONDITION + " integer, " +
            DELIVERY_MAX_CONDITION + " integer, " +
            DELIVERY_DESC + " text, " +
            DELIVERY_ADDRESS + " text, " +
            DELIVERY_LONGITUDE + " float, " +
            DELIVERY_LATITUDE + " float);";

    public DatabaseSqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ITEM);
        db.execSQL(CREATE_TABLE_ITEM_DEPRECATED);
        db.execSQL(CREATE_TABLE_CHAIN);
        db.execSQL(CREATE_TABLE_SHOP);
        db.execSQL(CREATE_TABLE_ITEM_AVAILABILITY);
        db.execSQL(CREATE_TABLE_FEATURED_ITEM);
        db.execSQL(CREATE_TABLE_FAVOURITE_ITEM);
        db.execSQL(CREATE_TABLE_SHOPPING_CART_ITEM);
        db.execSQL(CREATE_TABLE_DELIVERY_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	Log.i("", "SQLiteDatabase onUpgrade, oldVersion = " + oldVersion + " newVersion = " + newVersion);
//        String dropTableText = "DROP TABLE IF EXISTS %s";
//        db.execSQL(String.format(dropTableText, FEATURED_ITEM_TABLE));
//        db.execSQL(String.format(dropTableText, ITEM_AVAILABILITY_TABLE));
//        db.execSQL(String.format(dropTableText, CHAIN_TABLE));
//        db.execSQL(String.format(dropTableText, SHOP_TABLE));
//        db.execSQL(String.format(dropTableText, ITEM_TABLE));
//        db.execSQL(String.format(dropTableText, ITEM_DEPRECATED_TABLE));
//        db.execSQL(String.format(dropTableText, FAVOURITE_ITEM_TABLE));
//        db.execSQL(String.format(dropTableText, SHOPPING_CART_ITEM_TABLE));
//        db.execSQL(String.format(dropTableText, DELIVERY_ITEM_TABLE));
//        onCreate(db);
    	
    	if (oldVersion < 3 && newVersion == 3) {
    		try {
    			db.execSQL("ALTER TABLE " + ITEM_TABLE + " ADD COLUMN " + ITEM_OLD_PRICE + " FLOAT");
    		} catch (Exception e) {
    			Log.i("ADD COLUMN old_price", "old_price already exists");
			}
			try {
			 db.execSQL("ALTER TABLE " + FAVOURITE_ITEM_TABLE + " ADD COLUMN " + ITEM_OLD_PRICE + " FLOAT");
			} catch (Exception e) {
				Log.i("ADD COLUMN old_price", "old_price already exists");
			}
			try {
			db.execSQL("ALTER TABLE " + SHOPPING_CART_ITEM_TABLE + " ADD COLUMN " + ITEM_OLD_PRICE + " FLOAT");
			} catch (Exception e) {
				Log.i("ADD COLUMN old_price", "old_price already exists");
			}
		}
    }
}
