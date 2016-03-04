package com.example.ww.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
        TextView lastTweet = (TextView) findViewById(R.id.textMobileDetailedProfileLastTweet);
        ImageView picture = (ImageView) findViewById(R.id.imgMobileDetailedProfilePicture);
        ImageView partyIcon = (ImageView) findViewById(R.id.imgMobileDetailedProfilePartyIcon);
        ImageView background = (ImageView) findViewById(R.id.imgMobileDetailedProfileBackground);
        ListView committees = (ListView) findViewById(R.id.listMobileDetailedProfileCommittees);
        ListView recentBills = (ListView) findViewById(R.id.listMobileDetailedProfileRecentBills);

        SharedMethods.setName(rep.isSenator, rep.name, repName);
        party.setText(rep.party.toString());
        term.setText(rep.termBegin + "-" + rep.termEnd);
        email.setText(rep.email);
        website.setText(rep.website);
        twitter.setText(rep.twitter);
        lastTweet.setText(rep.lastTweet);
        picture.setImageDrawable(rep.picture);
        SharedMethods.setPartyImages(rep.party, partyIcon, background);

        StringAdapter sa = new StringAdapter(this, rep.committees);
        committees.setAdapter(sa);

        sa = new StringAdapter(this, rep.recentBills);
        recentBills.setAdapter(sa);
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
