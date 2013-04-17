package jp.s5r.allegro.tasks;

import android.content.Context;
import jp.s5r.allegro.models.ApkInfo;
import jp.s5r.allegro.utils.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DownloadListTask extends DownloadTask<String> {
  private DownloadListListener mListener;

  public DownloadListTask(Context context) {
    super(context);
  }

  public DownloadListTask setListener(DownloadListListener listener) {
    mListener = listener;
    return this;
  }

  @Override
  protected String doInBackground(URI... uris) {
    Log.i(getClass(), "doInBackground(URI...)");

    URI uri = null;
    if (uris != null && uris.length > 0) {
      uri = uris[0];
    }

    if (uri != null) {
      try {
        return downloadText(uri);
      } catch (IOException e) {
        toast("Exception:" + e.getMessage());
        Log.e(e);
      }
    }

    return null;
  }

  @Override
  protected void progress(int value) {
  }

  @Override
  protected void onPostExecute(String body) {
    Log.i(getClass(), "onPostExecute(String)");

    ArrayList<ApkInfo> apkInfoList = new ArrayList<ApkInfo>();

    try {
      JSONArray json = new JSONArray();
      if (body != null) {
        json = new JSONArray(body);
      }
      for (int i = 0; i < json.length(); i++) {
        JSONObject j = json.getJSONObject(i);
        String title = "";
        if (j.has("title")) {
          title = j.getString("title");
        }

        URI uri = null;
        if (j.has("uri")) {
          uri = URI.create(j.getString("uri"));
        }

        Date lastModified = null;
        if (j.has("last_modified")) {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZ");
          try {
            lastModified = sdf.parse(j.getString("last_modified"));
          } catch (ParseException e) {
          }
        }

        long fileSize = 0;
        if (j.has("size")) {
          fileSize = j.getLong("size");
        }

        apkInfoList.add(new ApkInfo(title, uri, fileSize, lastModified));
      }
    } catch (JSONException e) {
      toast(e.getClass().getSimpleName());
    }

    if (mListener != null) {
      mListener.onSuccess(apkInfoList);
    }
  }

  public interface DownloadListListener {
    void onSuccess(List<ApkInfo> apkInfoList);
  }
}

