package com.example.ww.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;

public class RepresentativeList extends Activity {

    ListView repListView;
    RepresentativeAdapter repAdapter;

    static String zip = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative_list);

        repListView = (ListView) findViewById(R.id.listMobileRepList);
        repAdapter = new RepresentativeAdapter(this,
                SearchActivity.reps);
        repListView.setAdapter(repAdapter);

        repListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(RepresentativeList.this, DetailedProfile.class);
                detailIntent.putExtra("INDEX", position);
                startActivity(detailIntent);
            }
        });
    }
}

class RepresentativeAdapter extends BaseAdapter {
    private Context context;
    private LinkedList<Representative> repList;

    public RepresentativeAdapter(Context context, LinkedList<Representative> repList) {
        this.context = context;
        this.repList = repList;
    }

    @Override
    public int getCount() {
        return repList.size();
    }

    @Override
    public Object getItem(int position) {
        return repList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Representative rep = repList.get(position);

        View repListItem = inflater.inflate(R.layout.rep_list_item, parent, false);
        TextView repName = (TextView) repListItem.findViewById(R.id.textMobileListItemRepName);
        TextView party = (TextView) repListItem.findViewById(R.id.textMobileListItemParty);
        TextView term = (TextView) repListItem.findViewById(R.id.textMobileListItemTerm);
        TextView email = (TextView) repListItem.findViewById(R.id.textMobileListItemEmail);
        TextView website = (TextView) repListItem.findViewById(R.id.textMobileListItemWebsite);
        TextView twitter = (TextView) repListItem.findViewById(R.id.textMobileListItemTwitter);
        TextView lastTweet = (TextView) repListItem.findViewById(R.id.textMobileListItemLastTweet);
        ImageView picture = (ImageView) repListItem.findViewById(R.id.imgMobileListItemPicture);
        ImageView partyIcon = (ImageView) repListItem.findViewById(R.id.imgMobileListItemPartyIcon);
        ImageView background = (ImageView) repListItem.findViewById(R.id.imgMobileListItemBackground);

        SharedMethods.setName(rep.isSenator, rep.name, repName);
        party.setText(rep.party.toString());
        term.setText(rep.termBegin + "-" + rep.termEnd);
        email.setText(rep.email);
        website.setText(rep.website);
        twitter.setText(rep.twitter);
        lastTweet.setText(rep.lastTweet);
        picture.setImageDrawable(rep.picture);
        SharedMethods.setPartyImages(rep.party, partyIcon, background);

        return repListItem;
    }
}