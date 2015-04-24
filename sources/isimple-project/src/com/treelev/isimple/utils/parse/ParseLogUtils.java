
package com.treelev.isimple.utils.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parse.ParseObject;
import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.utils.Constants;

public class ParseLogUtils {

    public static void logToParse(int deleteCount, int insertCount, int updatedCount, File log) {
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
    
    public static File createSyncLogFile(SyncLogEntity logEntity) {
      File syncLogFile = new File(ParseLogUtils.getTempLogFilesDirectory(), Constants.SYNC_LOG_FILE_NAME);
      try {
          
          if (!syncLogFile.exists()) {
              syncLogFile.createNewFile();
          }
          FileOutputStream out = new FileOutputStream(syncLogFile);
          Gson gson = new GsonBuilder().setPrettyPrinting().create();
          out.write(gson.toJson(logEntity).getBytes());
          out.close();
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
      
      return syncLogFile;
  }

}
