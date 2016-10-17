package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.BasicRequest;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.vo.user.FirebaseUserInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 29..
 */
public class AdminUpdateDataRequest extends BasicRequest<String> {

    private static final String TAG = "AdminUpdateDataRequest";

    private static final String API = Constants.API_SERVER_HOST + "/api/data/update";

    private static final int CACHE_TIME = 1000 * 60 * 30;


    public AdminUpdateDataRequest(Context context, BaseApiResponse.OnResponseListener<String> response) {
        super(context, API, response);
    }


    @Override
    protected Type getClassType() {
        return new TypeToken<String>(){}.getType();
    }

    @Override
    public boolean isCache() {
        return true;
    }

    @Override
    public long getCacheTimeMilliseconds() {
        return CACHE_TIME;
    }
}
