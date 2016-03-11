package com.example.ww.represent;

import android.app.SearchableInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by joleary on 2/19/16.
 */
public class PhoneToWatchService extends Service {

    private GoogleApiClient mApiClient;

    static final String IS_VOTE = "IS_VOTE";
    static final String INDEX = "INDEX";

    private static final String INDEX_PATH = "/index";
    private static final String NAME_PATH = "/name";
    private static final String PARTY_PATH = "/party";

    private static final String OBAMA_VOTES_PATH = "/obama";
    private static final String ROMNEY_VOTES_PATH = "/romney";
    private static final String COUNTY_PATH = "/county";

    private static JSONArray countyAndVotes = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Which cat do we want to feed? Grab this info from INTENT
        // which was passed over when we called startService
        Bundle extras = intent.getExtras();

        boolean is_vote = extras.getBoolean("IS_VOTE");
        if (!is_vote) {
            // Start activity using representative info on the watch
            int i = extras.getInt("INDEX");
            createWatchActivity(i);
        }
        else {
            // Send vote
            getAndSendVotes();
        }

        return START_STICKY;
    }

    private void createWatchActivity(final int i)
    {
        final Representative rep = SearchActivity.reps.get(i);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //first, connect to the apiclient
                mApiClient.connect();
                //now that you're connected, send a massage with the cat name
                sendMessage(INDEX_PATH, "" + i);

                if (rep.isSenator) {
                    sendMessage(NAME_PATH, "Sen. " + rep.name);
                }
                else {
                    sendMessage(NAME_PATH, "Rep. " + rep.name);
                }

                sendBitmap(rep.picture);

                if (rep.party.equals("Democrat")) {
                    sendMessage(PARTY_PATH, "(D)");
                }
                else if (rep.party.equals("Republican")) {
                    sendMessage(PARTY_PATH, "(R)");
                }
                else if (rep.party.equals("Independent")) {
                    sendMessage(PARTY_PATH, "(I)");
                }
                else if (rep.party.equals("Other")) {
                    sendMessage(PARTY_PATH, "N/A");
                }
            }
        }).start();
    }

    private void getAndSendVotes()
    {
        try {
            if (countyAndVotes == null) {
                //initialize it
                InputStream is = getResources().openRawResource(R.raw.election_county_2012);
                Writer writer = new StringWriter();
                char[] buffer = new char[1024];
                try {
                    Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    int n;
                    while ((n = reader.read(buffer)) != -1) {
                        writer.write(buffer, 0, n);
                    }
                }
                finally {
                    is.close();
                }

                countyAndVotes = new JSONArray(writer.toString());
            }

            if (RepresentativeList.county == null) {
                return;
            }

            if (RepresentativeList.state.equals("AK")) {
                for (int i = 0; i < countyAndVotes.length(); i++) {
                    JSONObject jo = countyAndVotes.getJSONObject(i);
                    if (jo.getString("state-postal").equals(RepresentativeList.state)) {
                        sendMessage(OBAMA_VOTES_PATH, jo.getString("obama-percentage"));
                        sendMessage(ROMNEY_VOTES_PATH, jo.getString("romney-percentage"));
                        sendMessage(COUNTY_PATH, RepresentativeList.county + ", " + RepresentativeList.state);
                    }
                }
            }

            //search through it
            //countyAndVotes.get()

            int cutoff = RepresentativeList.county.indexOf("County") - 1;
            String newstring = RepresentativeList.county.substring(0, cutoff);

            for (int i = 0; i < countyAndVotes.length(); i++) {
                JSONObject jo = countyAndVotes.getJSONObject(i);
                if (jo.getString("county-name").equals(RepresentativeList.county.substring(0, cutoff)) &&
                    jo.getString("state-postal").equals(RepresentativeList.state)) {
                    sendMessage(OBAMA_VOTES_PATH, jo.getString("obama-percentage"));
                    sendMessage(ROMNEY_VOTES_PATH, jo.getString("romney-percentage"));
                    sendMessage(COUNTY_PATH, RepresentativeList.county + ", " + RepresentativeList.state);
                }
            }
//            Log.d("T", countyAndVotes.toString(2));
//            sendMessage(COUNTY_PATH, RepresentativeList.county + ", " + RepresentativeList.state);
//            sendMessage(OBAMA_VOTES_PATH, "66.23");
//            sendMessage(ROMNEY_VOTES_PATH, "31.09");
//            sendMessage(COUNTY_PATH, "Alameda County, CA");
        }
        catch (Exception e) {
            Log.d("T", e.toString());
        }
    }

    @Override //remember, all services need to implement an IBiner
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage( final String path, final String text ) {
        //one way to send message: start a new thread and call .await()
        //see watchtophoneservice for another way to send a message
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    //we find 'nodes', which are nearby bluetooth devices (aka emulators)
                    //send a message for each of these nodes (just one, for an emulator)
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                    //4 arguments: api client, the node ID, the path (for the listener to parse),
                    //and the message itself (you need to convert it to bytes.)
                }
            }
        }).start();
    }

    /* The following code I got from StackOverflow */
    private void sendBitmap(Bitmap img) {

        Asset asset = createAssetFromBitmap(img);

        PutDataMapRequest dataMap = PutDataMapRequest.create("/image");
        dataMap.getDataMap().putAsset("profileImage", asset);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
          .putDataItem(mApiClient, request);
    }

    private void sendDrawable(Drawable img) {

        Bitmap bitmap = drawableToBitmap(img);
        Asset asset = createAssetFromBitmap(bitmap);

        PutDataMapRequest dataMap = PutDataMapRequest.create("/image");
        dataMap.getDataMap().putAsset("profileImage", asset);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
          .putDataItem(mApiClient, request);
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
