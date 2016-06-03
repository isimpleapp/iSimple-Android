package com.treelev.isimple.utils;

import android.util.Log;

import com.treelev.isimple.BuildConfig;

public class LogUtils {
	public static void d(String TAG, String message) {
		if (BuildConfig.DEBUG)
			Log.d(TAG, message);
	}

	public static void i(String TAG, String message) {
		if (BuildConfig.DEBUG)
			Log.i(TAG, message);
	}

	public static void w(String TAG, String message) {
		if (BuildConfig.DEBUG)
			Log.w(TAG, message);
	}

	public static void e(String TAG, String message) {
		if (BuildConfig.DEBUG)
			Log.e(TAG, message);
	}
}
