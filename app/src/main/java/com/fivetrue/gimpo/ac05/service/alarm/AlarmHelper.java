package com.fivetrue.gimpo.ac05.service.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.fivetrue.gimpo.ac05.service.BaseServiceHelper;
import com.fivetrue.gimpo.ac05.service.notification.NotificationHelper;
import com.google.gson.Gson;

/**
 * Created by ojin.kwon on 2016-02-25.
 */
public class AlarmHelper extends BaseServiceHelper {

    public static final String KEY_ALARM_PARCELABLE = "alarm_parcelable";
    private static final String ALARM_NAME_PREFIX = AlarmHelper.class.getName() + ".";

    public static final String BEFORE_D_DAY = "d-day";
    public static final String BEFORE_A_WEEK = "a_week";


    private AlarmManager mAlarmManager = null;
    private Gson mGson = null;
    private static AlarmHelper sHelper = null;

    public static AlarmHelper getInstance(Context context){
        if(sHelper == null){
            sHelper = new AlarmHelper(context);
        }
        return sHelper;
    }

    private  AlarmHelper(Context context) {
        super(context);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void createAlarm(Intent intent){
        if(intent != null){
            AlarmData data = intent.getParcelableExtra(KEY_ALARM_PARCELABLE);
            createAlarm(data);
        }
    }

    public void createAlarm(AlarmData data){
        if(data != null){
//            Intent sendIntent = NotificationService.createNotificationIntent(getContext(), data.notificationData);
//            PendingIntent pendingIntent = PendingIntent.getService(getContext(), data.id, sendIntent, PendingIntent.FLAG_NO_CREATE);
//            if(pendingIntent == null){
//                pendingIntent = PendingIntent.getService(getContext(), data.id, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            }
//            mAlarmManager.set(AlarmManager.RTC_WAKEUP, data.dTime, pendingIntent);
//            putData(ALARM_NAME_PREFIX + data.name , data);
        }
    }

    public void cancelAlarm(Intent intent){
        if(intent != null){
            AlarmData data = intent.getParcelableExtra(KEY_ALARM_PARCELABLE);
            cancelAlarm(data);
        }
    }

    public void cancelAlarm(AlarmData data){
        if(data != null){
//            Intent sendIntent = NotificationService.createNotificationIntent(getContext(), data.notificationData);
//            sendIntent.putExtra(NotificationHelper.KEY_NOTIFICATION_PARCELABLE, data.notificationData);
//            PendingIntent pendingIntent = PendingIntent.getService(getContext(), data.id, sendIntent, PendingIntent.FLAG_NO_CREATE);
//            if(pendingIntent == null){
//                pendingIntent = PendingIntent.getService(getContext(), data.id, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            }
//            mAlarmManager.cancel(pendingIntent);
        }
    }


    public AlarmData getAlarmData(String name){
        AlarmData data = null;
        String json = getData(ALARM_NAME_PREFIX + name);
        if(json != null){
            data = getGson().fromJson(json, AlarmData.class);
        }
        return data;
    }

    public AlarmData getBeforeWeekAlarmData(){
        return getAlarmData(BEFORE_A_WEEK);
    }

    public AlarmData getBeforeDayAlarmData(){
        return getAlarmData(BEFORE_D_DAY);
    }

    private void putData(String key, AlarmData data){
        if(key != null && data != null){
            String json = getGson().toJson(data);
            byte[] bytes = Base64.encode(json.getBytes(), Base64.DEFAULT);
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(key, new String(bytes)).commit();
        }
    }

    private String getData(String key){
        String str = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(key, null);
        if(str != null){
            byte[] data = Base64.decode(str, Base64.DEFAULT);
            str = new String(data);
        }
        return str;
    }

    private Gson getGson(){
        if(mGson == null){
            mGson = new Gson();
        }
        return mGson;
    }
}
