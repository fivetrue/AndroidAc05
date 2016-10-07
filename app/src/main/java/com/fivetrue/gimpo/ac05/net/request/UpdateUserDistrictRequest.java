package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.BasicRequest;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 29..
 */
public class UpdateUserDistrictRequest extends BasicRequest<UserInfo> {

    private static final String TAG = "UpdateUserDistrictRequest";

    private static final String API = Constants.API_SERVER_HOST + "/api/user/update/district";


    public UpdateUserDistrictRequest(Context context, BaseApiResponse.OnResponseListener<UserInfo> response) {
        super(context, API, response);
    }


    @Override
    protected Type getClassType() {
        return new TypeToken<UserInfo>(){}.getType();
    }
}
