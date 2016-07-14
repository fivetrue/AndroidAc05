package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class NoticeDataRequest extends BasicRequest {

    private static final String API = Constants.API_SERVER_HOST + "/api/data/noti";

    private boolean mCached = true;
    private long mCacheTime = 1000 * 60 * 10;

    public NoticeDataRequest(Context context, BaseApiResponse response) {
        super(context, API, response);
    }

    public void setType(String type){
        getParams().put("type", type);
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
}
