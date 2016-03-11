package com.example.ww.represent;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

/**
 * Created by WW on 3/7/2016.
 */
class SunlightRepFinder extends AsyncTask<String, String, LinkedList<Representative>> {
  String apiKey = "&apikey=5e62d221fd7340cc8a28828e59220325";
  String apiRepURL = "http://congress.api.sunlightfoundation.com/legislators/locate?zip=";

  @Override
  protected LinkedList<Representative> doInBackground(String... zips) {
    String zip = zips[0];
    LinkedList<Representative> reps = new LinkedList<>();
    String apiCallString = apiRepURL + zip + apiKey;

    Log.d("T", "Got Sunlight API calling string of " + apiCallString);

    InputStream in = null;

    try {
      URL url = new URL(apiCallString);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      in = new BufferedInputStream(urlConnection.getInputStream());
      JSONArray reply = new JSONObject(SharedMethods.inputStreamToString(in)).getJSONArray("results");

      for (int i = 0; i < reply.length(); i++) {
        JSONObject jo = reply.getJSONObject(i);

        Representative rep = new Representative();

        if (jo.getString("middle_name").equals("null")) {
          rep.name = jo.getString("first_name") + " " + jo.getString("last_name");
        }
        else {
          rep.name = jo.getString("first_name") + " " + jo.getString("middle_name") + " " + jo.getString("last_name");
        }

        switch (jo.getString("title")) {
          case "Rep":
            rep.isSenator = false;
            break;
          default:
            rep.isSenator = true;
            break;
        }

        switch (jo.getString("party")) {
          case "D":
            rep.party = "Democrat";
            break;
          case "R":
            rep.party = "Republican";
            break;
          default:
            rep.party = "Other";
        }

        rep.bioguideID = jo.getString("bioguide_id");

        rep.termBegin = jo.getString("term_start").substring(0, 4);
        rep.termEnd = jo.getString("term_end").substring(0, 4);

        rep.email = jo.getString("oc_email");
        rep.website = jo.getString("website");
        rep.twitter = jo.getString("twitter_id");

        //TODO: give rep a picture! rep.picture = SearchActivity.appContext.getDrawable(R.drawable.generic_m);

        getCommittees(rep);
        getBills(rep);

        reps.add(rep);

        Log.d("T", "Created Rep object with name: " + rep.name + ", " + rep.party);
      }

      return reps;

    }
    catch (Exception e ) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  String apiCommitteeURL = "http://congress.api.sunlightfoundation.com/committees?member_ids=";

  private void getCommittees(Representative rep)
  {
    String apiCallString = apiCommitteeURL + rep.bioguideID + apiKey;

    Log.d("T", "Got Sunlight API calling string of " + apiCallString);

    InputStream in = null;

    try {
      URL url = new URL(apiCallString);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      in = new BufferedInputStream(urlConnection.getInputStream());
      JSONArray reply = new JSONObject(SharedMethods.inputStreamToString(in)).getJSONArray("results");
      LinkedList<String> committees = new LinkedList<>();

      for (int i = 0; i < reply.length(); i++) {
        JSONObject jo = reply.getJSONObject(i);
        committees.add(jo.getString("name"));
      }

      rep.committees = committees;

//      for (String s: committees) {
//        Log.d("T", "Committee: " + s);
//      }
    } catch (Exception e) {
      Log.d("T", "Exception occurred for " + rep.name);
    }
  }

  String apiBillURL = "http://congress.api.sunlightfoundation.com/bills/search?sponsor_id=";

  private void getBills(Representative rep)
  {
    String apiCallString = apiBillURL + rep.bioguideID + apiKey;

    Log.d("T", "Got Sunlight API calling string of " + apiCallString);

    InputStream in = null;

    try {
      URL url = new URL(apiCallString);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      in = new BufferedInputStream(urlConnection.getInputStream());
      JSONArray reply = new JSONObject(SharedMethods.inputStreamToString(in)).getJSONArray("results");

      LinkedList<String> bills = new LinkedList<String>();
      for (int i = 0; i < reply.length(); i++) {
        JSONObject jo = reply.getJSONObject(i);

        String billName;

        billName = jo.getString("short_title");
        if (billName.equals("null")) {
          billName = jo.getString("official_title");
        }

        bills.add(billName);
      }

//      for (String s : bills) {
//        Log.d("T", "Bill: " + s);
//      }

      rep.recentBills = bills;
    } catch (Exception e) {
      Log.d("T", "Exception occurred for " + rep.name);
    }
  }
}
