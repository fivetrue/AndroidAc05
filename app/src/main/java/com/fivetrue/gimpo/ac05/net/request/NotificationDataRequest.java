package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.BasicRequest;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class NotificationDataRequest extends BasicRequest<ArrayList<NotificationData>> {

    private static final String API = Constants.API_SERVER_HOST + "/api/data/noti";

    private boolean mCached = true;
    private long mCacheTime = 1000 * 60 * 10;

    public NotificationDataRequest(Context context, BaseApiResponse.OnResponseListener<ArrayList<NotificationData>> response) {
        super(context, API, response);
        putParam("type", "0");
    }

    @Override
    public boolean isCache() {
        return mCached;
    }

    public void setCache(boolean b){
        mCached = b;
    }

    @Override
    public long getCacheTimeMilliseconds() {
        return mCacheTime;
    }

    @Override
    protected Type getClassType() {
        return new TypeToken<ArrayList<NotificationData>>(){}.getType();
    }
}
