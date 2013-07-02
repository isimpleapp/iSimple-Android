package com.treelev.isimple.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import com.treelev.isimple.domain.LoadFileData;
import com.treelev.isimple.tasks.UnzipTask;
import com.treelev.isimple.utils.managers.WebServiceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UpdateDataService extends Service {

    private WebServiceManager webServiceManager;
    private final static String LOAD_FILE_DATA_URL = "http://s1.isimpleapp.ru/xml/ver0/Update-Index.xml";
    public final static String FILE_URL_FORMAT = "%s/Simple";
    public final static String LAST_UPDATED_DATE = "date_for_last_update";
    public final static String NEED_DATA_UPDATE = "need_data_to_update";

    @Override
    public void onCreate() {
        super.onCreate();
        webServiceManager = new WebServiceManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        new UpdateDataTask(this).execute(webServiceManager, sharedPreferences);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class UpdateDataTask extends AsyncTask<Object, Void, List<File>> {

        private Context context;

        private UpdateDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<File> doInBackground(Object... params) {
            WebServiceManager webServiceManager = (WebServiceManager) params[0];
            List<LoadFileData> loadFileDataList = webServiceManager.getLoadFileData(LOAD_FILE_DATA_URL);
            List<File> fileList = null;
            if (loadFileDataList.size() > 0) {
                SharedPreferences preferenceManager = (SharedPreferences) params[1];
                fileList = new ArrayList<File>();
                for (LoadFileData loadFileData : loadFileDataList) {
                    String fileUrl = loadFileData.getFileUrl();
                    if (preferenceManager.getLong(fileUrl, -1) < loadFileData.getLoadDate().getTime()) {
                        fileList.add(webServiceManager.downloadFile(fileUrl));
                        SharedPreferences.Editor editor = preferenceManager.edit();
                        editor.putLong(fileUrl, loadFileData.getLoadDate().getTime());
                        editor.commit();
                    }
                }
            }
            return fileList;
        }

        @Override
        protected void onPostExecute(List<File> fileList) {
            super.onPostExecute(fileList);
            if (fileList != null && fileList.size() > 0) {
                new UnzipTask(context).execute(fileList.toArray(new File[fileList.size()]));
            }
        }
    }
}
