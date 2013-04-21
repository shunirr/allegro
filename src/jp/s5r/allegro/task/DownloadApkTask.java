package jp.s5r.allegro.task;

import android.app.ProgressDialog;
import android.content.Context;
import jp.s5r.allegro.utils.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class DownloadApkTask extends DownloadTask<File> {

  private ProgressDialog mProgressDialog;

  public DownloadApkTask(Context context, ProgressDialog progressDialog) {
    super(context);
    mProgressDialog = progressDialog;
  }

  @Override
  protected File doInBackground(URI... uris) {
    File file = new File("/sdcard/hoge.apk");
    try {
      downloadBinary(uris[0], file);
    } catch (IOException e) {
      toast("Exception:" + e.getMessage());
      Log.e(e);
    }
    return file;
  }

  @Override
  protected void progress(int value) {
    publishProgress(value);
  }

  @Override
  protected void onProgressUpdate(Integer... progress) {
    mProgressDialog.incrementProgressBy(progress[0]);
  }

  @Override
  protected void onPostExecute(File file) {
    mProgressDialog.dismiss();
//    if (!isCancelled() && file != null) {
//      Intent intent = new Intent(Intent.ACTION_VIEW);
//      intent.setDataAndType(Uri.fromFile(new File(APK_PATH)), "application/vnd.android.package-archive");
//      startActivity(intent);
//    }
  }
}

