package com.rolandoasmat.nvelope;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by rolandoasmat on 9/2/17.
 */

public class GoogleAnalyticsManager {
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static FirebaseAnalytics getInstance(Context context){
        if(mFirebaseAnalytics == null){
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
        return mFirebaseAnalytics;
    }
}
