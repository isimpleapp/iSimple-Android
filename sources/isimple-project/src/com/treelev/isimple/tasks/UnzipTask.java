package com.treelev.isimple.tasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.SplashActivity;
import com.treelev.isimple.service.DownloadDataService;
import com.treelev.isimple.utils.managers.WebServiceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipTask extends AsyncTask<File, Void, File[]> {

    private static UnzipTask task;

    private boolean running;
    private boolean error;

    private Context context;
    private SharedPreferences sharedPreferences;

    public static UnzipTask getUnzipTask(Context context) {
        if (task == null) {
            task = new UnzipTask(context);
        }
        return task;
    }

    private UnzipTask(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(DownloadDataService.PREFS, Context.MODE_MULTI_PROCESS);
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        error = false;
    }

    @Override
    protected File[] doInBackground(File... params) {
        running = true;
        try {
            for (File file : params) {
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
        } catch (Exception e) {
            error = true;
        }
        finally {
            running = false;
            task = null;
        }
        return params;
    }

    @Override
    protected void onPostExecute(File[] aVoid) {
        super.onPostExecute(aVoid);
        if (!error) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(DownloadDataService.NEED_DATA_UPDATE, true);
            editor.commit();

            SplashActivity.showNotification(context);
        }
    }
}
