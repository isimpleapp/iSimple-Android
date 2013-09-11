package com.treelev.isimple.utils.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.*;
import android.content.res.Resources;
import com.treelev.isimple.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SharedPreferencesManager {

    private final static String PREFS = "iSimple_prefs";
    private final static String READY_UPDATE = "ready_update";
    private final static String FIRST_START = "first_start";

    private static final String DATE_UPDATE = "date_update";
    private static final String DATE_PRICE_UPDATE = "date_price_update";
    private static final String DATE_CATALOG_UPDATE = "date_catalog_update";
    private static final String UPDATE_START = "update_start";
    private static final String PREPARATION_UPDATE = "preparation_update";

    private static final String FORMAT_DATE = "dd.MM.yyyy";

    public static  SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(PREFS, context.MODE_MULTI_PROCESS);
    }

    public static boolean isUpdateReady(Context context){
        return getSharedPreferences(context).getBoolean(READY_UPDATE, false);
    }

    public  static void setUpdateReady(Context context, boolean state){
        Editor editor = getEditor(context);
        editor.putBoolean(READY_UPDATE, state);
        editor.commit();
    }

    public static boolean isFirstStart(Context context){
        return getSharedPreferences(context).getBoolean(FIRST_START, true);
    }

    public static void setFirstStart(Context context, boolean state){
        Editor editor = getEditor(context);
        editor.putBoolean(FIRST_START, state);
        editor.commit();
    }

    public static String getDateUpdate(Context context){
        return getSharedPreferences(context).getString(DATE_UPDATE, context.getString(R.string.date_update));
    }

    public static void refreshDateUpdate(Context context){
        Editor editor = getEditor(context);
        editor.putString(DATE_UPDATE, getFormattedDate());
        editor.commit();
    }

    public static void refreshDatePriceUpdate(Context context){
        Editor editor = getEditor(context);
        editor.putString(DATE_PRICE_UPDATE, getFormattedDate());
        editor.commit();
    }

    public static String getDatePriceUpdate(Context context){
        return getSharedPreferences(context).getString(DATE_PRICE_UPDATE, context.getString(R.string.date_update));
    }

    public static void refreshDateCatalogUpdate(Context context){
        Editor editor = getEditor(context);
        editor.putString(DATE_CATALOG_UPDATE, getFormattedDate());
        editor.commit();
    }

    public static String getDateCatalogUpdate(Context context){
        return getSharedPreferences(context).getString(DATE_CATALOG_UPDATE,
                context.getString(R.string.date_update));
    }

    public static boolean isStartUpdate(Context context){
        return getSharedPreferences(context).getBoolean(UPDATE_START, false);
    }

    public static void setStartUpdate(Context context, boolean state){
        Editor editor = getEditor(context);
        editor.putBoolean(UPDATE_START, state);
        editor.commit();
    }

    public static boolean isPreparationUpdate(Context context){
        return getSharedPreferences(context).getBoolean(PREPARATION_UPDATE, false);
    }

    public static void setPreparationUpdate(Context context, boolean state){
        Editor editor = getEditor(context);
        editor.putBoolean(PREPARATION_UPDATE, state);
        editor.commit();
    }

    private static Editor getEditor(Context context){
        return context.getSharedPreferences(PREFS, context.MODE_MULTI_PROCESS).edit();
    }

    private static String getFormattedDate(){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat(FORMAT_DATE);
        return formatDate.format(date);
    }

}
