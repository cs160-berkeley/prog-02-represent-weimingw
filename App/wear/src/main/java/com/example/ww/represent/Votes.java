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

public class Votes extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votes);

        Intent thisIntent = getIntent();
        float obamaVotes = thisIntent.getFloatExtra("OBAMA", Float.NaN);
        float romneyVotes = thisIntent.getFloatExtra("ROMNEY", Float.NaN);
        String county = thisIntent.getStringExtra("COUNTY");

        TextView obamaText = (TextView) findViewById(R.id.textWearVotesObamaVotes);
        TextView romneyText = (TextView) findViewById(R.id.textWearVotesRomneyVotes);
        TextView countyText = (TextView) findViewById(R.id.textWearVotesCountyState);
        obamaText.setText(obamaVotes + "");
        romneyText.setText(romneyVotes + "");
        countyText.setText(county);

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

        if (Float.isNaN(Welcome.of1)) {
            Welcome.of1 = nf1;
            Welcome.of2 = nf2;
            Welcome.of3 = nf3;
        }

        if (nf1 != Welcome.of1) {
            totalDif += Math.abs(Welcome.of1 - nf1);
            Welcome.of1 = nf1;
        }

        if (nf2 != Welcome.of2) {
            totalDif += Math.abs(Welcome.of2 - nf2);
            Welcome.of2 = nf2;
        }

        if (nf3 != Welcome.of3) {
            totalDif += Math.abs(Welcome.of3 - nf3);
            Welcome.of3 = nf3;
        }

        if (totalDif > 90) {
            //it shook
            Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
            sendIntent.putExtra(WatchToPhoneService.REQUEST_TYPE, WatchToPhoneService.GET_RANDOM);
            startService(sendIntent);
            finish();
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
