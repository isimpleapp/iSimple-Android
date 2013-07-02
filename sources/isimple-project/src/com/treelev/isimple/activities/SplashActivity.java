package com.treelev.isimple.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.FileParseObject;
import com.treelev.isimple.service.UpdateDataService;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.preference.PreferenceManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SplashActivity extends Activity {

    private final static String[] urlList = new String[]{
            "http://s1.isimpleapp.ru/xml/ver0/Catalog-Update.xmlz",
            "http://s1.isimpleapp.ru/xml/ver0/Item-Prices.xmlz",
            "http://s1.isimpleapp.ru/xml/ver0/Item-Availability.xmlz",
            "http://s1.isimpleapp.ru/xml/ver0/Locations-And-Chains-Update.xmlz",
            "http://s1.isimpleapp.ru/xml/ver0/Delivery.xmlz",
            "http://s2.isimpleapp.ru/xml/ver0/Featured.xmlz",
            "http://s1.isimpleapp.ru/xml/ver0/Deprecated.xmlz"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        AssetManager assetManager = getApplicationContext().getAssets();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean needUpdateData = android.preference.PreferenceManager.getDefaultSharedPreferences(this).getBoolean(UpdateDataService.NEED_DATA_UPDATE, false);
        if (!needUpdateData) {
            new ImportDBFromFileTask().execute(assetManager, sharedPreferences);
        } else {
            File directory = new File(String.format(UpdateDataService.FILE_URL_FORMAT, Environment.getExternalStorageDirectory()));
            List<FileParseObject> fileParseObjectList = createFileList(directory.listFiles());
            new UpdateDataTask().execute(fileParseObjectList.toArray(new FileParseObject[fileParseObjectList.size()]));
        }
    }

    @Override
    public void onBackPressed() {}

    private List<FileParseObject> createFileList(File[] fileList) {
        List<FileParseObject> fileParseObjectList = new ArrayList<FileParseObject>();
        for (File file : fileList) {
            fileParseObjectList.add(new FileParseObject(file, this));
        }
        Collections.sort(fileParseObjectList);
        return fileParseObjectList;
    }

    private class ImportDBFromFileTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object... params) {
            importDBFromFile(false, params);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            finish();
            startActivity(new Intent(SplashActivity.this, CatalogListActivityNew.class));
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        }

        private void importDBFromFile(boolean ovveride, Object... params) {
            try {
                AssetManager am = (AssetManager) params[0];
                File file = new File("/data/data/com.treelev.isimple/databases/");
                file.mkdir();
                File dbFile = new File("/data/data/com.treelev.isimple/databases/iSimple.db");
                if (ovveride) {
                    createDb(dbFile, am);
                    putFileDatesInPref(params);
                } else {
                    if (!dbFile.exists()) {
                        createDb(dbFile, am);
                        putFileDatesInPref(params);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void putFileDatesInPref(Object... params) {
            SharedPreferences.Editor prefEditor = ((SharedPreferences) params[1]).edit();
            for (String url : urlList) {
                prefEditor.putLong(url, new Date(113, 7, 1).getTime());
            }
            prefEditor.commit();
        }

        private void createDb(File dbFile, AssetManager am) throws IOException {
            OutputStream os = new FileOutputStream(dbFile);
            byte[] b = new byte[4096];
            int r;
            InputStream is = am.open("iSimple.db");
            while ((r = is.read(b)) > -1) {
                os.write(b, 0, r);
            }
            is.close();
            os.close();
        }
    }

    private class UpdateDataTask extends AsyncTask<FileParseObject, Void, Void> {

        private Dialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SplashActivity.this, getString(R.string.update_data_notify_label),
                    getString(R.string.update_data_wait_label), false, false);
        }

        @Override
        protected Void doInBackground(FileParseObject... fileParseObjects) {
            for (FileParseObject fileParseObject : fileParseObjects) {
                fileParseObject.parseObjectDataToDB();
            }
            deleteFileDir();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            android.content.SharedPreferences.Editor editor = android.preference.PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).edit();
            editor.putBoolean(UpdateDataService.NEED_DATA_UPDATE, false);
            editor.commit();
            finish();
            Intent newIntent = new Intent(SplashActivity.this, CatalogListActivityNew.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newIntent);
        }

        private void deleteFileDir() {
            new File(String.format(UpdateDataService.FILE_URL_FORMAT, Environment.getExternalStorageDirectory())).delete();
        }
    }
}