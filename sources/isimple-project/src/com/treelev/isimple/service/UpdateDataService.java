package com.treelev.isimple.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.treelev.isimple.activities.SplashActivity;
import com.treelev.isimple.domain.FileParseObject;
import com.treelev.isimple.parser.CatalogParser;
import com.treelev.isimple.parser.ItemPricesParser;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;
import com.treelev.isimple.utils.managers.WebServiceManager;

public class UpdateDataService extends Service  {

    public static final String PARAM_PINTENT = "PENDING_INTENT";

    private PendingIntent mPi;
    private static UpdateDataTask mUpdateDataTask;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            mPi = intent.getParcelableExtra(PARAM_PINTENT);
            mUpdateDataTask = new UpdateDataTask();
            mUpdateDataTask.execute();
        } else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    public static boolean isUpdateDataTaskRunning() {
    	if (mUpdateDataTask == null || mUpdateDataTask.getStatus() != AsyncTask.Status.RUNNING) {
    		return false;
    	} else {
    		return true;
    	}
    }

    private class UpdateDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.v("Test log update data", "Update");
            Context context = getApplication();
            SharedPreferencesManager.setStartUpdate(context, true);
            boolean isCatalogUpdate = false;
            boolean isPriceUpdate = false;
//            boolean isDiscountReady = false;
            File directory = WebServiceManager.getDownloadDirectory();
            List<FileParseObject> fileParseObjectList = createFileList(directory.listFiles());
            Log.v("Test log", "UpdateDataService, fileParseObjectList size = " + fileParseObjectList.size());
            for (FileParseObject fileParseObject : fileParseObjectList) {
                fileParseObject.parseObjectDataToDB();
                Log.v("Test log", "UpdateDataService, fileParseObject.getFileName() = " + fileParseObject.getFileName());
                Log.v("Test log", "UpdateDataService, CatalogParser.getFileName() = " + CatalogParser.getFileName());
                Log.v("Test log", "UpdateDataService, ItemPricesParser.getFileName() = " + ItemPricesParser.getFileName());
                if(fileParseObject.getFileName().equalsIgnoreCase(CatalogParser.getFileName())){
                    isCatalogUpdate = true;
                }
                if(fileParseObject.getFileName().equalsIgnoreCase(ItemPricesParser.getFileName())){
                    isPriceUpdate = true;
                }
//                if(fileParseObject.getFileName().equalsIgnoreCase(ItemPriceDiscountParser.getFileName())){
//                	isDiscountReady = true;
//                }
            }
            SharedPreferencesManager.setStartUpdate(context, false);
            SharedPreferencesManager.setUpdateReady(context, false);
            SharedPreferencesManager.refreshDateUpdate(context);
            if(isCatalogUpdate){
                SharedPreferencesManager.refreshDateCatalogUpdate(context);
            }
            if(isPriceUpdate){
                SharedPreferencesManager.refreshDatePriceUpdate(context);
            }
            try {
                mPi.send(UpdateDataService.this, SplashActivity.STATUS_FINISH, new Intent());
            } catch (PendingIntent.CanceledException e) {
            }
            finally {
                stopSelf();
            }
            return null;
        }

        @Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			mUpdateDataTask = null;
		}

		private List<FileParseObject> createFileList(File[] fileList) {
            List<FileParseObject> fileParseObjectList = new ArrayList<FileParseObject>();
            if(fileList != null){
                for (File file : fileList) {
                	Log.i("", "file in directory: " + file.getName());
                    fileParseObjectList.add(new FileParseObject(file, getApplication()));
                }
                Collections.sort(fileParseObjectList);
            }
            return fileParseObjectList;
        }

    }
}
