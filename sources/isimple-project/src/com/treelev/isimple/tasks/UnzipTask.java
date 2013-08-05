package com.treelev.isimple.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.treelev.isimple.activities.SplashActivity;
import com.treelev.isimple.service.DownloadDataService;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipTask extends AsyncTask<File, Void, File[]> {

    private boolean error;

    private Context context;
    private SharedPreferences sharedPreferences;


    public UnzipTask(Context context) {
        this.context = context;
        this.sharedPreferences = SharedPreferencesManager.getSharedPreferences(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        error = false;
    }

    @Override
    protected File[] doInBackground(File... params) {
        try {
            for (File file : params) {
                if(file != null){
                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();
                    byte[] buffer = new byte[4096];
                    FileOutputStream fileOutputStream = null;
                    if (zipEntry == null) {
                        Log.i(getClass().getName(), "File " + file.getPath() + " don't unpack");
                    }
                    while (zipEntry != null) {
                        String fileName = zipEntry.getName();
                        File newFile = new File(file.getParent() + File.separator + fileName);
                        fileOutputStream = new FileOutputStream(newFile);
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, len);
                        }
                        fileOutputStream.close();
                        zipEntry = zipInputStream.getNextEntry();
                    }
                    zipInputStream.closeEntry();
                    zipInputStream.close();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    file.delete();
                }
            }
        } catch (Exception e) {
            error = true;
        }
        finally {

        }
        return params;
    }

    @Override
    protected void onPostExecute(File[] aVoid) {
        super.onPostExecute(aVoid);
        Log.v("Test log unzip post", "_");
        if (!error) {
            Log.v("Test log unzip post", "error = false");
            SharedPreferencesManager.setUpdateReady(context, true);
            SplashActivity.showUpdateNotification(context);
        }
        SharedPreferencesManager.setPreparationUpdate(context, false);
    }
}
