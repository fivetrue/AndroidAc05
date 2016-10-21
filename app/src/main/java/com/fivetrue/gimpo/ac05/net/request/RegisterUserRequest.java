package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.BasicRequest;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 29..
 */
public class RegisterUserRequest extends BasicRequest<User> {

    private static final String TAG = "RegisterUserRequest";

    private static final String API = Constants.API_SERVER_HOST + "/api/user/register";


    public RegisterUserRequest(Context context, BaseApiResponse.OnResponseListener<User> response) {
        super(context, API, response);
    }

    @Override
    protected Type getClassType() {
        return new TypeToken<User>(){}.getType();
    }
}
