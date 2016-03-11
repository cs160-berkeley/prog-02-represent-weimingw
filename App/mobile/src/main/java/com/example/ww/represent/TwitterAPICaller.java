package com.example.ww.represent;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.List;

/**
 * Created by WW on 3/8/2016.
 */
class TweetIDGetter extends AsyncTask<Representative, String, Long> {

  @Override
  protected Long doInBackground(Representative... params) {
    final Representative rep = params[0];
    final String twitterHandle = rep.twitter;
    final Long tweetID = null;

    TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
    // Can also use Twitter directly: Twitter.getApiClient()
    StatusesService statusesService = twitterApiClient.getStatusesService();
    statusesService.userTimeline(null, twitterHandle, new Integer(1), null,null, null, null, null, null, new Callback<List<Tweet>>() {
      @Override
      public void success(Result<List<Tweet>> result) {
        List<Tweet> tweets = result.data;
        if (tweets.size() > 0) {
          Tweet mostRecent = result.data.get(0);
          rep.tweetID = new Long(mostRecent.id);

          Log.d("T", rep.twitter + "'s tweet: " + mostRecent.id + " " + mostRecent.text);
        }
      }

      @Override
      public void failure(TwitterException e) {
        Log.d("T", e.toString());
      }
    });
    return null;
  }
}

class TwitterWrapper {
  Drawable pic;
  Long tweetID;

}