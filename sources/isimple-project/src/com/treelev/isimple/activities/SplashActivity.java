package com.treelev.isimple.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.treelev.isimple.R;
import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.service.UpdateDataService;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

public class SplashActivity extends Activity {

    public static final String FROM_NOTIFICATION = "from_notification";

    public static final int STATUS_FINISH = 101;
    public static final int TASK_UPDATE = 305;
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

        Context context = getApplication();

        boolean fromNotification = getIntent().getBooleanExtra(FROM_NOTIFICATION, false);

        boolean firstStart = SharedPreferencesManager.isFirstStart(context);
        boolean updateReady = isUpdateReady(context);
        boolean updateStart = SharedPreferencesManager.isStartUpdate(context);
        boolean isServiceUpdateTaskRunning = UpdateDataService.isUpdateDataTaskRunning();
        Log.v("Test log", "SplashActivity isServiceRunning = " + isServiceUpdateTaskRunning);

        if (firstStart) {
        	Log.v("Test log", "SplashActivity firstStart");
            AssetManager assetManager = getApplicationContext().getAssets();
            new ImportDBFromFileTask().execute(assetManager, SharedPreferencesManager.getSharedPreferences(context));
        } else if (fromNotification && updateReady && !updateStart) {
            Log.v("Test log", "SplashActivity start Update");
            startUpdate();
        } else if (updateReady && !isServiceUpdateTaskRunning && updateStart) {
			// This means app crashed, was killed or updated when update was in
			// progress. Thus we should clear all update flags.
        	Log.v("Test log", "SplashActivity clear update flags");
			SharedPreferencesManager.setUpdateReady(this, false);
			SharedPreferencesManager.setStartUpdate(this, false);
			startApplication(true);
            if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                showWarningNotification(getApplicationContext());
            }
        } else if(updateReady) {
            Log.v("Test log", "SplashActivity ");
            showUpdateNotification(getApplicationContext());
            startApplication(true);
        } else if(updateStart){
            showDialog();
        }
        else {
          startApplication(true);
          if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
              showWarningNotification(getApplicationContext());
          }
        }
    }
    
    private static boolean isUpdateReady(Context context){
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && SharedPreferencesManager.isUpdateReady(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showWarningNotification(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon, context.getString(R.string.update_data_notify_warning_label), System.currentTimeMillis());

        Intent newIntent = new Intent(context, SplashActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, newIntent, 0);
        notification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.update_data_notify_content_warning), pIntent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(1, notification);
    }

    public static void showUpdateNotification(Context context) {
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

    @Override
    public void onBackPressed() {
    }

//    private List<FileParseObject> createFileList(File[] fileList) {
//        List<FileParseObject> fileParseObjectList = new ArrayList<FileParseObject>();
//        for (File file : fileList) {
//            fileParseObjectList.add(new FileParseObject(file, this));
//        }
//        Collections.sort(fileParseObjectList);
//        return fileParseObjectList;
//    }

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
            startApplication(false);
        }
    }

    private void startApplication(boolean sleep){
        new StartApplication().execute(sleep);
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
            startApplication(false);
        }

        private void importDBFromFile(boolean override, Object... params) {
        	Log.i("", "importDBFromFile");
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
            Log.i("", "importDBFromFile end");
        }

        private void putFileDatesInPref(Object... params) {
            SharedPreferences.Editor prefEditor = ((SharedPreferences) params[1]).edit();
            for (String url : urlList) {
                prefEditor.putLong(url, getDate().getTime());
            }
            SharedPreferencesManager.setFirstStart(getApplication(), false);
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

        private Date getDate(){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, getResources().getInteger(R.integer.year));
            cal.set(Calendar.MONTH, getResources().getInteger(R.integer.month));
            cal.set(Calendar.DAY_OF_MONTH, getResources().getInteger(R.integer.day));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }
    }

    private class StartApplication extends AsyncTask<Boolean, Void, Void>{

        @Override
        protected Void doInBackground(Boolean... booleans) {
            if(booleans[0]){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
            Intent newIntent = new Intent(SplashActivity.this, CatalogListActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newIntent);
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        }
    }
}