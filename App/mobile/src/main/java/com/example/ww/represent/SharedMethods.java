package com.example.ww.represent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by WW on 2/27/2016.
 */
class SharedMethods {
    static void setPartyImages(String partyMembership, ImageView partyIcon, View background) {
        switch (partyMembership) {
            case "Democrat":
                partyIcon.setImageResource(R.drawable.donkey);
                background.setBackgroundColor(0xff297bff);
                return;
            case "Republican":
                partyIcon.setImageResource(R.drawable.elephant);
                background.setBackgroundColor(0xFFFF4444);
                return;
            default:
                partyIcon.setImageResource(R.drawable.americanflag);
                background.setBackgroundColor(0xFF27FF32);
        }
    }

    static void setName(boolean isSenator, String name, TextView tv) {
        if (isSenator) {
            tv.setText("Sen. " + name);
        }
        else {
            tv.setText("Rep. " + name);
        }
    }

    static String inputStreamToString(InputStream is) {
        String result = "";

        try {
            if (is != null) {
                StringWriter writer = new StringWriter();

                char[] buffer = new char[1024];
                try
                {
                    Reader reader = new BufferedReader(
                      new InputStreamReader(is, "UTF-8"));
                    int n;
                    while ((n = reader.read(buffer)) != -1)
                    {
                        writer.write(buffer, 0, n);
                    }
                }
                finally
                {
                    is.close();
                }
                result = writer.toString();
            } else {
                return "";
            }
        }
        catch (IOException ioe) {

        }
        return result;
    }
}

class PictureGetter extends AsyncTask<Representative, String, Bitmap> {
    final String imgURLBase = "https://raw.githubusercontent.com/unitedstates/images/gh-pages/congress/225x275/";
    @Override
    protected Bitmap doInBackground(Representative... params) {
        try {
            Representative rep = params[0];
            String src = imgURLBase + rep.bioguideID + ".jpg";

            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            rep.picture = myBitmap;
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
