package com.fivetrue.gimpo.ac05.service.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.service.BaseServiceHelper;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;


/**
 * Created by ojin.kwon on 2016-02-25.
 */
public class NotificationHelper extends BaseServiceHelper {

    public static final String ACTION_NOTIFICATION = NotificationData.class.getName() + ".notification";

    public static final String KEY_NOTIFICATION_PARCELABLE = "noti_parcelable";

    private NotificationManager mManager = null;

    public NotificationHelper(Context context){
        super(context);
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void createNotification(final NotificationData data){
        if(data != null && data.getId() > INVALID_VALUE){
            if(data.getImageUrl() != null){
                new Handler(getContext().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ImageLoadManager.getInstance().loadImageUrl(data.getImageUrl(), new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                if (response != null && response.getBitmap() != null && !response.getBitmap().isRecycled()) {
                                    makeNotification(getContext(), data, response.getBitmap());
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                makeNotification(getContext(), data, null);
                            }
                        });
                    }
                });
            }else{
                makeNotification(getContext(), data, null);
            }
        }
    }

    private void makeNotification(Context context, NotificationData data, Bitmap image){
        if(data != null){
            String title =TextUtils.isEmpty(data.getTitle()) ? context.getString(R.string.app_name) : data.getTitle();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
            builder.setSmallIcon(R.drawable.push_icon)
                    .setContentTitle(title)
                    .setContentText(data.getMessage())
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                    .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                    .setAutoCancel(true);

            if(image != null && !image.isRecycled()){
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                bigPictureStyle.bigPicture(image)
                        .setSummaryText(data.getMessage())
                        .setBigContentTitle(title);
                builder.setStyle(bigPictureStyle);
            }else{
                if(data.getMessage().contains("\n")){
                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                    String[] message = data.getMessage().split("\n");
                    inboxStyle.setBigContentTitle(data.getTitle());
                    for(String m : message){
                        inboxStyle.addLine(m);
                    }
                    builder.setStyle(inboxStyle);
                }else{
                    NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                    bigTextStyle.setBigContentTitle(title)
                            .setSummaryText(data.getMessage());
                    builder.setStyle(bigTextStyle);
                }
            }

            if(data.getTargetClass() != null){
                Class<?> target = null;
                try {
                    target = Class.forName(data.getTargetClass());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                Intent targetIntent = new Intent(getContext(), target);
                targetIntent.setAction(ACTION_NOTIFICATION);
                if(data.getUri() != null){
                    targetIntent.setData(Uri.parse(data.getUri()));
                }
                targetIntent.putExtra(KEY_NOTIFICATION_PARCELABLE, data);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(resultPendingIntent);
            }
            mManager.notify(data.getId(), builder.build());

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
