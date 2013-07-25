package com.treelev.isimple.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import com.treelev.isimple.activities.SplashActivity;
import com.treelev.isimple.tasks.DownloadDataTask;
import com.treelev.isimple.tasks.UnzipTask;
import com.treelev.isimple.utils.managers.WebServiceManager;

import java.io.File;
import java.util.Calendar;


public class DownloadDataService extends Service {

    public final static String LAST_UPDATED_DATE = "date_for_last_update";
    public final static String FIRST_START = "first_start";
    public final static String PREFS = "iSimple_prefs";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isExternalStorageAvailable()){
            SharedPreferences.Editor editor = getApplication().getSharedPreferences(DownloadDataService.PREFS, MODE_MULTI_PROCESS).edit();
            editor.putBoolean(SplashActivity.UPDATE_DATA_READY, false);
            editor.commit();

            Log.v("Test log start update", "start update");
            DownloadDataTask downloadDataTask = DownloadDataTask.getDownloadDataTask(getApplicationContext());
            UnzipTask unzipTask = UnzipTask.getUnzipTask(getApplicationContext());
            if (!downloadDataTask.isRunning() && !unzipTask.isRunning()) {
                downloadDataTask.execute();
            }
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }




    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        boolean externalStorageAvailable = false;
        boolean externalStorageWriteable = false;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            externalStorageAvailable = externalStorageWriteable = false;
        }
        if (externalStorageAvailable == true
                && externalStorageWriteable == true) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
