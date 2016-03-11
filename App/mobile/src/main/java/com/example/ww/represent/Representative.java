package com.example.ww.represent;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.ParcelableSpan;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by WW on 2/27/2016.
 */
class Representative {
    Bitmap picture;

    boolean isSenator;
    String bioguideID;
    String name;
    String party;
    String termBegin;
    String termEnd;
    String email;
    String website;
    String twitter;
    Long tweetID;

    LinkedList<String> recentBills;
    LinkedList<String> committees;
}

enum Party {
    DEMOCRAT, REPUBLICAN, INDEPENDENT, OTHER;

    @Override
    public String toString() {
        switch (this) {
            case DEMOCRAT: return "Democrat";
            case REPUBLICAN: return "Republican";
            case INDEPENDENT: return "Independent";
            case OTHER: return "Other";
            default: return "";
        }
    }
}