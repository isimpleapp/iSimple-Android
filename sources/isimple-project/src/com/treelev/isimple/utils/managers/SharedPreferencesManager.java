package com.treelev.isimple.utils.managers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.treelev.isimple.R;
import com.treelev.isimple.app.ISimpleApp;

public class SharedPreferencesManager {

    private final static String PREFS = "iSimple_prefs";
    // TODO In order to update db from assets on app market update change FIRST_START value
    private final static String FIRST_START = "first_start_3.0";
    private final static String FIRST_TIME_NEW_SYNC = "first_time_new_sync";

    private static final String DATE_UPDATE = "date_update";
    private static final String DATE_PRICE_UPDATE = "date_price_update";
    private static final String DATE_CATALOG_UPDATE = "date_catalog_update";
    private static final String UPDATE_START = "update_start";
    private static final String PREPARATION_UPDATE = "preparation_update";
    private static final String LAST_SYNC_TIMESTAMP = "last_sync_timestamp";
    private static final String LAST_MONTH_SYNC_TIMESTAMP = "last_month_sync_timestamp";
    private static final String LAST_DELIVERY_UPDATE_TIMESTAMP = "last_delivery_update_timestamp";
    private static final String LAST_OFFERS_SYNC_TIMESTAMP = "last_offers_sync_timestamp";
    private static final String LAST_KNOWN_OFFERS_URL = "LAST_KNOWN_OFFERS_URL";
    private static final String CONTACT_DATA_NAME = "CONTACT_DATA_NAME";
    private static final String CONTACT_DATA_EMAIL = "CONTACT_DATA_EMAIL";
    private static final String CONTACT_DATA_PHONE = "CONTACT_DATA_PHONE";

    private static final String FORMAT_DATE = "dd.MM.yyyy";
    
    private static final String LOAD_FILE_DATA_URL_KEY = "LOAD_FILE_DATA_URL";

