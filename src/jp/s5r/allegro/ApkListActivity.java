package jp.s5r.allegro;

import android.app.ProgressDialog;
import android.preference.PreferenceManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import jp.s5r.allegro.activities.BaseActivity;
import jp.s5r.allegro.models.ApkInfo;
import jp.s5r.allegro.task.DownloadApkTask;
import jp.s5r.allegro.task.DownloadListTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import jp.s5r.allegro.utils.ByteSize;
import jp.s5r.allegro.utils.Log;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ApkListActivity extends BaseActivity {
  private ApkListAdapter mAdapter;

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
              mPreferences.edit()
                          .putString("uri", mUri.toString())
                          .commit();
              new DownloadListTask(getApplicationContext())
                  .setListener(mDownloadListListener)
                  .execute(mUri);
            } catch (NullPointerException e) {
              Toast.makeText(
                  ApkListActivity.this,
                  "URI is null",
                  Toast.LENGTH_SHORT
              ).show();
            } catch (IllegalArgumentException e) {
              Toast.makeText(
                  ApkListActivity.this,
                  "Invalid URI",
                  Toast.LENGTH_SHORT
              ).show();
            }
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
          }
        })
        .create();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.apk_list_layout);

    mPreferences =
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    String uriStr = mPreferences.getString("uri", null);
    if (uriStr != null) {
      mUri = URI.create(uriStr);
    }

    setupDialog();

    mAdapter = new ApkListAdapter(getApplicationContext(),
                                  new ArrayList<ApkInfo>());

    ListView listView = (ListView) findViewById(R.id.app_list);
    listView.setAdapter(mAdapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProgressDialog dialog = new ProgressDialog(ApkListActivity.this);
        dialog.setTitle("Downloading ...");
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();

        ApkInfo info = (ApkInfo) mAdapter.getItem(position);
        new DownloadApkTask(
            getApplicationContext(),
            dialog
        ).execute(info.getUri());
      }
    });

    if (mUri != null) {
      new DownloadListTask(getApplicationContext())
          .setListener(mDownloadListListener)
          .execute(mUri);
    } else {
      mJsonUriDialog.show();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean ret = super.onCreateOptionsMenu(menu);
    menu.add(0, Menu.FIRST, Menu.NONE, "Set URI");
    return ret;
  }

  private DownloadListTask.DownloadListListener mDownloadListListener =
      new DownloadListTask.DownloadListListener() {
        @Override
        public void onSuccess(List<ApkInfo> apkInfoList) {
          Log.i(ApkListActivity.class, "onSuccess(List<ApkInfo>)");
          Log.d("apk count: " + apkInfoList.size());
          mAdapter.clear();
          mAdapter.add(apkInfoList);
          mAdapter.notifyDataSetChanged();
        }
      };

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    mJsonUriDialog.show();
    return super.onOptionsItemSelected(item);
  }

  class ApkListAdapter extends ArrayAdapter {
    private List<ApkInfo> mApkList;
    private LayoutInflater mInflater;

    public ApkListAdapter(Context context, List<ApkInfo> apkList) {
      super(context, 0);
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

    public void add(List<ApkInfo> infoList) {
      for (ApkInfo info : infoList) {
        add(info);
      }
    }

    public void clear() {
      mApkList.clear();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      ApkInfo info = (ApkInfo) getItem(i);
      ViewHolder holder;
      if (view == null) {
        view = mInflater.inflate(R.layout.list_item, null);
        holder = new ViewHolder();
        holder.tvAppName = (TextView) view.findViewById(R.id.tv_appname);
        holder.tvStatus  = (TextView) view.findViewById(R.id.tv_status);
        holder.tvInfo    = (TextView) view.findViewById(R.id.tv_info);
        view.setTag(holder);
      } else {
        holder = (ViewHolder) view.getTag();
      }

      holder.tvAppName.setText(info.getTitle());

      // 前回取得したパッケージの最終更新日
      long lastModified = mPreferences.getLong(info.getUri().toString(), 0);
      if (lastModified < info.getLastModified().getTime()) {
        holder.tvStatus.setText("new!");
        mPreferences.edit().putLong(
            info.getUri().toString(),
            info.getLastModified().getTime()
        ).commit();
      } else {
        holder.tvStatus.setText("");
      }

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      holder.tvInfo.setText(
          new ByteSize(info.getFileSize()).toString() +
          ", " +
          sdf.format(info.getLastModified()));

      return view;
    }

    private class ViewHolder {
      TextView tvStatus;
      TextView tvAppName;
      TextView tvInfo;
    }
  }
}