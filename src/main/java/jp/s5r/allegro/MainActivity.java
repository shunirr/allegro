package jp.s5r.allegro;

import com.androidquery.AQuery;
import jp.s5r.allegro.models.ApkInfo;
import jp.s5r.allegro.models.ApkInfoGenerated;
import net.vvakame.util.jsonpullparser.JsonFormatException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {
  private final static String APK_PATH = Environment.getExternalStorageDirectory() + "/hoge.apk";

  private Handler mHandler = new Handler();
  private AppListAdapter mAdapter;

  private URI mUri;
  private AlertDialog mJsonUriDialog;
  private EditText mUriEditText;

  private SharedPreferences mPreferences;

  private AQuery mAq;

  private void setupDialog() {
    mUriEditText = new EditText(this);
    if (mUri != null) {
      mUriEditText.setText(mUri.toString());
    }

    mJsonUriDialog = new AlertDialog.Builder(this)
        .setTitle("Set JSON-List URI")
        .setView(mUriEditText)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            try {
              mUri = URI.create(mUriEditText.getText().toString());
              mPreferences.edit().putString("uri", mUri.toString()).commit();
              new DownloadListTask().execute(mUri);
            } catch (NullPointerException e) {
              Toast.makeText(MainActivity.this, "URI is null", Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException e) {
              Toast.makeText(MainActivity.this, "Invalid URI", Toast.LENGTH_SHORT).show();
            }
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
          }
        })
        .create();
  }

  private void setupPreferences() {
    String defaultUri = null;
    String defaultUsername = null;
    String defaultPassword = null;
    try {
      Field fUri = jp.s5r.allegro.R.string.class.getField("uri");
      int idUri = fUri.getInt(jp.s5r.allegro.R.string.class);
      defaultUri = getString(idUri);

      Field fUsername = jp.s5r.allegro.R.string.class.getField("username");
      int idUsername = fUsername.getInt(jp.s5r.allegro.R.string.class);
      defaultUsername = getString(idUsername);

      Field fPassword = jp.s5r.allegro.R.string.class.getField("password");
      int idPassword = fPassword.getInt(jp.s5r.allegro.R.string.class);
      defaultPassword = getString(idPassword);
    } catch (NoSuchFieldException e) {
    } catch (IllegalAccessException e) {
    }

    mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    try {
      String uriStr = mPreferences.getString("uri", null);
      if (uriStr == null) {
        uriStr = defaultUri;
      }
      mUri = URI.create(uriStr);

      String usernameStr = mPreferences.getString("username", null);
      if (usernameStr == null) {
        usernameStr = defaultUsername;
      }

      String passwordStr = mPreferences.getString("password", null);
      if (passwordStr == null) {
        passwordStr = defaultPassword;
      }

      mPreferences.edit().putString("uri", mUri.toString()).commit();
      mPreferences.edit().putString("username", usernameStr).commit();
      mPreferences.edit().putString("password", passwordStr).commit();
    } catch (NullPointerException e) {
      mUri = null;
    } catch (IllegalArgumentException e) {
      mUri = null;
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mAq = new AQuery(this);

    setupPreferences();
    setupDialog();

    mAdapter = new AppListAdapter(getApplicationContext(), new ArrayList<ApkInfo>());
    setListAdapter(mAdapter);

    mAq.id(android.R.id.list).itemClicked(this, "listItemClicked");

    if (mUri != null) {
      new DownloadListTask().execute(mUri);
    } else {
      mJsonUriDialog.show();
    }
  }

  @SuppressWarnings("unused")
  public void listItemClicked(final AdapterView<?> parent,
                              final View view,
                              final int position,
                              final long id) {
    ApkInfo info = (ApkInfo) mAdapter.getItem(position);
    new DownloadApkTask(MainActivity.this).execute(URI.create(info.getUri()));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean ret = super.onCreateOptionsMenu(menu);
    menu.add(0 , Menu.FIRST , Menu.NONE , "Set URI");
    return ret;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    mJsonUriDialog.show();
    return super.onOptionsItemSelected(item);
  }

  private DefaultHttpClient createHttpClient() {
    HttpParams params = new BasicHttpParams();
    HttpConnectionParams.setSocketBufferSize(params, 1024 * 4);
    HttpConnectionParams.setConnectionTimeout(params, 1000 * 20);
    HttpConnectionParams.setSoTimeout(params, 1000 * 20);

    return new DefaultHttpClient(params);
  }

  private void destroyHttpClient(HttpClient httpClient) {
    if (httpClient != null) {
      httpClient.getConnectionManager().shutdown();
    }
  }

  private String getResponseBody(HttpResponse response) throws IOException {
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

  private void toast(final String message) {
    mHandler.post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
      }
    });
  }

  class AppListAdapter extends BaseAdapter {
    private List<ApkInfo> mApkList;
    private LayoutInflater mInflater;

    public AppListAdapter(Context context, List<ApkInfo> apkList) {
      super();
      mApkList = apkList;
      mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
      return mApkList.size();
    }

    @Override
    public Object getItem(int i) {
      return mApkList.get(i);
    }

    @Override
    public long getItemId(int i) {
      return i;
    }

    public void add(ApkInfo info) {
      mApkList.add(info);
    }

    public void clear() {
      mApkList.clear();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      ApkInfo info = (ApkInfo) getItem(i);
      ViewHolder holder = null;
      if (view == null) {
        view = mInflater.inflate(R.layout.list_item, null);
        holder = new ViewHolder();
        holder.tvAppName = (TextView) view.findViewById(R.id.tv_appname);
        holder.tvStatus = (TextView) view.findViewById(R.id.tv_status);
        holder.tvInfo  = (TextView) view.findViewById(R.id.tv_info);
        view.setTag(holder);
      } else {
        holder = (ViewHolder) view.getTag();
      }

      holder.tvAppName.setText(info.getTitle());

      // 前回取得したパッケージの最終更新日
      long lastModified = mPreferences.getLong(info.getUri(), 0);
      if (lastModified < info.getLastModified().getTime()) {
        holder.tvStatus.setText("new!");
        mPreferences.edit().putLong(info.getUri(), info.getLastModified().getTime()).commit();
      } else {
        holder.tvStatus.setText("");
      }

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      holder.tvInfo.setText(getReadableBytes(info.getSize()) + ", " + sdf.format(info.getLastModified()));

      return view;
    }

    private class ViewHolder {
      TextView tvStatus;
      TextView tvAppName;
      TextView tvInfo;
    }
  }

  private static String getReadableBytes(long bytes) {
    final String[] UNIT = {
        "B", "KB", "MB", "GB"
    };

    String readableBytes = bytes + UNIT[0];
    long baseNumber = 1024;
    for (int i = 1; i < UNIT.length; i++) {
      double readableNum = bytes / baseNumber;
      if (readableNum < 1024) {
        readableBytes = readableNum + UNIT[i];
        break;
      }
      baseNumber *= 1024;
    }

    return readableBytes;
  }

  class DownloadListTask extends AsyncTask<URI, Integer, String> {
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
      if (body == null || body.equals("")) {
        Toast.makeText(getApplicationContext(),
            "Json is null.",
            Toast.LENGTH_SHORT)
            .show();
        return;
      }

      mAdapter.clear();

      List<ApkInfo> apkInfoList = null;
      try {
        apkInfoList = ApkInfoGenerated.getList(body);
      } catch (IOException e) {
        e.printStackTrace();
      } catch (JsonFormatException e) {
        e.printStackTrace();
      }

      if (apkInfoList == null) {
        Toast.makeText(getApplicationContext(),
                       "Json parse error.",
                       Toast.LENGTH_SHORT)
             .show();
        return;
      }

      for (ApkInfo info : apkInfoList) {
        mAdapter.add(info);
      }

      mAdapter.notifyDataSetChanged();
    }
  }

  class DownloadApkTask extends AsyncTask<URI, Integer, File> {
    private static final int BUFFER_SIZE = 1024;

    private ProgressDialog mProgressDialog;
    private Activity mActivity;

    public DownloadApkTask(Activity a) {
      mActivity = a;
    }

    @Override
    protected void onPreExecute() {
      mProgressDialog = new ProgressDialog(mActivity);
      mProgressDialog.setTitle("Downloading...");
      mProgressDialog.setIndeterminate(true);
      mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      mProgressDialog.show();
    }

    @Override
    protected File doInBackground(URI... uris) {
      File file = null;
      HttpGet method = new HttpGet(uris[0]);
      DefaultHttpClient client = null;

      String username = mPreferences.getString("username", null);
      String password = mPreferences.getString("password", null);

      try {
        client = createHttpClient();

        if (username != null && !username.equals("") && password != null && !password.equals("")) {
          UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
          AuthScope scope = new AuthScope(method.getURI().getHost(), method.getURI().getPort());
          client.getCredentialsProvider().setCredentials(scope, credentials);
        }

        HttpResponse response = client.execute(method);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
          mProgressDialog.setMax((int) response.getEntity().getContentLength());
          mProgressDialog.setIndeterminate(false);

          file = new File(APK_PATH);
          if (file.exists()) {
            file.delete();
          }
          file.createNewFile();

          BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent(), BUFFER_SIZE);
          BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
          try {
            byte buffer[] = new byte[BUFFER_SIZE];
            int size;
            while ((size = bis.read(buffer)) > 0) {
              bos.write(buffer, 0, size);
              publishProgress(size);

              if (isCancelled()) {
                break;
              }
            }

            bos.flush();
          } finally {
            bos.close();
            bis.close();
          }
        } else {
          toast("StatusCode: " + response.getStatusLine().getStatusCode());
        }
      } catch (ClientProtocolException e) {
        file = null;
        toast(e.getClass().getSimpleName());
      } catch (IOException e) {
        file = null;
        toast(e.getClass().getSimpleName());
      } finally {
        destroyHttpClient(client);
      }

      return file;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
      mProgressDialog.incrementProgressBy(progress[0]);
    }

    @Override
    protected void onPostExecute(File file) {
      mProgressDialog.dismiss();
      if (!isCancelled() && file != null) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(APK_PATH)), "application/vnd.android.package-archive");
        startActivity(intent);
      }
    }
  }
}