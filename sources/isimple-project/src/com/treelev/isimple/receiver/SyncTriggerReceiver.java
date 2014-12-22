package com.treelev.isimple.receiver;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.treelev.isimple.service.DownloadDataService;
import com.treelev.isimple.service.SyncServcie;
import com.treelev.isimple.utils.Constants;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

public class SyncTriggerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        
        boolean downloadDataTaskRunning = SyncServcie.isSyncDataTaskRunning();
        if (!downloadDataTaskRunning
                && SharedPreferencesManager.isPreparationUpdate(context)) {
            // This means app crashed, was killed or updated when update was in
            // progress. Thus we should clear all download flags.
            Log.v("Test log", "CatalogListActivity clear download update flags");
            SharedPreferencesManager.setPreparationUpdate(context, false);
        }
        if (!SharedPreferencesManager.isPreparationUpdate(context)
                && !SharedPreferencesManager.isUpdateReady(context)) {
            context.startService(new Intent(context, SyncServcie.class));
        }
        
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.roll(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        alarm.set(
            alarm.RTC_WAKEUP,
            cal.getTimeInMillis(),
            PendingIntent.getBroadcast(context, 0, new Intent(Constants.INTENT_ACTION_SYNC_TRIGGER), 0)
        );
    }

}
