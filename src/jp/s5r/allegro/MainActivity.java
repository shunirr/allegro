package jp.s5r.allegro;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import jp.s5r.allegro.models.ApkInfo;
import jp.s5r.allegro.task.DownloadApkTask;
import jp.s5r.allegro.task.DownloadListTask;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {
  private final static String APK_PATH =
      Environment.getExternalStorageDirectory() + "/hoge.apk";

  private Handler mHandler = new Handler();
  private AppListAdapter mAdapter;

  private URI mUri;
  private AlertDialog mJsonUriDialog;
  private EditText mUriEditText;

  private SharedPreferences mPreferences;

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

    setupPreferences();
    setupDialog();

    mAdapter = new AppListAdapter(getApplicationContext(), new ArrayList<ApkInfo>());
    setListAdapter(mAdapter);

    ListView listView = (ListView) findViewById(android.R.id.list);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ApkInfo info = (ApkInfo) mAdapter.getItem(position);
        new DownloadApkTask(MainActivity.this).execute(info.getUri());
      }
    });

    /*
    * market.json
    * [
    *   {"title": "MyApp Ver1", "uri": "http://example.com/my-app-v1.apk"},
    *   {"title": "MyApp Ver2", "uri": "http://example.com/my-app-v2.apk"},
    *   {"title": "MyApp Ver3", "uri": "http://example.com/my-app-v3.apk"}
    * ]
    */

    if (mUri != null) {
      new DownloadListTask().execute(mUri);
    } else {
      mJsonUriDialog.show();
    }
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
      long lastModified = mPreferences.getLong(info.getUri().toString(), 0);
      if (lastModified < info.getLastModified().getTime()) {
        holder.tvStatus.setText("new!");
        mPreferences.edit().putLong(info.getUri().toString(), info.getLastModified().getTime()).commit();
      } else {
        holder.tvStatus.setText("");
      }

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      holder.tvInfo.setText(getReadableBytes(info.getFileSize()) + ", " + sdf.format(info.getLastModified()));

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
}