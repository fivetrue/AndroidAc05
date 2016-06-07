package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;
import android.os.Build;

import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.vo.naver.NaverUserInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 29..
 */
public class RegisterUserRequest extends BasicRequest<NaverUserInfo> {

    private static final String API = Constants.API_SERVER_HOST + "/api/user/register";


    public RegisterUserRequest(Context context, BaseApiResponse.OnResponseListener<NaverUserInfo> responseListener) {
        super(context, API, responseListener);
    }

    @Override
    protected Type getClassType() {
        return new TypeToken<NaverUserInfo>(){}.getType();
    }

    public void setNaverUserInfo(NaverUserInfo userInfo){
        if(userInfo != null){
            Field[] fields = userInfo.getClass().getDeclaredFields();
            for(Field f : fields){
                f.setAccessible(true);
                try {
                    getParams().put(f.getName(), (String)f.get(userInfo));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
