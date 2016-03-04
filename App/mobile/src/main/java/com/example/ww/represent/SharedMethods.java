package com.example.ww.represent;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by WW on 2/27/2016.
 */
class SharedMethods {
    static void setPartyImages(String partyMembership, ImageView partyIcon, ImageView background) {
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
}
