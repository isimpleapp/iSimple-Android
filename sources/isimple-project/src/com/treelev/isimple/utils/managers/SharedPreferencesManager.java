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
    private final static String READY_UPDATE = "ready_update";
    private final static String FIRST_START = "first_start";
    private final static String FIRST_TIME_NEW_SYNC = "first_time_new_sync";

    private static final String DATE_UPDATE = "date_update";
    private static final String DATE_PRICE_UPDATE = "date_price_update";
    private static final String DATE_CATALOG_UPDATE = "date_catalog_update";
    private static final String UPDATE_START = "update_start";
    private static final String PREPARATION_UPDATE = "preparation_update";

    private static final String FORMAT_DATE = "dd.MM.yyyy";
    
    private static final String LOAD_FILE_DATA_URL_KEY = "LOAD_FILE_DATA_URL";

    public static  SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(PREFS, context.MODE_MULTI_PROCESS);
    }

    public static boolean isUpdateReady(Context context){
        return getSharedPreferences(context).getBoolean(READY_UPDATE, false);
    }

    public  static void setUpdateReady(Context context, boolean state){
        Editor editor = getEditor();
        editor.putBoolean(READY_UPDATE, state);
        editor.commit();
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
    	Context context = (Context) ISimpleApp.getInstantce();
        return context.getSharedPreferences(PREFS, context.MODE_MULTI_PROCESS).edit();
    }

    private static String getFormattedDate(){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat(FORMAT_DATE);
        return formatDate.format(date);
    }

}
