package com.example.ww.represent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

public class SearchActivity
        extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    RelativeLayout results;
    Button useCurrentLocation;
    Button submit;
    Button toRepList;
    EditText zip;
    Context currentContext;
    static Context appContext;

    static LinkedList<Representative> reps;

    GoogleApiClient mGoogleApiClient;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        currentContext = this;
        appContext = this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        results = (RelativeLayout) findViewById(R.id.layoutMobileSearchResults);
        results.setVisibility(View.INVISIBLE);

        useCurrentLocation = (Button) findViewById(R.id.btnMobileSearchCurrentLocation);
        useCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                //call GeocodingAPICaller.execute on lastLocation
                String zip = null;
                try {
                    if (lastLocation == null) {
                        new AlertDialog.Builder(currentContext).setMessage("Could not get current location. Maybe location services are disabled?").show();
                    }
                    else {
                        zip = new GeocodeForZipCaller().execute(lastLocation.getLatitude(), lastLocation.getLongitude()).get();
                        searchZIP(zip);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        zip = (EditText) findViewById(R.id.editMobileSearchZIP);

        submit = (Button) findViewById(R.id.btnMobileSearchSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String str = zip.getText().toString();
            if (str.length() != 5) {
                new AlertDialog.Builder(currentContext).setMessage("Invalid US ZIP code. Please put in 5 numerical digits.").show();
            }
            else {
                searchZIP(str);
            }
            }
        });

        toRepList = (Button) findViewById(R.id.btnMobileSearchMoreInfo);
        toRepList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create new activity

                Intent repIntent = new Intent(SearchActivity.this, RepresentativeList.class);
                startActivity(repIntent);
            }
        });

        Intent intent = getIntent();
        if (intent.getBooleanExtra("RANDOM", false)) {
            reps = new LinkedList<Representative>();
            getRandom();
        }
        else {
            reps = new LinkedList<Representative>();
        }
    }

    void getRandom() {
        String newZip;
        int asdf = 0;

        try {
            do {
                double mainlandOrHawaiiAlaska = Math.random();
                if (mainlandOrHawaiiAlaska > 3 / 435) {
                    CountyAndState cs;
                    do {
                        cs = new CountyAndState();

                        double longitude = Math.random() * -58 - 67;
                        double latitude = Math.random() * 27 + 20;
                        newZip = new GeocodeForZipCaller().execute(latitude, longitude).get();
                        if (newZip != null) {
                            cs = new GeocodeForCountyState().execute(newZip).get();
                        }
                        Log.d("T", newZip + " " + (newZip == null));
                    } while (newZip == null || cs.county == null);
                }
                else {
                    double district = Math.random();
                    if (district < 0.33) {
                        newZip = "99501";
                    }
                    else if (district < 0.66) {
                        newZip = "96814";
                    }
                    else {
                        newZip = "96753";
                    }
                }

                EditText zipEdit = (EditText) findViewById(R.id.editMobileSearchZIP);
                zipEdit.setText(newZip, TextView.BufferType.EDITABLE);
                asdf++;
                Log.d("T", "\t" + asdf + "");
            }
            while (!searchZIP(newZip));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void getLocation() {
        try {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        catch (SecurityException se) {
            new AlertDialog.Builder(currentContext).setMessage("Permission to get location denied.").show();
        }
    }

    private boolean searchZIP(String zip) {
        reps.clear();

        try {
            reps = new SunlightRepFinder().execute(zip).get();

            if (reps.size() > 0) {
                RepresentativeList.zip = zip;
                CountyAndState cs = new GeocodeForCountyState().execute(zip).get();
                RepresentativeList.county = cs.county;
                RepresentativeList.state = cs.state;

                for (Representative rep : reps) {
                    new TweetIDGetter().execute(rep).get();
                    new PictureGetter().execute(rep).get();
                }

                ImageView iv = (ImageView) findViewById(R.id.imgMobileSearchRep1);
                iv.setImageBitmap(reps.get(0).picture);

                iv = (ImageView) findViewById(R.id.imgMobileSearchRep2);
                iv.setImageBitmap(reps.get(1).picture);

                iv = (ImageView) findViewById(R.id.imgMobileSearchRep3);
                iv.setImageBitmap(reps.get(2).picture);

                fadeIn(results);

                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendIntent.putExtra(PhoneToWatchService.IS_VOTE, false);
                sendIntent.putExtra(PhoneToWatchService.INDEX, 0);
                startService(sendIntent);
                return true;
            }
            else {
                Toast.makeText(this, "Could not find representatives for this ZIP", Toast.LENGTH_SHORT);
                results.setVisibility(View.INVISIBLE);
                return false;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void fadeIn(final View v) {
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setInterpolator(new AccelerateInterpolator());
            anim.setDuration(500);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            v.startAnimation(anim);
    }

    /*
    * Interface method implementations
    */
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(10);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
        catch (SecurityException se) {
            new AlertDialog.Builder(currentContext).setMessage("Permission to get location denied.").show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
    }
}
