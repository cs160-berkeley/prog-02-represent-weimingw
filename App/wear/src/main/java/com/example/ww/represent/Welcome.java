package com.example.ww.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;


public class Welcome extends Activity implements SensorEventListener {

  private TextView mTextView;

  private SensorManager mSensorManager;
  private Sensor mSensor;

  static float of1 = Float.NaN;
  static float of2 = Float.NaN;
  static float of3 = Float.NaN;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);

    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    mSensorManager.registerListener(this,
      mSensor, SensorManager.SENSOR_DELAY_NORMAL);
  }

  public final void onSensorChanged(SensorEvent event)
  {
    // Use values from event.values array
    float nf1 = event.values[0];
    float nf2 = event.values[1];
    float nf3 = event.values[2];

    float totalDif = 0;

    if (Float.isNaN(of1)) {
      of1 = nf1;
      of2 = nf2;
      of3 = nf3;
    }

    if (nf1 != of1) {
      totalDif += Math.abs(of1 - nf1);
      of1 = nf1;
    }

    if (nf2 != of2) {
      totalDif += Math.abs(of2 - nf2);
      of2 = nf2;
    }

    if (nf3 != of3) {
      totalDif += Math.abs(of3 - nf3);
      of3 = nf3;
    }

    if (totalDif > 90) {
      //it shook
      Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
      sendIntent.putExtra(WatchToPhoneService.REQUEST_TYPE, WatchToPhoneService.GET_RANDOM);
      startService(sendIntent);
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) { }

  @Override
  protected void onPause()
  {
    super.onPause();
    mSensorManager.unregisterListener(this);
  }
}
