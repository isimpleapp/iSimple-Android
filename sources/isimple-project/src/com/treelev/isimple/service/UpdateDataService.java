package com.treelev.isimple.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import com.treelev.isimple.domain.LoadFileData;
import com.treelev.isimple.tasks.UnzipTask;
import com.treelev.isimple.utils.managers.WebServiceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UpdateDataService extends Service {

    private WebServiceManager webServiceManager;
    private SharedPreferences sharedPreferences;

    private final static String LOAD_FILE_DATA_URL = "http://s1.isimpleapp.ru/xml/ver0/Update-Index.xml";
    public final static String FILE_URL_FORMAT = "%s/Simple";
    public final static String LAST_UPDATED_DATE = "date_for_last_update";
    public final static String NEED_DATA_UPDATE = "need_data_to_update";
    public final static String PREFS = "iSimple_prefs";

    @Override
    public void onCreate() {
        super.onCreate();
        webServiceManager = new WebServiceManager();
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFS, MODE_MULTI_PROCESS);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new UpdateDataTask(getApplicationContext()).execute();
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
            List<LoadFileData> loadFileDataList = webServiceManager.getLoadFileData(LOAD_FILE_DATA_URL);
            List<File> fileList = null;
            if (loadFileDataList.size() > 0) {
                fileList = new ArrayList<File>();
                for (LoadFileData loadFileData : loadFileDataList) {
                    String fileUrl = loadFileData.getFileUrl();
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

        @Override
        protected void onPostExecute(List<File> fileList) {
            super.onPostExecute(fileList);
            if (fileList != null && fileList.size() > 0) {
                new UnzipTask(getApplicationContext(), sharedPreferences).execute(fileList.toArray(new File[fileList.size()]));
            }
        }
    }
}
