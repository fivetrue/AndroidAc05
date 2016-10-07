package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.BasicRequest;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class ImageInfoDataRequest extends BasicRequest<ArrayList<ImageInfo>> {

    private static final String API = Constants.API_SERVER_HOST + "/api/data/image";


    private boolean mCached = true;
    private long mCacheTime = 1000 * 60 * 60;

    public ImageInfoDataRequest(Context context, BaseApiResponse.OnResponseListener<ArrayList<ImageInfo>> response) {
        super(context, API, response);
    }


    @Override
    public long getCacheTimeMilliseconds() {
        return mCacheTime;
    }

    @Override
    public boolean isCache() {
        return mCached;
    }

    @Override
    protected Type getClassType() {
        return new TypeToken<ArrayList<ImageInfo>>(){}.getType();
    }
}
