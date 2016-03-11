package com.example.ww.represent;

import android.app.AlertDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



/**
 * Created by WW on 3/6/2016.
 */
class GeocodeForZipCaller extends AsyncTask<Double, String, String> {

  private static final String apiURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";

  @Override
  protected String doInBackground(Double... coords) {
    String result = null;
    Double latitude = coords[0];
    Double longitude = coords[1];
    String apiCallString = apiURL + latitude.toString() + "," + longitude.toString();
    Log.d("T", "Got Geocoding API calling string of " + apiCallString);

    InputStream in = null;

    try {
      URL url = new URL(apiCallString);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      in = new BufferedInputStream(urlConnection.getInputStream());
      JSONArray reply = new JSONObject(SharedMethods.inputStreamToString(in)).getJSONArray("results");
      //JSONObject addr = reply.getJSONObject(0);
      JSONArray addr = reply.getJSONObject(0).getJSONArray("address_components");

      for (int i = 0; i < addr.length(); i++) {
        JSONObject comp = addr.getJSONObject(i);
        JSONArray types = comp.getJSONArray("types");

        for (int j = 0; j < types.length(); j++) {
          if (types.get(j).equals("postal_code")) {
            result = comp.getString("short_name");
          }
        }
      }
      //Log.d("T", "JSON object you're in: " + addr.toString(4));
    }
    catch (Exception e ) {
      System.out.println(e.getMessage());
      return e.getMessage();
    }
    Log.d("T", "result is: " + result);
    return result;
  }
}

class CountyAndState
{
  String county;
  String state;
}

/**
 * Created by WW on 3/6/2016.
 */
class GeocodeForCountyState extends AsyncTask<String, String, CountyAndState> {

  private static final String apiURL = "https://maps.googleapis.com/maps/api/geocode/json?address=";

  @Override
  protected CountyAndState doInBackground(String... zips) {
    CountyAndState result = new CountyAndState();
    String zip = zips[0];
    String apiCallString = apiURL + zip;
    Log.d("T", "Got Geocoding API calling string of " + apiCallString);

    InputStream in = null;

    try {
      URL url = new URL(apiCallString);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      in = new BufferedInputStream(urlConnection.getInputStream());
      JSONArray reply = new JSONObject(SharedMethods.inputStreamToString(in)).getJSONArray("results");
      //JSONObject addr = reply.getJSONObject(0);
      JSONArray addr = reply.getJSONObject(0).getJSONArray("address_components");

      for (int i = 0; i < addr.length(); i++) {
        JSONObject comp = addr.getJSONObject(i);
        JSONArray types = comp.getJSONArray("types");

        for (int j = 0; j < types.length(); j++) {
          if (types.get(j).equals("administrative_area_level_2")) {
            result.county = comp.getString("short_name");
          }
          else if (types.get(j).equals("administrative_area_level_1")) {
            result.state = comp.getString("short_name");
          }
        }
      }
      //Log.d("T", "JSON object you're in: " + addr.toString(4));
    }
    catch (Exception e ) {
      System.out.println(e.getMessage());
      Log.d("T", e.getMessage());
      return new CountyAndState();
    }
    Log.d("T", "result is: " + result.county + ", " + result.state);
    return result;
  }
}