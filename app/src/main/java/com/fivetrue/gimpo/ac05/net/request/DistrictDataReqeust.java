package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.BasicRequest;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.vo.user.District;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class DistrictDataReqeust extends BasicRequest<ArrayList<District>> {

    private static final String API = Constants.API_SERVER_HOST + "/api/district/get";


    public DistrictDataReqeust(Context context, BaseApiResponse.OnResponseListener<ArrayList<District>> response) {
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

    @Override
    protected Type getClassType() {
        return new TypeToken<ArrayList<District>>(){}.getType();
    }
}
