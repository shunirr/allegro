package jp.s5r.android.market;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R;
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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.net.URI;

public class MainActivity extends ListActivity {
    private final static String APK_PATH = "/sdcard/hoge.apk";
    private Handler mHandler = new Handler();
    private ArrayAdapter<ApkInfo> mAdapter;

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

    private void setupListViews() {
        mAdapter = new ArrayAdapter<ApkInfo>(this, R.layout.simple_list_item_1);
        setListAdapter(mAdapter);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApkInfo info = mAdapter.getItem(position);
                new DownloadApkTask(MainActivity.this).execute(info.uri);
            }
        });
    }

    private void setupPreferences() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            mUri = URI.create(mPreferences.getString("uri", null));
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
        setupListViews();
        setupDialog();

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

    class ApkInfo {
        String title;
        URI uri;

        public ApkInfo(String title, URI uri) {
            this.title = title;
            this.uri = uri;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private void toast(final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
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
            try {
                JSONArray json = new JSONArray();
                if (body != null) {
                    json = new JSONArray(body);
                }
                for (int i = 0; i < json.length(); i++) {
                    JSONObject j = json.getJSONObject(i);
                    if (j.has("title") && j.has("uri")) {
                        String title = j.getString("title");
                        URI uri = URI.create(j.getString("uri"));

                        mAdapter.add(new ApkInfo(title, uri));
                    }
                }
            } catch (JSONException e) {
                toast(e.getClass().getSimpleName());
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    class DownloadApkTask extends AsyncTask<URI, Integer, File> {
        private static final int BUFFER_SIZE = 1024;

        private ProgressDialog mProgressDialog;
        private Context mContext;

        public DownloadApkTask(Context c) {
            mContext = c;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setTitle("Downloading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.show();
        }

        @Override
        protected File doInBackground(URI... uris) {
            File file = null;
            HttpGet method = new HttpGet(uris[0]);
            HttpClient client = null;
            try {
                client = createHttpClient();
                HttpResponse response = client.execute(method);

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    mProgressDialog.setMax((int) response.getEntity().getContentLength());

                    file = new File(APK_PATH);
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();

                    BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
                    BufferedInputStream bis = new BufferedInputStream(entity.getContent(), BUFFER_SIZE);
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
            mProgressDialog.setProgress(mProgressDialog.getProgress() + progress[0]);
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