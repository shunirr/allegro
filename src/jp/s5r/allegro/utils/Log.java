package jp.s5r.allegro.utils;

import java.util.Date;

public class Log {
  public static final String  TAG_NAME   = "Allegro";
  public static final boolean ENABLE_LOG = true;

  protected Log() {}

  public static void d(String message) {
    d(TAG_NAME, message);
  }

  public static void d(String tag, String message) {
    if (!ENABLE_LOG) {
      return;
    }
    if (android.util.Log.isLoggable(tag, android.util.Log.DEBUG)) {
      dTime(tag, message);
    }
  }

  public static void d(Class<?> clazz, String method) {
    d(TAG_NAME, clazz, method);
  }

  public static void d(String tag, Class<?> clazz, String method) {
    if (!ENABLE_LOG) {
      return;
    }
    if (android.util.Log.isLoggable(tag, android.util.Log.DEBUG)) {
      dTime(tag, clazz.getSimpleName() + "#" + method);
    }
  }

  public static void d(String variableName, boolean value) {
    d(TAG_NAME, variableName, value);
  }

  public static void d(String tag, String variableName, boolean value) {
    if (!ENABLE_LOG) {
      return;
    }
    if (android.util.Log.isLoggable(tag, android.util.Log.DEBUG)) {
      dTime(tag, variableName + ":" + ((value) ? "true" : "false"));
    }
  }

  public static void v(String message) {
    v(TAG_NAME, message);
  }

  public static void v(String tag, String message) {
    if (!ENABLE_LOG) {
      return;
    }
    if (android.util.Log.isLoggable(tag, android.util.Log.VERBOSE)) {
      vTime(tag, message);
    }
  }

  public static void e(String message) {
    e(TAG_NAME, message);
  }

  public static void e(String tag, String message) {
    if (!ENABLE_LOG) {
      return;
    }
    if (android.util.Log.isLoggable(tag, android.util.Log.ERROR)) {
      eTime(tag, message);
    }
  }

  public static void e(Class<?> clazz, String method, String message) {
    e(TAG_NAME, clazz, method, message);
  }

  public static void e(String tag, Class<?> clazz, String method, String message) {
    if (!ENABLE_LOG) {
      return;
    }
    if (android.util.Log.isLoggable(tag, android.util.Log.DEBUG)) {
      eTime(tag, clazz.getSimpleName() + "#" + method + ": " + message);
    }
  }

  public static void e(Throwable t) {
    e(TAG_NAME, t);
  }

  public static void e(String tag, Throwable t) {
    if (!ENABLE_LOG) {
      return;
    }
    if (android.util.Log.isLoggable(tag, android.util.Log.ERROR)) {
      eTime(tag, "", t);
    }
  }

  public static void i(String message) {
    i(TAG_NAME, message);
  }

  public static void i(String tag, String message) {
    if (!ENABLE_LOG) {
      return;
    }
    if (android.util.Log.isLoggable(tag, android.util.Log.INFO)) {
      iTime(tag, message);
    }
  }

  public static void i(Class<?> clazz, String method) {
    i(TAG_NAME, clazz, method);
  }

  public static void i(String tag, Class<?> clazz, String method) {
    if (!ENABLE_LOG) {
      return;
    }
    if (android.util.Log.isLoggable(tag, android.util.Log.DEBUG)) {
      iTime(tag, clazz.getSimpleName() + "#" + method);
    }
  }

  public static void w(String message) {
    w(TAG_NAME, message);
  }

  public static void w(String tag, String message) {
    if (!ENABLE_LOG) {
      return;
    }
    if (android.util.Log.isLoggable(tag, android.util.Log.WARN)) {
      wTime(tag, message);
    }
  }

  private static void dTime(String tag, String message) {
    Date d = new Date();
    android.util.Log.d(tag, "[" + d.toGMTString() + "] " + message);
  }

  private static void vTime(String tag, String message) {
    Date d = new Date();
    android.util.Log.v(tag, "[" + d.toGMTString() + "] " + message);
  }

  private static void eTime(String tag, String message) {
    Date d = new Date();
    android.util.Log.e(tag, "[" + d.toGMTString() + "] " + message);
  }

  private static void eTime(String tag, String message, Throwable t) {
    Date d = new Date();
    android.util.Log.e(tag, "[" + d.toGMTString() + "] " + message, t);
  }

  private static void iTime(String tag, String message) {
    Date d = new Date();
    android.util.Log.i(tag, "[" + d.toGMTString() + "] " + message);
  }

  private static void wTime(String tag, String message) {
    Date d = new Date();
    android.util.Log.v(tag, "[" + d.toGMTString() + "] " + message);
  }
}
