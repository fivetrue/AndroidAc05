package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.net.BaseApiRequest;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.utils.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 17..
 */
public abstract class BasicRequest extends BaseApiRequest {

    private static final String TAG = "BasicRequest";

    private static final String KEY_APP_ID = "Application-ID";
    private static final String KEY_APP_KEY = "Application-Key";

    public BasicRequest(Context context, String url, BaseApiResponse response) {
        super(context, url, response);
        getHeaders().put(KEY_APP_ID, getContext().getPackageName());
        getHeaders().put(KEY_APP_KEY, getContext().getString(R.string.fivetrue_app_key));
    }

    public BasicRequest(Context context, int method, String url, BaseApiResponse response) {
        super(context, method, url, response);
        getHeaders().put(KEY_APP_ID, getContext().getPackageName());
        getHeaders().put(KEY_APP_KEY, getContext().getString(R.string.fivetrue_app_key));
    }

    public void setObject(Object object){
        if(object != null){
            Field[] fields = object.getClass().getDeclaredFields();
            for(Field f : fields){
                f.setAccessible(true);
                try {
                    Object value = f.get(object);
                    String key = f.getName();
                    if(value != null && value instanceof String){
                        Log.i(TAG, "setObject: key / value = " + key + " / " + value.toString());
                        getParams().put(key, (String) value);
                    }else{
                        getParams().put(key, String.valueOf(value));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
