package jp.s5r.allegro.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;
import jp.s5r.allegro.utils.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public abstract class DownloadTask<T> extends AsyncTask<URI, Integer, T> {
  protected static final int BUFFER_SIZE = 1024;

  protected Context mContext;
  protected Handler mHandler;

  public DownloadTask(Context context) {
    mContext = context;
    mHandler = new Handler();
  }

  @Override
  protected T doInBackground(URI... uris) {
    return null;
  }

  protected abstract void progress(int value);

  protected String downloadText(URI targetUri) throws IOException {
    String body = null;
    HttpGet method = new HttpGet(targetUri);
    HttpClient client = null;
    try {
      client = createHttpClient();
      HttpResponse response = client.execute(method);

      int statusCode = response.getStatusLine().getStatusCode();

      Log.d("StatusCode: " + statusCode);
      if (statusCode == HttpStatus.SC_OK) {
        body = getResponseBody(response);
      }
    } finally {
      destroyHttpClient(client);
    }

    return body;
  }

  protected boolean downloadBinary(URI targetUri, File outFile)
      throws IOException {
    return downloadBinary(targetUri, outFile, null, null);
  }

  protected boolean downloadBinary(URI targetUri, File outFile,
                                   String username, String password)
      throws IOException {
    File file = new File("/sdcard/hoge.apk");
    HttpGet method = new HttpGet(targetUri);
    DefaultHttpClient client = null;

    try {
      client = createHttpClient();

      if (username != null && username.equals("") &&
          password != null && password.equals("")) {
        UsernamePasswordCredentials credentials =
            new UsernamePasswordCredentials(username, password);
        AuthScope scope = new AuthScope(method.getURI().getHost(),
                                        method.getURI().getPort());
        client.getCredentialsProvider().setCredentials(scope, credentials);
      }

      HttpResponse response = client.execute(method);

      Log.d("StatusCode: " + response.getStatusLine().getStatusCode());
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        if (outFile.exists()) {
          outFile.delete();
        }
        outFile.createNewFile();

        BufferedInputStream bis =
            new BufferedInputStream(response.getEntity().getContent(), BUFFER_SIZE);
        BufferedOutputStream bos =
            new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);

        long contentLength = response.getEntity().getContentLength();

        try {
          byte buffer[] = new byte[BUFFER_SIZE];
          int size;
          while ((size = bis.read(buffer)) > 0) {
            bos.write(buffer, 0, size);
            progress((int)(size / contentLength * 100));

            if (isCancelled()) {
              break;
            }
          }

          bos.flush();
        } finally {
          bos.close();
          bis.close();
        }
      }
    } finally {
      destroyHttpClient(client);
    }

    return true;
  }

  protected DefaultHttpClient createHttpClient() {
    HttpParams params = new BasicHttpParams();
    HttpConnectionParams.setSocketBufferSize(params, 1024 * 4);
    HttpConnectionParams.setConnectionTimeout(params, 1000 * 20);
    HttpConnectionParams.setSoTimeout(params, 1000 * 20);

    return new DefaultHttpClient(params);
  }

  protected void destroyHttpClient(HttpClient httpClient) {
    if (httpClient != null) {
      httpClient.getConnectionManager().shutdown();
    }
  }

  protected String getResponseBody(HttpResponse response) throws IOException {
    InputStream       is  = null;
    InputStreamReader isr = null;
    BufferedReader    br  = null;

    String responseBody = null;
    try {
      is  = response.getEntity().getContent();
      isr = new InputStreamReader(is);
      br  = new BufferedReader(isr);

      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      responseBody = sb.toString();

    } finally {
      if (br != null) {
        br.close();
      }
      if (isr != null) {
        isr.close();
      }
      if (is != null) {
        is.close();
      }
    }

    return responseBody;
  }

  protected void toast(final String message) {
    mHandler.post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(
            mContext.getApplicationContext(),
            message,
            Toast.LENGTH_SHORT
        ).show();
      }
    });
  }
}
