package jp.s5r.allegro.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.net.URI;

public final class PreferenceUtils {
  private PreferenceUtils() {
    throw new RuntimeException();
  }

  private static SharedPreferences getPreferences(final Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  public static Uri getJsonUri(final Context context) {
    String uriStr = getPreferences(context).getString("uri", null);
    if (uriStr != null) {
      return Uri.parse(uriStr);
    }

    return null;
  }

  public static void setJsonUri(final Context context, final URI jsonUri) {
    setJsonUri(context, jsonUri.toString());
  }

  public static void setJsonUri(final Context context, final Uri jsonUri) {
    setJsonUri(context, jsonUri.toString());
  }

  public static void setJsonUri(final Context context, final String jsonUri) {
    SharedPreferences.Editor editor = getPreferences(context).edit();
    editor.putString("uri", jsonUri);
    editor.commit();
  }
}

