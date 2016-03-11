package com.example.ww.represent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.identity.*;

import io.fabric.sdk.android.Fabric;

public class Login extends Activity {

  private TwitterLoginButton loginButton;
  private TextView status;

  private static final String TWITTER_KEY = "U3cRHDuTx1vJPvxz4CKFyCEP9";
  private static final String TWITTER_SECRET = "6VHRO2I8Tw3Imq3pMQIihqfdeTmsn73cZ60uPP5JSnivXdYKue";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
    Fabric.with(this, new Twitter(authConfig));

    if (Twitter.getSessionManager().getActiveSession() != null) {
      Intent searchIntent = new Intent(getBaseContext(), SearchActivity.class);
      startActivity(searchIntent);
    }

    setContentView(R.layout.activity_login);

    loginButton = (TwitterLoginButton)findViewById(R.id.twitter_login_button);
    status = (TextView)findViewById(R.id.status);

    loginButton.setCallback(new LoginHandler());

    Button noTwitterBtn = (Button) findViewById(R.id.btnMobileLoginNoTwitter);
    noTwitterBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent searchIntent = new Intent(getBaseContext(), SearchActivity.class);
        startActivity(searchIntent);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    loginButton.onActivityResult(requestCode, resultCode, data);
  }

  private class LoginHandler extends Callback<TwitterSession> {
    @Override
    public void success(Result<TwitterSession> twitterSessionResult) {
      Intent searchIntent = new Intent(getBaseContext(), SearchActivity.class);
      startActivity(searchIntent);
    }

    @Override
    public void failure(TwitterException e) {
      status.setText("Status: Login Failed");
    }
  }
}