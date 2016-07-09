package com.fivetrue.gimpo.ac05;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.fivetrue.gimpo.ac05.analytics.GoogleAnalytics;
import com.fivetrue.gimpo.ac05.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.net.NetworkManager;

import io.fabric.sdk.android.Fabric;

/**
 * Created by kwonojin on 16. 6. 2..
 */
public class ApplicationEX extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        NetworkManager.init(this);
        ImageLoadManager.init(NetworkManager.getInstance().getRequestQueue());
        GoogleAnalytics.init(this);
    }
}
