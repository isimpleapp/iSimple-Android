
package com.treelev.isimple.utils.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.utils.Constants;
import com.treelev.isimple.utils.LogUtils;

public class ParseLogUtils {

    public static void logToParse(int deleteCount, int insertCount, int updatedCount, ParseFile log) {
        ParseObject gameScore = new ParseObject("comsimpleisimpleLOG");
        gameScore.put("UDID", ISimpleApp.getDeviceId());
        gameScore.put("application", "com.simple.isimple");
        gameScore.put("deleteCount", deleteCount);
        gameScore.put("deviceName", ISimpleApp.getDeviceName());
        gameScore.put("insertCount", insertCount);
        gameScore.put("log", log);
        gameScore.put("updatedCount", updatedCount);
        gameScore.saveInBackground();
    }

    public static File getTempLogFilesDirectory() {
        File syncLogDir = new File(Environment.getExternalStorageDirectory() + "/iSimpleTemp");
        if (!syncLogDir.exists()) {
            syncLogDir.mkdirs();
        }

        return syncLogDir;
    }

    public static ParseFile createSyncLogFile(SyncLogEntity logEntity) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(logEntity);
        byte[] data = json.getBytes();

        ParseFile syncLogFile = new ParseFile(Constants.SYNC_LOG_FILE_NAME, data);
        syncLogFile.saveInBackground();

        return syncLogFile;
    }

}
