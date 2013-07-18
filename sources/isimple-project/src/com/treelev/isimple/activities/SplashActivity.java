package com.treelev.isimple.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import com.treelev.isimple.R;
import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.domain.FileParseObject;
import com.treelev.isimple.service.DownloadDataService;
import com.treelev.isimple.service.UpdateDataService;
import com.treelev.isimple.utils.managers.WebServiceManager;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;

import java.io.*;
import java.util.*;

public class SplashActivity extends Activity {

    public static final String FROM_NOTIFICATION = "from_notification";
    public static final String TIME_LAST_UPDATE = "time_last_update";

    public static final int STATUS_FINISH = 101;
    public static final int TASK_UPDATE = 305;
    public static final long SECOND_TO_DAY = 86400000;
    private Dialog mDialog;

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
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(DownloadDataService.PREFS, MODE_MULTI_PROCESS);

        boolean firstStart = sharedPreferences.getBoolean(DownloadDataService.FIRST_START, true);
        boolean fromNotification = getIntent().getBooleanExtra(FROM_NOTIFICATION, false);
        boolean updateReady = sharedPreferences.getBoolean(UpdateDataService.UPDATE_READY, false);
        if (firstStart) {
            new ImportDBFromFileTask().execute(assetManager, sharedPreferences);
        } else if (fromNotification && needNotification(this)) {
            startUpdate();
        } else if(updateReady) {
            showNotification(getApplicationContext());
            startApplication();
        } else {
            showDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public static boolean needNotification(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadDataService.PREFS, MODE_MULTI_PROCESS);
        long lastTimeUpdate = sharedPreferences.getLong(TIME_LAST_UPDATE, -1);
        long currentTime = Calendar.getInstance().getTimeInMillis() / SECOND_TO_DAY;
        boolean needUpdateData = WebServiceManager.getDownloadDirectory().exists();
        return lastTimeUpdate != currentTime || needUpdateData;
    }

    public static void showNotification(Context context) {
        if(needNotification(context)){
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification(R.drawable.icon, context.getString(R.string.update_data_notify_label), System.currentTimeMillis());

            Intent newIntent = new Intent(context, SplashActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.putExtra(SplashActivity.FROM_NOTIFICATION, true);

            PendingIntent pIntent = PendingIntent.getActivity(context, 0, newIntent, 0);
            notification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.update_data_content_label), pIntent);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(1, notification);
        }
    }

    @Override
    public void onBackPressed() {
    }

    private List<FileParseObject> createFileList(File[] fileList) {
        List<FileParseObject> fileParseObjectList = new ArrayList<FileParseObject>();
        for (File file : fileList) {
            fileParseObjectList.add(new FileParseObject(file, this));
        }
        Collections.sort(fileParseObjectList);
        return fileParseObjectList;
    }

    @Override
    protected void onDestroy() {
        ((ISimpleApp)getApplication()).updateStateCart();
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == STATUS_FINISH){
            switch (requestCode){
                case TASK_UPDATE:
                    hideDialog();
                break;
            }
            startApplication();
        }
    }

    private void startApplication(){
        finish();
        Intent newIntent = new Intent(this, CatalogListActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private void startUpdate(){
        showDialog();
        Intent updateServiceIntent = new Intent(this, UpdateDataService.class);
        PendingIntent pi = createPendingResult(TASK_UPDATE, new Intent(), 0);
        updateServiceIntent.putExtra(UpdateDataService.PARAM_PINTENT, pi);
        startService(updateServiceIntent);
    }

    private void showDialog(){
        if(mDialog == null){
            mDialog = ProgressDialog.show(SplashActivity.this, getString(R.string.update_data_notify_label),
                    getString(R.string.update_data_wait_label), false, false);
        }
    }

    private void hideDialog(){
        if(mDialog != null){
            mDialog.dismiss();
        }
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
            startApplication();
        }

        private void importDBFromFile(boolean override, Object... params) {
            try {
                AssetManager am = (AssetManager) params[0];
                File file = new File("/data/data/com.treelev.isimple/databases/");
                file.mkdir();
                File dbFile = new File("/data/data/com.treelev.isimple/databases/iSimple.db");
                if (override || !dbFile.exists()) {
                    createDb(dbFile, am);
                    putFileDatesInPref(params);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void putFileDatesInPref(Object... params) {
            SharedPreferences.Editor prefEditor = ((SharedPreferences) params[1]).edit();
            for (String url : urlList) {
                prefEditor.putLong(url, new Date(113, 3, 1).getTime());
            }
            prefEditor.putLong(TIME_LAST_UPDATE, Calendar.getInstance().getTimeInMillis() / SECOND_TO_DAY - 1);
            prefEditor.putBoolean(DownloadDataService.FIRST_START, false);
            prefEditor.putBoolean(UpdateDataService.UPDATE_READY, true);
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
            WebServiceManager.deleteDownloadDirectory();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            SharedPreferences.Editor editor = SplashActivity.this.getSharedPreferences(DownloadDataService.PREFS, MODE_MULTI_PROCESS).edit();
            editor.putBoolean(DownloadDataService.FIRST_START, false);
            editor.commit();

            finish();
            Intent newIntent = new Intent(SplashActivity.this, CatalogListActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newIntent);
        }

    }

}