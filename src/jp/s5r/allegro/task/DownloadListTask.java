package jp.s5r.allegro.task;

import android.content.Context;
import jp.s5r.allegro.models.ApkInfo;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadListTask extends DownloadTask<String> {
  public DownloadListTask(Context context) {
    super(context);
  }

  @Override
  protected String doInBackground(URI... uris) {
    String body = null;
    HttpGet method = new HttpGet(uris[0]);
    HttpClient client = null;
    try {
      client = createHttpClient();
      HttpResponse response = client.execute(method);

      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        body = getResponseBody(response);
      } else {
        toast("StatusCode: " + response.getStatusLine().getStatusCode());
      }
    } catch (ClientProtocolException e) {
      toast(e.getClass().getSimpleName());
    } catch (IOException e) {
      toast(e.getClass().getSimpleName());
    } finally {
      destroyHttpClient(client);
    }

    return body;
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

