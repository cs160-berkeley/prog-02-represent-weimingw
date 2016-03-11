package com.example.ww.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;

import java.util.LinkedList;

public class DetailedProfile extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_profile);

        populateFields();
    }

    private void populateFields()
    {
        Intent myIntent = getIntent();
        int index = myIntent.getIntExtra("INDEX", 0);

        Representative rep = SearchActivity.reps.get(index);

        TextView repName = (TextView) findViewById(R.id.textMobileDetailedProfileRepName);
        TextView party = (TextView) findViewById(R.id.textMobileDetailedProfileParty);
        TextView term = (TextView) findViewById(R.id.textMobileDetailedProfileTerm);
        TextView email = (TextView) findViewById(R.id.textMobileDetailedProfileEmail);
        TextView website = (TextView) findViewById(R.id.textMobileDetailedProfileWebsite);
        TextView twitter = (TextView) findViewById(R.id.textMobileDetailedProfileTwitter);
        ImageView picture = (ImageView) findViewById(R.id.imgMobileDetailedProfilePicture);
        ImageView partyIcon = (ImageView) findViewById(R.id.imgMobileDetailedProfilePartyIcon);
        ScrollView background = (ScrollView) findViewById(R.id.layoutMobileDetailedProfile);

        SharedMethods.setName(rep.isSenator, rep.name, repName);
        party.setText(rep.party.toString());
        term.setText(rep.termBegin + "-" + rep.termEnd);
        email.setText(Html.fromHtml("<a href=\"mailto:"+ rep.email +"\">" + rep.email + "</a>"));
        email.setClickable(true);
        email.setMovementMethod(LinkMovementMethod.getInstance());
        website.setText(Html.fromHtml("<a href=\"" + rep.website + "\">" + rep.website + "</a>"));
        website.setClickable(true);
        website.setMovementMethod(LinkMovementMethod.getInstance());
        twitter.setText(Html.fromHtml("<a href=\"http://www.twitter.com/" + rep.twitter + "\">" + rep.twitter + "</a>"));
        twitter.setClickable(true);
        twitter.setMovementMethod(LinkMovementMethod.getInstance());
        picture.setImageBitmap(rep.picture);
        SharedMethods.setPartyImages(rep.party, partyIcon, background);

        if (rep.tweetID != null) {
            long tweetId = rep.tweetID;
            TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    CompactTweetView tweetView = new CompactTweetView(getBaseContext(), result.data);
                    ((FrameLayout) findViewById(R.id.layoutMobileDetailedTweet)).addView(tweetView);
                    tweetView.setClickable(false);
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.d("TwitterKit", "Load Tweet failure", exception);
                }
            });
        }

        LinearLayout committeeView = (LinearLayout) findViewById(R.id.layoutMobileDetailedCommittees);
        for (String committee : rep.committees) {
            TextView tv = new TextView(getBaseContext());
            tv.setText(committee);
            tv.setPadding(12, 6, 6, 6);
            tv.setTextSize(13);
            committeeView.addView(tv);
        }

        LinearLayout billView = (LinearLayout) findViewById(R.id.layoutMobileDetailedBills);
        for (String bill : rep.recentBills) {
            TextView tv = new TextView(getBaseContext());
            tv.setText(bill);
            tv.setPadding(12, 6, 6, 6);
            tv.setTextSize(13);
            billView.addView(tv);
        }
    }
}

class StringAdapter extends BaseAdapter {

    Context context;
    LinkedList<String> list;

    StringAdapter(Context context, LinkedList<String> srcList)
    {
        this.context = context;
        list = srcList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        String str = list.get(position);

        View basicListItem = inflater.inflate(R.layout.basic_list_item, parent, false);
        TextView tv = (TextView) basicListItem.findViewById(R.id.textMobileBasicListItem1);
        tv.setText(str);
        return basicListItem;
    }
}
