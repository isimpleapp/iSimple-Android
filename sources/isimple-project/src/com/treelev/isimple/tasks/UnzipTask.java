package com.treelev.isimple.tasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.SplashActivity;
import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.service.UpdateDataService;
import org.holoeverywhere.preference.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipTask extends AsyncTask<File, Void, File[]> {

    private NotificationManager notificationManager;
    private Context context;


    public UnzipTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected File[] doInBackground(File... params) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return params;
    }

    @Override
    protected void onPostExecute(File[] aVoid) {
        super.onPostExecute(aVoid);
        Notification notification = new Notification(R.drawable.icon, context.getString(R.string.update_data_notify_label), System.currentTimeMillis());
        //SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        Log.i(getClass().getName(), (context).getApplicationContext().getPackageName());
        SharedPreferences.Editor editor = ((ISimpleApp) context).getSharedPreferences("iSimple_prefs", Context.MODE_PRIVATE).edit();
        editor.putBoolean(UpdateDataService.NEED_DATA_UPDATE, true);
        editor.apply();
        Intent newIntent = new Intent(context, SplashActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, newIntent, 0);
        notification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.update_data_content_label), pIntent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, notification);
    }
}
