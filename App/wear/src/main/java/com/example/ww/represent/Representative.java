package com.example.ww.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.animation.Animation.AnimationListener;
import static android.view.animation.Animation.RESTART;

public class Representative extends Activity implements SensorEventListener {

    private TextView mTextView;

    Runnable swipeIndicatorRunnable;
    Handler swipeIndicatorHandler;
    boolean indicatorsVisible;

    static String PARTY_KEY = "REP_PARTY";
    static String NAME_KEY = "REP_NAME";
    static String INDEX_KEY = "INDEX";
    static String PIC_KEY = "PICTURE";

    static int index = -1;

    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;
    float minMovement = 80;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Intent thisIntent = getIntent();

        index = thisIntent.getIntExtra(INDEX_KEY, -1);

        TextView nameView = (TextView) findViewById(R.id.textWearRepresentativeName);
        nameView.setText(thisIntent.getStringExtra(NAME_KEY));

        TextView partyView = (TextView) findViewById(R.id.textWearRepresentativeParty);
        String party = thisIntent.getStringExtra(PARTY_KEY);
        partyView.setText(party);

        ImageView picView = (ImageView) findViewById(R.id.imgWearRepresentativePic);
        Bitmap pic = (Bitmap) thisIntent.getParcelableExtra(PIC_KEY);
        picView.setImageBitmap(pic);

        ImageView iconView = (ImageView) findViewById(R.id.imgWearRepresentativeParty);
        if (party != null) {
            if (party.equals("(D)"))
            {
                iconView.setImageResource(R.drawable.donkey);
            }
            else if (party.equals("(R)"))
            {
                iconView.setImageResource(R.drawable.elephant);
            }
            else {
                iconView.setImageResource(R.drawable.flag);
            }
        }

        swipeIndicatorRunnable = new Runnable() {
            @Override
            public void run() {

                ImageView img = (ImageView) findViewById(R.id.imgWearRepresentativeDownswipe);
                fadeOut(img);

                img = (ImageView) findViewById(R.id.imgWearRepresentativeLeftswipe);
                fadeOut(img);

                indicatorsVisible = false;
            }
        };

        swipeIndicatorHandler = new Handler();
        swipeIndicatorHandler.postDelayed(swipeIndicatorRunnable, 0);

        LinearLayout ll = (LinearLayout) findViewById(R.id.layoutWearRepresentative);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!indicatorsVisible) {
                    ImageView img = (ImageView) findViewById(R.id.imgWearRepresentativeDownswipe);
                    //fadeIn(img);
                    moveIndicatorDown(img);

                    img = (ImageView) findViewById(R.id.imgWearRepresentativeLeftswipe);
                    moveIndicatorLeft(img);
                }

                swipeIndicatorHandler.removeCallbacks(swipeIndicatorRunnable);
                swipeIndicatorHandler.postDelayed(swipeIndicatorRunnable, 3000);
                indicatorsVisible = true;
            }
        });

        final Activity thisActivity = this;
        ll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    // when user first touches the screen we get x and y coordinate
                    case MotionEvent.ACTION_DOWN: {
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        x2 = event.getX();
                        y2 = event.getY();

                        //if left to right sweep event on screen
                        if (x1 < x2 && x2 - x1 > minMovement) {
                            //this is usually like the back button...
                            Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                            sendIntent.putExtra(WatchToPhoneService.REQUEST_TYPE, WatchToPhoneService.GET_REP);
                            sendIntent.putExtra(WatchToPhoneService.INDEX, index - 1);
                            startService(sendIntent);
                        }
                        // if right to left sweep event on screen
                        if (x1 > x2 && x1 - x2 > minMovement) {
                            Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                            sendIntent.putExtra(WatchToPhoneService.REQUEST_TYPE, WatchToPhoneService.GET_REP);
                            sendIntent.putExtra(WatchToPhoneService.INDEX, index + 1);
                            startService(sendIntent);
                        }
                        // if UP to Down sweep event on screen
                        if (y1 < y2 && y2 - y1 > minMovement) {
                            Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                            sendIntent.putExtra(WatchToPhoneService.REQUEST_TYPE, WatchToPhoneService.GET_VOTE);
                            startService(sendIntent);
                        }
                        //if Down to UP sweep event on screen
                        //if (y1 > y2 && y1 - y2 > minMovement) { }
                        if (Math.abs(x1 - x2) < minMovement && Math.abs(y1 - y2) < minMovement) {
                            Intent getDetailsIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                            getDetailsIntent.putExtra(WatchToPhoneService.REQUEST_TYPE, WatchToPhoneService.DETAILS);
                            getDetailsIntent.putExtra(WatchToPhoneService.INDEX, index);
                            startService(getDetailsIntent);
                        }
                        break;
                    }
                }
                return false;
            }
        });
    }

    private void fadeIn(final ImageView img)
    {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(500);

        anim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                img.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        img.startAnimation(anim);
    }

    private void fadeOut(final ImageView img)
    {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(500);

        anim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                img.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        img.startAnimation(anim);
    }

    private void moveIndicatorDown(final ImageView img)
    {
        TranslateAnimation anim = new TranslateAnimation(0, 0, -48, 0);
        //anim.setInterpolator(new AccelerateInterpolator());
        anim.setRepeatCount(2);
        anim.setRepeatMode(RESTART);
        anim.setFillAfter(false);
        anim.setDuration(1000);

        anim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fadeOut(img);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        img.startAnimation(anim);
    }

    private void moveIndicatorLeft(final ImageView img)
    {
        TranslateAnimation anim = new TranslateAnimation(48, 0, 0, 0);
        //anim.setInterpolator(new AccelerateInterpolator());
        anim.setRepeatCount(2);
        anim.setRepeatMode(RESTART);
        anim.setFillAfter(false);
        anim.setDuration(1000);

        anim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fadeOut(img);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        img.startAnimation(anim);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    protected void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(this,
          mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override

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
        }
    }

    @Override

    protected void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
