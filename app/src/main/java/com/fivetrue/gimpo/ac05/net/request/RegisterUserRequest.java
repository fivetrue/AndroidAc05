package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 29..
 */
public class RegisterUserRequest extends BasicRequest {

    private static final String TAG = "RegisterUserRequest";

    private static final String API = Constants.API_SERVER_HOST + "/api/user/register";


    public RegisterUserRequest(Context context, BaseApiResponse response) {
        super(context, API, response);
    }

//    @Override
//    protected Type getClassType() {
//        return new TypeToken<UserInfo>(){}.getType();
//    }

}
