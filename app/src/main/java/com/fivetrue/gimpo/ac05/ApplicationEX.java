package com.fivetrue.gimpo.ac05;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.net.NetworkManager;
import com.fivetrue.gimpo.ac05.chatting.FirebaseChattingService;

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
        if(!isServiceRunning(FirebaseChattingService.class)){
            startService(new Intent(this, FirebaseChattingService.class));
        }
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
