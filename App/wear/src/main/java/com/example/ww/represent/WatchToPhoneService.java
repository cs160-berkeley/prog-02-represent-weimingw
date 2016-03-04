package com.example.ww.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchToPhoneService extends Service {

    private GoogleApiClient mApiClient;
    private List<Node> nodes = new ArrayList<>();

    static final String REQUEST_TYPE = "REQUEST_TYPE";
    static final String GET_REP = "GET_REP";
    static final String GET_VOTE = "GET_VOTES";
    static final String GET_RANDOM = "GET_RANDOM";
    static final String INDEX = "INDEX";
    static final String DETAILS = "DETAILS";

    private static final String GET_REP_PATH = "/get_rep";
    private static final String INDEX_PATH = "/index";
    private static final String GET_VOTE_PATH = "/get_vote";
    private static final String GET_RANDOM_PATH = "/get_random";
    private static final String GET_DETAILS_PATH = "/get_details";

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

        if (intent == null) {
            return START_STICKY;
        }

        Bundle extras = intent.getExtras();

        String reqType = extras.getString("REQUEST_TYPE");

        if (reqType.equals(GET_REP)) {
            // Start activity using representative info on the watch
            final int i = extras.getInt(INDEX);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //first, connect to the apiclient
                    mApiClient.connect();
                    //now that you're connected, send a massage with the cat name
                    sendMessage(GET_REP_PATH, "" + i);
                }
            }).start();
        }
        else if (reqType.equals(GET_RANDOM)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //first, connect to the apiclient
                    mApiClient.connect();
                    //now that you're connected, send a massage with the cat name
                    sendMessage(GET_RANDOM_PATH, "");
                }
            }).start();
        }
        else if (reqType.equals(DETAILS))
        {
            final int i = extras.getInt(INDEX);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //first, connect to the apiclient
                    mApiClient.connect();
                    //now that you're connected, send a massage with the cat name
                    sendMessage(GET_DETAILS_PATH, "" + i);
                }
            }).start();
        }
        else {
            // Send vote
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //first, connect to the apiclient
                    mApiClient.connect();
                    //now that you're connected, send a massage with the cat name
                    sendMessage(GET_VOTE_PATH, "");
                }
            }).start();
        }

        return START_STICKY;
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

}