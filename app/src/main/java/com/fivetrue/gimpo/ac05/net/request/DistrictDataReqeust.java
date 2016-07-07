package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class DistrictDataReqeust extends BasicRequest {

    private static final String API = Constants.API_SERVER_HOST + "/api/district/get";


    public DistrictDataReqeust(Context context, BaseApiResponse response) {
        super(context, API, response);
    }

    public void setType(String type){
        getParams().put("type", type);
    }

    @Override
    public boolean isCache() {
        return true;
    }

    @Override
    public long getCacheTimeMilliseconds() {
        return 1000 * 60 * 60 * 24;
    }
}
