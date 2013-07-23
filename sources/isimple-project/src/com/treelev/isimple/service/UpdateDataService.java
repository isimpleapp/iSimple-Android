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
import com.treelev.isimple.R;
import com.treelev.isimple.activities.SplashActivity;
import com.treelev.isimple.domain.FileParseObject;
import com.treelev.isimple.parser.CatalogParser;
import com.treelev.isimple.parser.ItemPricesParser;
import com.treelev.isimple.utils.managers.WebServiceManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class UpdateDataService extends Service  {

    public static final String UPDATE_READY = "update_ready";
    public static final String PARAM_PINTENT = "PENDING_INTENT";
    public static final String DATE_UPDATE = "date_update";
    public static final String DATE_PRICE_UPDATE = "date__price_update";
    public static final String DATE_CATALOG_UPDATE = "date__catalog_update";


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
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(DownloadDataService.PREFS, MODE_MULTI_PROCESS);
            boolean needUpdate = sharedPreferences.getBoolean(SplashActivity.UPDATE_DATA_READY, false);
            String datePriceUpdate = "";
            String dateCatalogUpdate = "";
            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
            java.util.Date currentDate =  Calendar.getInstance().getTime();
            File directory = WebServiceManager.getDownloadDirectory();
            boolean updateDataReady = directory.exists();
            if (updateDataReady) {
                List<FileParseObject> fileParseObjectList = createFileList(directory.listFiles());
                for (FileParseObject fileParseObject : fileParseObjectList) {
                    fileParseObject.parseObjectDataToDB();
                    if(fileParseObject.getFileName().equalsIgnoreCase(CatalogParser.FILE_NAME)){
                        dateCatalogUpdate = formatDate.format(currentDate);
                    }
                    if(fileParseObject.getFileName().equalsIgnoreCase(ItemPricesParser.FILE_NAME)){
                        datePriceUpdate = formatDate.format(currentDate);
                    }
                }
                WebServiceManager.deleteDownloadDirectory();
            } else if(!updateDataReady) {
                SplashActivity.showWarningNotification(getApplication());
            }
            try {
                mPi.send(UpdateDataService.this, SplashActivity.STATUS_FINISH, new Intent());
            } catch (PendingIntent.CanceledException e) {
            }finally {
                SharedPreferences.Editor editor = getApplication().getSharedPreferences(DownloadDataService.PREFS, MODE_MULTI_PROCESS).edit();
                editor.putBoolean(SplashActivity.UPDATE_START, false);
                if(!updateDataReady){
                    editor.putBoolean(UPDATE_READY, true);
                    editor.putBoolean(SplashActivity.UPDATE_DATA_READY, false);
                    editor.putLong(SplashActivity.TIME_LAST_UPDATE, Calendar.getInstance().getTimeInMillis() / SplashActivity.SECOND_TO_DAY);
                    editor.putString(DATE_UPDATE, formatDate.format(currentDate));
                    if(dateCatalogUpdate.length() > 0){
                        editor.putString(DATE_CATALOG_UPDATE, dateCatalogUpdate);
                    }
                    if(datePriceUpdate.length() > 0){
                        editor.putString(DATE_PRICE_UPDATE, datePriceUpdate);
                    }
                }
                editor.commit();
                stopSelf();
            }
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
