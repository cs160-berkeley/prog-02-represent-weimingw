package com.example.ww.represent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages

    private static final String INDEX_PATH = "/index";
    private static final String NAME_PATH = "/name";
    private static final String PARTY_PATH = "/party";
    private static final String PIC_PATH = "/pic";

    private static final String OBAMA_VOTES_PATH = "/obama";
    private static final String ROMNEY_VOTES_PATH = "/romney";
    private static final String COUNTY_PATH = "/county";

    private static String repName = null;
    private static String repParty = null;
    private static Bitmap repPic = null;
    private static int index = -1;

    private static float obamaVotes = Float.NaN;
    private static float romneyVotes = Float.NaN;
    private static String county = null;


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());
        //use the 'path' field in sendmessage to differentiate use cases
        //(here, fred vs lexy)

        if (messageEvent.getPath().equalsIgnoreCase( NAME_PATH ))
        {
            repName = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            //send message to phone to send the next part
        }
        else if (messageEvent.getPath().equalsIgnoreCase( PARTY_PATH ))
        {
            repParty = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            //send message to phone to send n
        }
        else if (messageEvent.getPath().equalsIgnoreCase( PIC_PATH ))
        {
            repName = new String(messageEvent.getData());
        }
        else if (messageEvent.getPath().equalsIgnoreCase( INDEX_PATH ))
        {
           String indexString = new String(messageEvent.getData(), StandardCharsets.UTF_8);
           index = Integer.parseInt(indexString);
        }
        else {
            if (messageEvent.getPath().equalsIgnoreCase( OBAMA_VOTES_PATH ))
            {
                String voteString = new String(messageEvent.getData(), StandardCharsets.UTF_8);
                obamaVotes = Float.parseFloat(voteString);
            }
            else if (messageEvent.getPath().equalsIgnoreCase( ROMNEY_VOTES_PATH ))
            {
                String voteString = new String(messageEvent.getData(), StandardCharsets.UTF_8);
                romneyVotes = Float.parseFloat(voteString);
            }
            else if (messageEvent.getPath().equalsIgnoreCase( COUNTY_PATH ))
            {
                county = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            }
            else
            {
                super.onMessageReceived( messageEvent );
            }

            //make vote activity here
            if (!Float.isNaN(obamaVotes) && !Float.isNaN(romneyVotes) && county != null)
            {
                Intent voteIntent = new Intent(this, Votes.class);
                voteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                voteIntent.putExtra("OBAMA", obamaVotes);
                voteIntent.putExtra("ROMNEY", romneyVotes);
                voteIntent.putExtra("COUNTY", county);
                startActivity(voteIntent);
                obamaVotes = Float.NaN;
                romneyVotes = Float.NaN;
                county = null;

            }
            return;

        }

        //make representative activity here
        checkMakePage();
    }

    private void checkMakePage()
    {
        if (repName != null && repParty != null && index >= 0 && repPic != null)
        {
            //TODO Check for picture existence
            Intent intent = new Intent(this, Representative.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra(Representative.NAME_KEY, repName);
            intent.putExtra(Representative.PARTY_KEY, repParty);
            intent.putExtra(Representative.INDEX_KEY, index);
            intent.putExtra(Representative.PIC_KEY, repPic);
            startActivity(intent);
            repName = null;
            repParty = null;
            index = -1;
            repPic = null;
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
              event.getDataItem().getUri().getPath().equals("/image")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset profileAsset = dataMapItem.getDataMap().getAsset("profileImage");
                Bitmap bitmap = loadBitmapFromAsset(profileAsset);
                // Do something with the bitmap
                repPic = bitmap;
                checkMakePage();
            }
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder( this )
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

        ConnectionResult result = mGoogleApiClient.blockingConnect(10000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
          mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }
}