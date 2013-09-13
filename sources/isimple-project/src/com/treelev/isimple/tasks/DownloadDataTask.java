package com.treelev.isimple.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.treelev.isimple.activities.SplashActivity;
import com.treelev.isimple.domain.LoadFileData;
import com.treelev.isimple.service.DownloadDataService;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;
import com.treelev.isimple.utils.managers.WebServiceManager;
import org.holoeverywhere.app.Activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadDataTask extends AsyncTask<Object, Void, List<File>> {

    private final static String LOAD_FILE_DATA_URL = "http://s1.isimpleapp.ru/xml/ver0/Update-Index.xml";

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
            List<LoadFileData> loadFileDataList = webServiceManager.getLoadFileData(LOAD_FILE_DATA_URL);
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
        if (fileList != null && fileList.size() > 0 && !error) {
            new UnzipTask(context).execute(fileList.toArray(new File[fileList.size()]));
        } else {
            SharedPreferencesManager.setPreparationUpdate(context, false);
        }
    }
}
