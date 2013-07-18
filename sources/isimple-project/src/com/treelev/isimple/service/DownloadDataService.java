package com.treelev.isimple.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.treelev.isimple.tasks.DownloadDataTask;
import com.treelev.isimple.tasks.UnzipTask;


public class DownloadDataService extends Service {

    public final static String LAST_UPDATED_DATE = "date_for_last_update";
    public final static String FIRST_START = "first_start";
    public final static String PREFS = "iSimple_prefs";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
