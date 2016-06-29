package com.fivetrue.gimpo.ac05.analytics;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by kwonojin on 16. 6. 25..
 */
public class GoogleAnalytics {

    private static long MIN_SESSION_TIME = 1000 * 60 * 1;
    private static long MAX_SESSION_TIME = 1000 * 60 * 10;
    private static GoogleAnalytics sInstance = null;

    private FirebaseAnalytics mFirebaseAnalytics = null;

    public static void init (Context context){
        sInstance = new GoogleAnalytics(context);
    }

    public static GoogleAnalytics getInstance(){
        return sInstance;
    }

    private GoogleAnalytics(Context context){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.setMinimumSessionDuration(MIN_SESSION_TIME);
        mFirebaseAnalytics.setSessionTimeoutDuration(MAX_SESSION_TIME);
    }


    public void setUserId(String userId){
        mFirebaseAnalytics.setUserId(userId);
    }

    public void setUserProperties(Pair<String, String>... properties){
        for(Pair<String, String> pair : properties){
            mFirebaseAnalytics.setUserProperty(pair.first, pair.second);
        }
    }

    public void sendLogEventProperties(Event event){
        if(event != null){
            Bundle b = new Bundle();
            for(Pair<String, String> p : event.getParams()){
                b.putString(p.first, p.second);
            }
            mFirebaseAnalytics.logEvent(event.name(), b);
        }
    }

}
