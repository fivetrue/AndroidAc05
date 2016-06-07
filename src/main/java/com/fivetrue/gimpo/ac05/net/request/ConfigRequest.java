package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class ConfigRequest extends BasicRequest<AppConfig> {

    private static final String API = Constants.API_SERVER_HOST + "/api/config/appconfig";

    public ConfigRequest(Context context, BaseApiResponse.OnResponseListener<AppConfig> responseListener) {
        super(context, API, responseListener);
    }

    @Override
    protected Type getClassType() {
        return new TypeToken<AppConfig>(){}.getType();
    }

    @Override
    public long getCacheTimeMilliseconds() {
        return 1000 * 60 * 5;
    }

    @Override
    public boolean isCache() {
        return true;
    }
}