    public static  SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(PREFS, Context.MODE_MULTI_PROCESS);
    }

    public static boolean isFirstStart(Context context){
        return getSharedPreferences(context).getBoolean(FIRST_START, true);
    }

    public static void setFirstStart(Context context, boolean state){
        Editor editor = getEditor();
        editor.putBoolean(FIRST_START, state);
        editor.commit();
    }
    
    public static boolean isFirstTimeNewSync(Context context){
        return getSharedPreferences(context).getBoolean(FIRST_TIME_NEW_SYNC, true);
    }

    public static void setFirstTimeNewSync(Context context, boolean state){
        Editor editor = getEditor();
        editor.putBoolean(FIRST_TIME_NEW_SYNC, state);
        editor.commit();
    }

    public static String getDateUpdate(Context context){
        return getSharedPreferences(context).getString(DATE_UPDATE, context.getString(R.string.date_update));
    }

    public static void refreshDateUpdate(Context context){
        Editor editor = getEditor();
        editor.putString(DATE_UPDATE, getFormattedDate());
        editor.commit();
    }

    public static void refreshDatePriceUpdate(Context context){
        Editor editor = getEditor();
        editor.putString(DATE_PRICE_UPDATE, getFormattedDate());
        editor.commit();
    }

    public static String getDatePriceUpdate(Context context){
        return getSharedPreferences(context).getString(DATE_PRICE_UPDATE, context.getString(R.string.price_date_update));
    }

    public static void refreshDateCatalogUpdate(Context context){
        Editor editor = getEditor();
        editor.putString(DATE_CATALOG_UPDATE, getFormattedDate());
        editor.commit();
    }

    public static String getDateCatalogUpdate(Context context){
        return getSharedPreferences(context).getString(DATE_CATALOG_UPDATE,
                context.getString(R.string.catalog_date_update));
    }

    public static boolean isStartUpdate(Context context){
        return getSharedPreferences(context).getBoolean(UPDATE_START, false);
    }

    public static void setStartUpdate(Context context, boolean state){
        Editor editor = getEditor();
        editor.putBoolean(UPDATE_START, state);
        editor.commit();
    }

    public static boolean isPreparationUpdate(Context context){
        return getSharedPreferences(context).getBoolean(PREPARATION_UPDATE, false);
    }

    public static void setPreparationUpdate(Context context, boolean state){
        Editor editor = getEditor();
        editor.putBoolean(PREPARATION_UPDATE, state);
        editor.commit();
    }
    
    public static String getUpdateFileName(String updateFile) {
    	Context context = (Context) ISimpleApp.getInstantce();
        return getSharedPreferences(context).getString(updateFile, "");
    }

    public static void putUpdateFileName(String updateFile, String updateFileName){
        Editor editor = getEditor();
        editor.putString(updateFile, updateFileName);
        editor.commit();
    }
    
    public static String getUpdateFileUrl() {
    	Context context = (Context) ISimpleApp.getInstantce();
//        return getSharedPreferences(context).getString(LOAD_FILE_DATA_URL_KEY, "http://10.0.1.10/iSimple/Update-Index.xml");
        return getSharedPreferences(context).getString("LOAD_FILE_DATA_URL", "http://s1.isimpleapp.ru/xml/ver0/Update-Index.xml");
//        return getSharedPreferences(context).getString("LOAD_FILE_DATA_URL", "http://sun.treelev.com/iSimple/Update-Index.xml");
    }
    
    public static void putUpdateFileUrl(String updateFileUrl){
        Editor editor = getEditor();
        editor.putString(LOAD_FILE_DATA_URL_KEY, updateFileUrl);
        editor.commit();
    }
    
    public static void deletePreference(String key){
        Editor editor = getEditor();
        editor.remove(key);
        editor.commit();
    }

    private static Editor getEditor(){
    	Context context = ISimpleApp.getInstantce();
        return context.getSharedPreferences(PREFS, Context.MODE_MULTI_PROCESS).edit();
    }

    private static String getFormattedDate(){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat(FORMAT_DATE);
        return formatDate.format(date);
    }
    
    public static long getLastSyncTimestamp(Context context){
        return getSharedPreferences(context).getLong(LAST_SYNC_TIMESTAMP, 0);
    }

    public static void setLastSyncTimestamp(Context context, long timestamp){
        Editor editor = getEditor();
        editor.putLong(LAST_SYNC_TIMESTAMP, timestamp);
        editor.commit();
    }
    
    public static long getLastMonthSyncTimestamp(Context context){
        return getSharedPreferences(context).getLong(LAST_MONTH_SYNC_TIMESTAMP, 0);
    }

    public static void setLastMonthSyncTimestamp(Context context, long timestamp){
        Editor editor = getEditor();
        editor.putLong(LAST_MONTH_SYNC_TIMESTAMP, timestamp);
        editor.commit();
    }
    
    public static long getLastDeliveryUpdateTimestamp(){
        return getSharedPreferences(ISimpleApp.getInstantce()).getLong(LAST_DELIVERY_UPDATE_TIMESTAMP, 0);
    }

    public static void setLastDeliveryUpdateTimestamp(long timestamp){
        Editor editor = getEditor();
        editor.putLong(LAST_DELIVERY_UPDATE_TIMESTAMP, timestamp);
        editor.commit();
    }
    
    public static long getLastOffersSyncTimestamp(){
        return getSharedPreferences(ISimpleApp.getInstantce()).getLong(LAST_OFFERS_SYNC_TIMESTAMP, 0);
    }

    public static void setLastOffersSyncTimestamp(long timestamp){
        Editor editor = getEditor();
        editor.putLong(LAST_OFFERS_SYNC_TIMESTAMP, timestamp);
        editor.commit();
    }
    
    public static String getLastKnownOffersUrl(){
        return getSharedPreferences(ISimpleApp.getInstantce()).getString(LAST_KNOWN_OFFERS_URL, "");
    }

    public static void setLastKnownOffersUrl(String url){
        Editor editor = getEditor();
        editor.putString(LAST_KNOWN_OFFERS_URL, url);
        editor.commit();
    }
    
    public static void setContactDataName(Context context, String name){
        Editor editor = getEditor();
        editor.putString(CONTACT_DATA_NAME, name);
        editor.commit();
    }
    
    public static String getContactDataName(Context context){
        return getSharedPreferences(context).getString(CONTACT_DATA_NAME, null);
    }
    
    public static void setContactDataEmail(Context context, String email){
        Editor editor = getEditor();
        editor.putString(CONTACT_DATA_EMAIL, email);
        editor.commit();
    }
    
    public static String getContactDataEmail(Context context){
        return getSharedPreferences(context).getString(CONTACT_DATA_EMAIL, null);
    }
    
    public static void setContactDataPhone(Context context, String phone){
        Editor editor = getEditor();
        editor.putString(CONTACT_DATA_PHONE, phone);
        editor.commit();
    }
    
    public static String getContactDataPhone(Context context){
        return getSharedPreferences(context).getString(CONTACT_DATA_PHONE, null);
    }

}
