package com.fivetrue.gimpo.ac05.service;

import android.os.Bundle;
import android.util.Log;

import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.service.notification.NotificationHelper;
import com.fivetrue.gimpo.ac05.ui.ByPassAcitivty;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;
import com.fivetrue.gimpo.ac05.ui.SplashActivity;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

/**
 * Created by kwonojin on 16. 3. 28..
 */
public class GCMService extends GcmListenerService {

    private static final String TAG = "GCMService";
    private static final String DATA_KEY = "data";

    private static final int DEFAULT_NOTIFICATION_ID = 0x88;

    private ConfigPreferenceManager mConfigPref = null;
    private NotificationHelper mNotificationHelper = null;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        if(mConfigPref == null){
            mConfigPref = new ConfigPreferenceManager(this);
        }
        mNotificationHelper = new NotificationHelper(this);

        String message = data.getString(DATA_KEY);
        if(message != null && DefaultPreferenceManager.getInstance(this).isPushService()){
            NotificationData noti = new Gson().fromJson(message, NotificationData.class);
            if(noti.getId() <= 0){
                noti.setId(DEFAULT_NOTIFICATION_ID);
            }
            noti.setTargetClass(ByPassAcitivty.class.getName());

            mNotificationHelper.createNotification(noti);
        }
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
    }
}
