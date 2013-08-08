package com.tapit.adview;

import android.util.Log;

public class TILog {
  public static final String TAG = "TapIt";

  public static void d(String message) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, message);
    }
  }
  public static void d(String message, Throwable throwable) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, message, throwable);
    }
  }

  public static void i(String message) {
    if (BuildConfig.DEBUG) {
      Log.i(TAG, message);
    }
  }
  public static void i(String message, Throwable throwable) {
    if (BuildConfig.DEBUG) {
      Log.i(TAG, message, throwable);
    }
  }

  public static void v(String message) {
    if (BuildConfig.DEBUG) {
      Log.v(TAG, message);
    }
  }
  public static void v(String message, Throwable throwable) {
    if (BuildConfig.DEBUG) {
      Log.v(TAG, message, throwable);
    }
  }

  public static void w(String message) {
    Log.w(TAG, message);
  }
  public static void w(String message, Throwable throwable) {
    Log.w(TAG, message, throwable);
  }

  public static void e(String message) {
    Log.e(TAG, message);
  }
  public static void e(String message, Throwable throwable) {
    Log.e(TAG, message, throwable);
  }
}
