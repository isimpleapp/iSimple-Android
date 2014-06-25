package com.treelev.isimple.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.treelev.isimple.activities.SplashActivity;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

public class UnzipTask extends AsyncTask<File, Void, File[]> {

    private ArrayList<Boolean> errors = new ArrayList<Boolean>();

    private Context context;
    private SharedPreferences sharedPreferences;


    public UnzipTask(Context context) {
        this.context = context;
        this.sharedPreferences = SharedPreferencesManager.getSharedPreferences(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        errors.clear();
    }

    @Override
    protected File[] doInBackground(File... params) {
        
            for (File file : params) {
            	try {
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
                        String xmlTag = SharedPreferencesManager.getUpdateFileName(file.getName());
                        SharedPreferencesManager.putUpdateFileName(xmlTag, newFile.getName());
                        SharedPreferencesManager.deletePreference(file.getName());
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
                    errors.add(false);
                }
            	} catch (Exception e) {
                	e.printStackTrace();
                	errors.add(true);
                }
                finally {

                }
            }
        
        return params;
    }

    @Override
    protected void onPostExecute(File[] aVoid) {
        super.onPostExecute(aVoid);
        Log.v("Test log unzip post", "_");
        Log.v("Test log unzip post", ", errors size = ");
        Log.v("Test log unzip post", ", errors.contains(false) = " + errors.contains(false));
        if (errors.contains(false)) {
            SharedPreferencesManager.setUpdateReady(context, true);
            SplashActivity.showUpdateNotification(context);
        }
        SharedPreferencesManager.setPreparationUpdate(context, false);
    }
}
