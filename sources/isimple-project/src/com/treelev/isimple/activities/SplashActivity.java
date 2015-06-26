
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
import org.holoeverywhere.widget.Toast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.treelev.isimple.R;
import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.service.SyncServcie;
import com.treelev.isimple.utils.Constants;
import com.treelev.isimple.utils.LogUtils;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

public class SplashActivity extends Activity {

	private final static String TAG = SplashActivity.class.getSimpleName();
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    private String SENDER_ID = "Your-Sender-ID";
    private GoogleCloudMessaging gcm;
    private int messageId;
    private String registrationId;
	
    public static final String FROM_NOTIFICATION = "from_notification";

    public static final int STATUS_FINISH = 101;
    public static final int TASK_UPDATE = 305;
    private Dialog mDialog;
    private SyncStatusReceiver syncStatusReceiver;

    private final static String[] urlList = new String[] {
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
        
        AppsFlyerLib.sendTracking(getApplicationContext()); 
        
        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            registrationId = getRegistrationId(this);

            if (registrationId.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        Context context = getApplication();

        boolean firstStart = SharedPreferencesManager.isFirstStart(context);

        if (firstStart) {
            LogUtils.i("Test log", "SplashActivity firstStart");
            AssetManager assetManager = getApplicationContext().getAssets();
            new ImportDBFromFileTask().execute(assetManager,
                    SharedPreferencesManager.getSharedPreferences(context));
        } else if (SharedPreferencesManager.isFirstTimeNewSync(ISimpleApp.getInstantce())) {
            LogUtils.i("Test log", "SplashActivity NOT firstStart, but first new sync");
            showDialog();
            SplashActivity.this.registerReceiver(new SyncFinishedReceiver(), new IntentFilter(
                    Constants.INTENT_ACTION_SYNC_FINISHED));
            sendBroadcast(new Intent(Constants.INTENT_ACTION_SYNC_TRIGGER));
            SharedPreferencesManager.setFirstTimeNewSync(ISimpleApp.getInstantce(), false);
        } else {
            LogUtils.i("Test log", "SplashActivity NOT firstStart, NOT first new sync");
            startApplication(true);
        }

        syncStatusReceiver = new SyncStatusReceiver();
    }

 // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(syncStatusReceiver,
                new IntentFilter(Constants.INTENT_ACTION_SYNC_STATE_UPDATE));
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncStatusReceiver);
    }

    @Override
    public void onBackPressed() {
    }

    // private List<FileParseObject> createFileList(File[] fileList) {
    // List<FileParseObject> fileParseObjectList = new
    // ArrayList<FileParseObject>();
    // for (File file : fileList) {
    // fileParseObjectList.add(new FileParseObject(file, this));
    // }
    // Collections.sort(fileParseObjectList);
    // return fileParseObjectList;
    // }

    @Override
    protected void onDestroy() {
        ((ISimpleApp) getApplication()).updateStateCart();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == STATUS_FINISH) {
            switch (requestCode) {
                case TASK_UPDATE:
                    hideDialog();
                    break;
            }
            startApplication(false);
        }
    }

    private void startApplication(boolean sleep) {
        new StartApplication().execute(sleep);
    }

    private void showDialog() {
        if (mDialog == null) {
            mDialog = ProgressDialog.show(SplashActivity.this,
                    getString(R.string.update_data_notify_label),
                    getString(R.string.update_data_wait_label), false, false);
        }
    }

    private void hideDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void updateDialog(String message) {
        if (mDialog != null) {
            ((ProgressDialog) mDialog).setMessage(message);
        }
    }

    private class ImportDBFromFileTask extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... params) {
            importDBFromFile(true, params);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (SharedPreferencesManager.isFirstTimeNewSync(ISimpleApp.getInstantce())) {
                showDialog();
                SplashActivity.this.registerReceiver(new SyncFinishedReceiver(), new IntentFilter(
                        Constants.INTENT_ACTION_SYNC_FINISHED));
//                sendBroadcast(new Intent(Constants.INTENT_ACTION_SYNC_TRIGGER));
                SyncServcie.startSync(ISimpleApp.getInstantce(), null);
                SharedPreferencesManager.setFirstTimeNewSync(ISimpleApp.getInstantce(), false);
            } else {
                startApplication(false);
            }
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

        private Date getDate() {
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

    private class StartApplication extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... booleans) {
            if (booleans[0]) {
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
            Intent newIntent = new Intent(SplashActivity.this, CatalogListActivityNew.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newIntent);
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        }
    }

    private class SyncFinishedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i("Test log", "SyncFinishedReceiver onReceive");
            hideDialog();
            startApplication(false);
            SplashActivity.this.unregisterReceiver(this);
            
            if (!intent.getBooleanExtra(Constants.INTENT_ACTION_SYNC_SUCCESSFULL, true)) {
                Toast.makeText(context, R.string.sync_error_update_index_error, Toast.LENGTH_LONG).show();
            }
        }

    }

    private class SyncStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateDialog(intent.getStringExtra(Constants.INTENT_EXTRA_SYNC_STATE));
        }

    }
    
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }
    
    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }
    
    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }
    
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void registerInBackground() {
        new AsyncTask() {

			protected Object doInBackground(Object... params) {
				String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(SplashActivity.this);
                    }
                    registrationId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + registrationId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(SplashActivity.this, registrationId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
			}
        }.execute(null, null, null);
    }
}
