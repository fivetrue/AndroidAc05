package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class ImageInfoDataRequest extends BasicRequest {

    private static final String API = Constants.API_SERVER_HOST + "/api/data/image";
//    private static final String API = "http://10.10.5.71:8080/gimpo-ac05/api/data/page";
//    private static final String API = "http://192.168.43.126:8080/gimpo-ac05/api/data/page";
//    private static final String API = "http://192.168.219.168:8080/gimpo-ac05/api/data/page";


    private boolean mCached = true;
    private long mCacheTime = 1000 * 60 * 5;

    public ImageInfoDataRequest(Context context, BaseApiResponse response) {
        super(context, API, response);
    }

//    @Override
//    protected Type getClassType() {
//        return new TypeToken<AppConfig>(){}.getType();
//    }

    public void setCache(boolean b){
        mCached = b;
    }

    @Override
    public long getCacheTimeMilliseconds() {
        return mCacheTime;
    }

    @Override
    public boolean isCache() {
        return mCached;
    }
}
