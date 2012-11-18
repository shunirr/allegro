package jp.s5r.allegro.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

public class DownloadApkTask extends DownloadTask<File> {

  private ProgressDialog mProgressDialog;

  public DownloadApkTask(Context context, ProgressDialog progressDialog) {
    super(context);
    mProgressDialog = progressDialog;
  }

  @Override
  protected void onPreExecute() {
//    mProgressDialog = new ProgressDialog(mActivity);
//    mProgressDialog.setTitle("Downloading...");
//    mProgressDialog.setIndeterminate(true);
//    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//    mProgressDialog.show();
  }

  @Override
  protected File doInBackground(URI... uris) {
//    File file = null;
//    HttpGet method = new HttpGet(uris[0]);
//    DefaultHttpClient client = null;
//
//    String username = mPreferences.getString("username", null);
//    String password = mPreferences.getString("password", null);
//
//    try {
//      client = createHttpClient();
//
//      if (username != null && !username.equals("") && password != null && !password.equals("")) {
//        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
//        AuthScope scope = new AuthScope(method.getURI().getHost(), method.getURI().getPort());
//        client.getCredentialsProvider().setCredentials(scope, credentials);
//      }
//
//      HttpResponse response = client.execute(method);
//
//      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//        mProgressDialog.setMax((int) response.getEntity().getContentLength());
//        mProgressDialog.setIndeterminate(false);
//
//        file = new File(APK_PATH);
//        if (file.exists()) {
//          file.delete();
//        }
//        file.createNewFile();
//
//        BufferedInputStream  bis = new BufferedInputStream(response.getEntity().getContent(), BUFFER_SIZE);
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
//        try {
//          byte buffer[] = new byte[BUFFER_SIZE];
//          int size;
//          while ((size = bis.read(buffer)) > 0) {
//            bos.write(buffer, 0, size);
//            publishProgress(size);
//
//            if (isCancelled()) {
//              break;
//            }
//          }
//
//          bos.flush();
//        } finally {
//          bos.close();
//          bis.close();
//        }
//      } else {
//        toast("StatusCode: " + response.getStatusLine().getStatusCode());
//      }
//    } catch (ClientProtocolException e) {
//      file = null;
//      toast(e.getClass().getSimpleName());
//    } catch (IOException e) {
//      file = null;
//      toast(e.getClass().getSimpleName());
//    } finally {
//      destroyHttpClient(client);
//    }
//
//    return file;
    return null;
  }

  @Override
  protected void onProgressUpdate(Integer... progress) {
    mProgressDialog.incrementProgressBy(progress[0]);
  }

  @Override
  protected void onPostExecute(File file) {
//    mProgressDialog.dismiss();
//    if (!isCancelled() && file != null) {
//      Intent intent = new Intent(Intent.ACTION_VIEW);
//      intent.setDataAndType(Uri.fromFile(new File(APK_PATH)), "application/vnd.android.package-archive");
//      startActivity(intent);
//    }
  }
}

