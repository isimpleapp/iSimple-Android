package com.treelev.isimple.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import com.treelev.isimple.activities.SplashActivity;
import com.treelev.isimple.domain.FileParseObject;
import com.treelev.isimple.utils.managers.WebServiceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class UpdateDataService extends Service  {

    public static final String UPDATE_READY = "update_ready";
    public static final String PARAM_PINTENT = "PENDING_INTENT";


    private PendingIntent mPi;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPi = intent.getParcelableExtra(PARAM_PINTENT);
        if(mPi != null){
            new UpdateDataTask().execute();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class UpdateDataTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            File directory = WebServiceManager.getDownloadDirectory();
            if (directory.exists()) {
                List<FileParseObject> fileParseObjectList = createFileList(directory.listFiles());
                for (FileParseObject fileParseObject : fileParseObjectList) {
                    fileParseObject.parseObjectDataToDB();
                }
                WebServiceManager.deleteDownloadDirectory();
                SharedPreferences.Editor editor = getApplication().getSharedPreferences(DownloadDataService.PREFS, MODE_MULTI_PROCESS).edit();
                editor.putBoolean(UPDATE_READY, true);
                editor.putLong(SplashActivity.TIME_LAST_UPDATE, Calendar.getInstance().getTimeInMillis() / SplashActivity.SECOND_TO_DAY);
                editor.commit();
                try {
                    mPi.send(UpdateDataService.this, SplashActivity.STATUS_FINISH, new Intent());
                } catch (PendingIntent.CanceledException e) {
                }
            }
            stopSelf();
            return null;
        }

        private List<FileParseObject> createFileList(File[] fileList) {
            List<FileParseObject> fileParseObjectList = new ArrayList<FileParseObject>();
            for (File file : fileList) {
                fileParseObjectList.add(new FileParseObject(file, getApplication()));
            }
            Collections.sort(fileParseObjectList);
            return fileParseObjectList;
        }
    }
}
