package com.fivetrue.gimpo.ac05.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.ByPassAcitivty;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by kwonojin on 16. 3. 28..
 */
public class GCMService extends GcmListenerService {

    private static final String TAG = "GCMService";

    private ConfigPreferenceManager mConfigPref = null;

    private NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        if(mConfigPref == null){
            mConfigPref = new ConfigPreferenceManager(this);
        }
        if(mNotificationManager == null){
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        GcmMessage message = new GcmMessage(data);

        if(message != null && DefaultPreferenceManager.getInstance(this).isPushService()){
            createNotification(message);
        }
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
    }
    public void createNotification(final GcmMessage data) {

        if (data != null) {
            if (data.imageUrl != null) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ImageLoadManager.getInstance().loadImageUrl(data.imageUrl, new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                if (response != null && response.getBitmap() != null && !response.getBitmap().isRecycled()) {
                                    makeNotification(data, response.getBitmap());
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                makeNotification(data, null);
                            }
                        });
                    }
                });
            } else {
                makeNotification(data, null);
            }
        }
    }

    private void makeNotification(GcmMessage data, Bitmap image){
        if(data != null){
            String title = TextUtils.isEmpty(data.title) ? getString(R.string.app_name) : data.title;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.push_icon)
                    .setContentTitle(title)
                    .setContentText(data.message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                    .setAutoCancel(true);

            if(image != null && !image.isRecycled()){
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                bigPictureStyle.setBigContentTitle(title)
                        .setSummaryText(data.message)
                        .bigPicture(image)
                        .bigLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
                builder.setStyle(bigPictureStyle);
            }else{
                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle(title)
                        .setSummaryText(getString(R.string.app_name))
                        .bigText(data.message);
                builder.setStyle(bigTextStyle);
            }

            Intent targetIntent = data.toIntent();
            targetIntent.setClass(this, ByPassAcitivty.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(data.id, builder.build());
        }
    }
}
