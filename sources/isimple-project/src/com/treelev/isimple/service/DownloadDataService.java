package com.treelev.isimple.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.treelev.isimple.domain.LoadFileData;
import com.treelev.isimple.tasks.UnzipTask;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;
import com.treelev.isimple.utils.managers.WebServiceManager;


public class DownloadDataService extends Service {

    public final static String LAST_UPDATED_DATE = "date_for_last_update";
    private static DownloadDataTask mDownloadDataTask;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isExternalStorageAvailable()){
            Context context = getApplication();
            if(!SharedPreferencesManager.isPreparationUpdate(context)){
                Log.v("Test log start update", "start update");
                SharedPreferencesManager.setUpdateReady(context, false);
                SharedPreferencesManager.setPreparationUpdate(context, true);
                mDownloadDataTask = new DownloadDataTask(context);
                mDownloadDataTask.execute();
            }
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    public static boolean isDownloadDataTaskRunning() {
    	if (mDownloadDataTask == null || mDownloadDataTask.getStatus() != AsyncTask.Status.RUNNING) {
    		return false;
    	} else {
    		return true;
    	}
    }

    public boolean isExternalStorageAvailable() {
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
    
    public class DownloadDataTask extends AsyncTask<Object, Void, List<File>> {

        private Context context;
        private WebServiceManager webServiceManager;
        private SharedPreferences sharedPreferences;

        private boolean error;

        public DownloadDataTask(Context context) {
            this.webServiceManager = new WebServiceManager();
            this.sharedPreferences = SharedPreferencesManager.getSharedPreferences(context);
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            error = false;
        }

        @Override
        protected List<File> doInBackground(Object... params) {
            try {
                List<LoadFileData> loadFileDataList = webServiceManager.getLoadFileData(SharedPreferencesManager.getUpdateFileUrl());
                List<File> fileList = null;
                if (loadFileDataList.size() > 0) {
                    fileList = new ArrayList<File>();
                    for (LoadFileData loadFileData : loadFileDataList) {
                        String fileUrl = loadFileData.getFileUrl();
                        Log.v("Test log fileUrl", fileUrl);
                        Log.v("Test log time preference", String.valueOf(sharedPreferences.getLong(fileUrl, -1)));
                        Log.v("Test log time file", String.valueOf(loadFileData.getLoadDate().getTime()));
                        if (sharedPreferences.getLong(fileUrl, -1) < loadFileData.getLoadDate().getTime()) {
                            fileList.add(webServiceManager.downloadFile(fileUrl));
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putLong(fileUrl, loadFileData.getLoadDate().getTime());
                            editor.commit();
                        }
                    }
                }
                return fileList;
            }
            catch (Exception ex) {
                error = true;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<File> fileList) {
            super.onPostExecute(fileList);
            
            mDownloadDataTask = null;
            
            if (fileList != null && fileList.size() > 0 && !error) {
                new UnzipTask(context).execute(fileList.toArray(new File[fileList.size()]));
            } else {
                SharedPreferencesManager.setPreparationUpdate(context, false);
            }
        }
    }
}
