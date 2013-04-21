package jp.s5r.allegro.activities;

import android.app.Activity;
import android.os.Bundle;
import jp.s5r.allegro.utils.Log;

public class BaseActivity extends Activity {
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(getClass(),"#onCreate(Bundle)");
    super.onCreate(savedInstanceState);
  }

  protected void onStart() {
    Log.i(getClass(), "#onStart()");
    super.onStart();
  }

  protected void onRestart() {
    Log.i(getClass(), "#onRestart()");
    super.onRestart();
  }

  protected void onResume() {
    Log.i(getClass(), "#onResume()");
    super.onResume();
  }

  protected void onPause() {
    Log.i(getClass(), "#onPause()");
    super.onPause();
  }

  protected void onStop() {
    Log.i(getClass(), "#onStop()");
    super.onStop();
  }

  protected void onDestroy() {
    Log.i(getClass(), "#onDestroy()");
    super.onDestroy();
  }

  @Override
  public void finish() {
    if (!isFinishing()) {
      super.finish();
    }
  }
}