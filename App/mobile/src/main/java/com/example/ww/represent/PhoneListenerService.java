package com.example.ww.represent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String TOAST = "/send_toast";

    private static final String GET_REP_PATH = "/get_rep";
    private static final String INDEX_PATH = "/index";
    private static final String GET_VOTE_PATH = "/get_vote";
    private static final String GET_RANDOM_PATH = "/get_random";
    private static final String GET_DETAILS_PATH = "/get_details";

    private static int index = -1;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(GET_REP_PATH) ) {
            String indexString = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            index = Integer.parseInt(indexString);

            if (index == SearchActivity.reps.size())
            {
                index = 0;
            }

            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            sendIntent.putExtra(PhoneToWatchService.IS_VOTE, false);
            sendIntent.putExtra(PhoneToWatchService.INDEX, index);
            startService(sendIntent);
            index = -1;
        }
        else if ( messageEvent.getPath().equalsIgnoreCase(GET_VOTE_PATH)) {
            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            sendIntent.putExtra(PhoneToWatchService.IS_VOTE, true);
            startService(sendIntent);
        }
        else if ( messageEvent.getPath().equalsIgnoreCase(GET_RANDOM_PATH)) {
            Intent randomIntent = new Intent(getBaseContext(), SearchActivity.class);
            randomIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            randomIntent.putExtra("RANDOM", true);
            startActivity(randomIntent);
        }
        else if ( messageEvent.getPath().equalsIgnoreCase(GET_DETAILS_PATH)) {
            String indexString = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            index = Integer.parseInt(indexString);

            Intent randomIntent = new Intent(getBaseContext(), DetailedProfile.class);
            randomIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            randomIntent.putExtra("INDEX", index);
            startActivity(randomIntent);
            index = -1;
        }
        else {
            super.onMessageReceived( messageEvent );
        }

    }
}