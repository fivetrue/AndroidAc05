package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.BasicRequest;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfo;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfoEntry;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class ImageInfoEntryDataRequest extends BasicRequest<ArrayList<ImageInfoEntry>> {

    private static final String API = Constants.API_SERVER_HOST + "/api/data/imageEntry";


    private boolean mCached = true;
    private long mCacheTime = 1000 * 60 * 60;

    public ImageInfoEntryDataRequest(Context context, BaseApiResponse.OnResponseListener<ArrayList<ImageInfoEntry>> response) {
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
        return new TypeToken<ArrayList<ImageInfoEntry>>(){}.getType();
    }
}
