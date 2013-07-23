package com.treelev.isimple.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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
        if(needUpdate()){
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

    private boolean needUpdate(){
        boolean result = true;
        File directory = WebServiceManager.getDownloadDirectory();
        if(directory.exists()){
            long day = directory.lastModified() / SplashActivity.SECOND_TO_DAY;
            long currentDay = Calendar.getInstance().getTimeInMillis() / SplashActivity.SECOND_TO_DAY;
            result = day != currentDay;
            if(result){
                WebServiceManager.deleteDownloadDirectory();
            }
        }
        return result;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
