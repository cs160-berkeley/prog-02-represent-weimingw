package com.example.ww.represent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.util.LinkedList;

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

    static LinkedList<Representative> reps;

    GoogleApiClient mGoogleApiClient;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        currentContext = this;

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
                String str = "94704";
                //TODO: Geocoding location to get ZIP
                searchZIP(str);
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
            getRandom();
        }
        else {
            reps = new LinkedList<Representative>();
        }
    }

    void getRandom() {
        reps.clear();
        String newZip;
        do {
            if (Math.random() < 0.5) {
                newZip = "94704";
            }
            else {
                newZip = "89139";
            }
        } while (newZip.equals(RepresentativeList.zip));


        EditText zipEdit = (EditText) findViewById(R.id.editMobileSearchZIP);
        zipEdit.setText(newZip, TextView.BufferType.EDITABLE);

        searchZIP(newZip);
    }

    private void getLocation() {
        try {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation == null) {
                new AlertDialog.Builder(currentContext).setMessage("Could not get current location. Maybe location services are disabled?").show();
            }
        }
        catch (SecurityException se) {
            new AlertDialog.Builder(currentContext).setMessage("Permission to get location denied.").show();
        }
    }

    private void searchZIP(String zip) {
        reps.clear();

        RepresentativeList.zip = zip;

        //if results > 0, fill up reps
        //else throw invalid alert
        if (zip.equals("94704")) {
            Representative rep1 = new Representative();
            rep1.picture = getResources().getDrawable(R.drawable.barbaralee, getTheme());
            rep1.isSenator = false;
            rep1.name = "Barbara Lee";
            rep1.party = "Democrat";
            rep1.termBegin = 2015;
            rep1.termEnd = 2017;
            rep1.email = "lee@example.com";
            rep1.website = "lee.house.gov";
            rep1.twitter = "@RepBarbaraLee";
            rep1.lastTweet = "ICYMI: The @mlkfreedomctr will live stream the Barbara Lee & Elihu Harris lecture w/ @bobbyseale tonight at 6:45pm. Don’t miss it!";
            rep1.committees = new LinkedList<String>();
            rep1.recentBills = new LinkedList<String>();
            rep1.committees.add("House Committee on Appropriations");
            rep1.committees.add("House Committee on The Budget");
            rep1.committees.add("House Democratic Steering and Policy Committee");
            rep1.recentBills.add("H.R.197 : Food Assistance to Improve Reintegration Act of 2013");
            rep1.recentBills.add("H.R.198 : Repeal of the Authorization for Use of Military Force");
            rep1.recentBills.add("H.R.199 : Income Equity Act of 2013");
            rep1.recentBills.add("H.R.200 : Responsible End to the War in Afghanistan Act");
            rep1.recentBills.add("H.R.201 : Poverty Impact Trigger Act of 2013");
            reps.add(rep1);

            Representative rep2 = new Representative();
            rep2.picture = getResources().getDrawable(R.drawable.barbaraboxer, getTheme());
            rep2.isSenator = true;
            rep2.name = "Barbara Boxer";
            rep2.party = "Democrat";
            rep2.termBegin = 2011;
            rep2.termEnd = 2017;
            rep2.email = "senator@boxer.senate.gov";
            rep2.website = "www.boxer.senate.gov";
            rep2.twitter = "@SenatorBoxer";
            rep2.lastTweet = "@SenateDems stood united at the Supreme Court today to tell @Senate_GOPs: #DoYourJob";
            rep2.committees = new LinkedList<String>();
            rep2.recentBills = new LinkedList<String>();
            rep2.committees.add("U.S. Senate Committee on Environment and Public Works");
            rep2.committees.add("U.S. Senate Committee on Foreign Relations");
            rep2.committees.add("U.S. Senate Select Committee on Ethics");
            rep2.recentBills.add("S.1983 : Pechanga Band of Luiseno Mission Indians Water Rights Settlement Act");
            rep2.recentBills.add("S.2487 : Female Veteran Suicide Prevention Act");
            rep2.recentBills.add("S.2412 : Tule Lake National Historic Site Establishment Act of 2015");
            rep2.recentBills.add("S.2204 : End of Suffering Act of 2015");
            rep2.recentBills.add("S.1971 : California Coastal National Monument Expansion Act");
            reps.add(rep2);

            Representative rep3 = new Representative();
            rep3.picture = getResources().getDrawable(R.drawable.dianefeinstein, getTheme());
            rep3.isSenator = true;
            rep3.name = "Dianne Feinstein";
            rep3.party = "Democrat";
            rep3.termBegin = 2013;
            rep3.termEnd = 2019;
            rep3.email = "senator@feinstein.senate.gov";
            rep3.website = "www.feinstein.senate.gov";
            rep3.twitter = "@SenFeinstein";
            rep3.lastTweet = "(2/2) Kalamazoo and Hesston shootings provide yet another call for Congress to act to #StopGunViolence.";
            rep3.committees = new LinkedList<String>();
            rep3.recentBills = new LinkedList<String>();
            rep3.committees.add("U.S. Senate Committee on Environment and Public Works");
            rep3.committees.add("U.S. Senate Committee on Foreign Relations");
            rep3.committees.add("U.S. Senate Select Committee on Ethics");
            rep3.recentBills.add("S.524 : Comprehensive Addiction and Recovery Act of 2016");
            rep3.recentBills.add("S.Res.374 : A resolution relating to the death of Antonin Scalia, Associate Justice of the Supreme Court of the United States.");
            rep3.recentBills.add("S.Res.376 : A resolution designating the first week of April 2016 as \"National Asbestos Awareness Week\".");
            rep3.recentBills.add("S.2234 : Office of Strategic Services Congressional Gold Medal Act");
            rep3.recentBills.add("S.2276 : SAFE PIPES Act");
            reps.add(rep3);

        }
        else if (zip.equals("89139")) {
            Representative rep1 = new Representative();
            rep1.picture = getResources().getDrawable(R.drawable.joeheck, getTheme());
            rep1.isSenator = false;
            rep1.name = "Joseph Heck";
            rep1.party = "Republican";
            rep1.termBegin = 2015;
            rep1.termEnd = 2017;
            rep1.email = "heck@example.com";
            rep1.website = "heck.house.gov";
            rep1.twitter = "@repjoeheck";
            rep1.lastTweet = "1st #WomensHistoryMonth feature: Army General Ann E. Dunwoody, first woman to serve as 4 star general in Army #NV03 ";
            rep1.committees = new LinkedList<String>();
            rep1.recentBills = new LinkedList<String>();
            rep1.committees.add("House Armed Forces Committee");
            rep1.committees.add("Education and the Workforce Committee");
            rep1.committees.add("House Permanent Select Committee on Intelligence");
            rep1.recentBills.add("H.R.2685 - Department of Defense Appropriations Act, 2016");
            rep1.recentBills.add("H.R.4670 - Mojave National Preserve Boundary Adjustment Act of 2016");
            reps.add(rep1);

            Representative rep2 = new Representative();
            rep2.picture = getResources().getDrawable(R.drawable.deanheller, getTheme());
            rep2.isSenator = true;
            rep2.name = "Dean Heller";
            rep2.party = "Republican";
            rep2.termBegin = 2011;
            rep2.termEnd = 2017;
            rep2.email = "senator@heller.senate.gov";
            rep2.website = "www.heller.senate.gov";
            rep2.twitter = "@SenDeanHeller";
            rep2.lastTweet = "Today marks the 75th anniversary of @mmschocolate -- Happy Birthday! #CelebrateWithM";
            rep2.committees = new LinkedList<String>();
            rep2.recentBills = new LinkedList<String>();
            rep2.committees.add("U.S. Senate Committee on Banking, Housing, and Urban Affairs");
            rep2.committees.add("U.S. Senate Committee on Commerce, Science, and Transportation");
            rep2.committees.add("U.S. Senate Committee on Finance");
            rep2.committees.add("U.S. Senate Committee on Veterans' Affairs");
            rep2.committees.add("U.S. Senate Special Committee on Aging");
            rep2.recentBills.add("S.1436 - Nevada Native Nations Land Act");
            rep2.recentBills.add("S.2604 - Digital Security Commission Act of 2016");
            rep2.recentBills.add("S.Res.374 - A resolution relating to the death of Antonin Scalia, Associate Justice of the Supreme Court of the United States.");
            reps.add(rep2);

            Representative rep3 = new Representative();
            rep3.picture = getResources().getDrawable(R.drawable.harryreid, getTheme());
            rep3.isSenator = true;
            rep3.name = "Harry Reid";
            rep3.party = "Democrat";
            rep3.termBegin = 2013;
            rep3.termEnd = 2019;
            rep3.email = "senator@reid.senate.gov";
            rep3.website = "www.reid.senate.gov";
            rep3.twitter = "@senatorreid";
            rep3.lastTweet = "Judiciary Cmte Rs were understandably reluctant to sign onto the McConnell-Grassley pledge to not do their jobs. https://www.facebook.com/SenatorReid/videos/vb.360249323990357/1284429614905652/?type=2&theater …";
            rep3.committees = new LinkedList<String>();
            rep3.recentBills = new LinkedList<String>();
            rep3.committees.add("U.S. Senate Select Committee on Intelligence (Ex officio)");
            rep3.committees.add("U.S. Senate Joint Committee on Inaugural Ceremonies");
            rep3.recentBills.add("S.524 : Comprehensive Addiction and Recovery Act of 2016");
            rep3.recentBills.add("S. 2397: A bill to amend the Child Abuse Prevention and Treatment Act to authorize ...");
            rep3.recentBills.add("S. 2377: Defeat ISIS and Protect and Secure the United States Act of 2015.");
            rep3.recentBills.add("S. 1986: Moapa Band of Paiutes Land Conveyance Act");
            reps.add(rep3);
        }

        if (reps.size() > 0) {
            ImageView iv = (ImageView) findViewById(R.id.imgMobileSearchRep1);
            iv.setImageDrawable(reps.get(0).picture);

            iv = (ImageView) findViewById(R.id.imgMobileSearchRep2);
            iv.setImageDrawable(reps.get(1).picture);

            iv = (ImageView) findViewById(R.id.imgMobileSearchRep3);
            iv.setImageDrawable(reps.get(2).picture);

            fadeIn(results);
        }
        //technically should be doing...
        //search for the district
        //get data for the people
        //show preview thing

        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        sendIntent.putExtra(PhoneToWatchService.IS_VOTE, false);
        sendIntent.putExtra(PhoneToWatchService.INDEX, 0);
        startService(sendIntent);
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
