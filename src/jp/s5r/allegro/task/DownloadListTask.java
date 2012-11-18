package jp.s5r.allegro.task;

import android.content.Context;
import jp.s5r.allegro.utils.Log;

import java.io.IOException;
import java.net.URI;

public class DownloadListTask extends DownloadTask<String> {
  public DownloadListTask(Context context) {
    super(context);
  }

  @Override
  protected String doInBackground(URI... uris) {
    try {
      return downloadText(uris[0]);
    } catch (IOException e) {
      toast("Exception:" + e.getMessage());
      Log.e(e);
    }

    return null;
  }

  @Override
  protected void progress(int value) {
  }

  @Override
  protected void onPostExecute(String body) {
//    mAdapter.clear();
//
//    try {
//      JSONArray json = new JSONArray();
//      if (body != null) {
//        json = new JSONArray(body);
//      }
//      for (int i = 0; i < json.length(); i++) {
//        JSONObject j = json.getJSONObject(i);
//        String title = "";
//        if (j.has("title")) {
//          title = j.getString("title");
//        }
//
//        URI uri = null;
//        if (j.has("uri")) {
//          uri = URI.create(j.getString("uri"));
//        }
//
//        Date lastModified = null;
//        if (j.has("last_modified")) {
//          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZ");
//          try {
//            lastModified = sdf.parse(j.getString("last_modified"));
//          } catch (ParseException e) {
//          }
//        }
//
//        long fileSize = 0;
//        if (j.has("size")) {
//          fileSize = j.getLong("size");
//        }
//
//        mAdapter.add(new ApkInfo(title, uri, fileSize, lastModified));
//      }
//    } catch (JSONException e) {
//      toast(e.getClass().getSimpleName());
//    }
//
//    mAdapter.notifyDataSetChanged();
  }
}

