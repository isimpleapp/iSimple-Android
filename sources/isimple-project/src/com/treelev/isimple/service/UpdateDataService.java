package com.treelev.isimple.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.SplashActivity;
import com.treelev.isimple.domain.FileParseObject;
import com.treelev.isimple.parser.CatalogParser;
import com.treelev.isimple.parser.ItemPricesParser;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;
import com.treelev.isimple.utils.managers.WebServiceManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class UpdateDataService extends Service  {


    public static final String PARAM_PINTENT = "PENDING_INTENT";

    private PendingIntent mPi;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            mPi = intent.getParcelableExtra(PARAM_PINTENT);
            new UpdateDataTask().execute();
        } else {
            stopSelf();
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
            Log.v("Test log update data", "Update");
            Context context = getApplication();
            SharedPreferencesManager.setStartUpdate(context, true);
            boolean isCatalogUpdate = false;
            boolean isPriceUpdate = false;
            File directory = WebServiceManager.getDownloadDirectory();
            List<FileParseObject> fileParseObjectList = createFileList(directory.listFiles());
            for (FileParseObject fileParseObject : fileParseObjectList) {
                fileParseObject.parseObjectDataToDB();
                if(fileParseObject.getFileName().equalsIgnoreCase(CatalogParser.FILE_NAME)){
                    isCatalogUpdate = true;
                }
                if(fileParseObject.getFileName().equalsIgnoreCase(ItemPricesParser.FILE_NAME)){
                    isPriceUpdate = true;
                }
            }
            SharedPreferencesManager.setStartUpdate(context, false);
            SharedPreferencesManager.setUpdateReady(context, false);
            SharedPreferencesManager.refreshDateUpdate(context);
            if(isCatalogUpdate){
                SharedPreferencesManager.refreshDateCatalogUpdate(context);
            }
            if(isPriceUpdate){
                SharedPreferencesManager.refreshDatePriceUpdate(context);
            }
            try {
                mPi.send(UpdateDataService.this, SplashActivity.STATUS_FINISH, new Intent());
            } catch (PendingIntent.CanceledException e) {
            }
            finally {
                stopSelf();
            }
            return null;
        }

        private List<FileParseObject> createFileList(File[] fileList) {
            List<FileParseObject> fileParseObjectList = new ArrayList<FileParseObject>();
            if(fileList != null){
                for (File file : fileList) {
                    fileParseObjectList.add(new FileParseObject(file, getApplication()));
                }
                Collections.sort(fileParseObjectList);
            }
            return fileParseObjectList;
        }

    }
}
