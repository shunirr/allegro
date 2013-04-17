package jp.s5r.allegro.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import jp.s5r.allegro.utils.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class DownloadApkTask extends DownloadTask<File> {
  private ProgressDialog mProgressDialog;
  private DownloadApkListener mListener;

  public DownloadApkTask(Context context, ProgressDialog progressDialog) {
    super(context);
    mProgressDialog = progressDialog;
  }

  public DownloadApkTask setListener(DownloadApkListener listener) {
    mListener = listener;
    return this;
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

    if (mListener != null) {
      mListener.onSuccess(file);
    }
  }

  public interface DownloadApkListener {
    void onSuccess(File file);
  }
}

