package com.treelev.isimple.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import com.treelev.isimple.activities.SplashActivity;
import com.treelev.isimple.tasks.DownloadDataTask;
import com.treelev.isimple.tasks.UnzipTask;


public class DownloadDataService extends Service {

    public final static String LAST_UPDATED_DATE = "date_for_last_update";
    public final static String FIRST_START = "first_start";
    public final static String PREFS = "iSimple_prefs";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences.Editor editor = getApplication().getSharedPreferences(DownloadDataService.PREFS, MODE_MULTI_PROCESS).edit();
        editor.putBoolean(SplashActivity.UPDATE_DATA_READY, false);
        editor.commit();
        DownloadDataTask downloadDataTask = DownloadDataTask.getDownloadDataTask(getApplicationContext());
        UnzipTask unzipTask = UnzipTask.getUnzipTask(getApplicationContext());
        if (!downloadDataTask.isRunning() && !unzipTask.isRunning()) {
            downloadDataTask.execute();
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
