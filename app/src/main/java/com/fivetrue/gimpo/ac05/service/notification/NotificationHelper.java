package com.fivetrue.gimpo.ac05.service.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.service.BaseServiceHelper;
import com.fivetrue.gimpo.ac05.ui.SplashActivity;


/**
 * Created by ojin.kwon on 2016-02-25.
 */
public class NotificationHelper extends BaseServiceHelper {

    public static final String KEY_NOTIFICATION_PARCELABLE = "noti_parcelable";

    private NotificationManager mManager = null;

    public NotificationHelper(Context context){
        super(context);
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void createNotification(Intent intent){
        if(intent != null){
            createNotification((NotificationData) intent.getParcelableExtra(KEY_NOTIFICATION_PARCELABLE));
        }
    }

    public void createNotification(final NotificationData data){
        if(data != null && data.id > INVALID_VALUE){
            if(data.imageUrl != null){
                ImageLoadManager.getInstance().loadImageUrl(data.imageUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if(response != null && response.getBitmap() != null && !response.getBitmap().isRecycled()){
                            makeNotification(data, response.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        makeNotification(data, null);
                    }
                });
            }else{
                makeNotification(data, null);
            }
        }
    }

    private void makeNotification(NotificationData data, Bitmap image){
        if(data != null){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
            builder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(data.title)
                    .setContentText(data.message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);

            if(image != null && !image.isRecycled()){
                builder.setLargeIcon(image);
            }

            if(data.targetClass != null){
                Class<?> target = null;
                try {
                    target = Class.forName(data.targetClass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                Intent targetIntent = new Intent(getContext(), target);
                targetIntent.setAction(NotificationData.ACTION_NOTIFICATION);
                if(data.uri != null){
                    targetIntent.setData(Uri.parse(data.uri));
                }
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
                stackBuilder.addParentStack(SplashActivity.class);
                stackBuilder.addNextIntent(targetIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(resultPendingIntent);
            }
            mManager.notify(data.id, builder.build());

        }
    }

    public void cancelNotification(Intent intent){
        if(intent != null){
            cancelNotification(intent.getIntExtra(KEY_ID, INVALID_VALUE));
        }
    }
    public void cancelNotification(int id){
        mManager.cancel(id);
    }

    public void cancelAllNotification(){
        mManager.cancelAll();
    }
}
