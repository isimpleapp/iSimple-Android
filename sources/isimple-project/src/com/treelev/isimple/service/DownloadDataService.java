package com.treelev.isimple.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import com.treelev.isimple.tasks.DownloadDataTask;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;


public class DownloadDataService extends Service {

    public final static String LAST_UPDATED_DATE = "date_for_last_update";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isExternalStorageAvailable()){
            Context context = getApplication();
            if(!SharedPreferencesManager.isPreparationUpdate(context)){
                Log.v("Test log start update", "start update");
                SharedPreferencesManager.setUpdateReady(context, false);
                SharedPreferencesManager.setPreparationUpdate(context, true);
                new DownloadDataTask(context).execute();
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
        return  externalStorageAvailable && externalStorageWriteable;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
