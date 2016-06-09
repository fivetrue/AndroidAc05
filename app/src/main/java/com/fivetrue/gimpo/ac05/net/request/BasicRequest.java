package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.net.BaseApiRequest;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.utils.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 17..
 */
public abstract class BasicRequest<T> extends BaseApiRequest {

    private static final String TAG = "BasicRequest";

    private static final String KEY_APP_ID = "Application-ID";
    private static final String KEY_APP_KEY = "Application-Key";

    private BaseApiResponse.OnResponseListener<T> mOnResponseListener = null;


    public BasicRequest(Context context, String url, BaseApiResponse.OnResponseListener<T> responseListener) {
        super(context, url, null);
        setResponse(baseApiResponse);
        mOnResponseListener = responseListener;
        getHeaders().put(KEY_APP_ID, getContext().getPackageName());
        getHeaders().put(KEY_APP_KEY, getContext().getString(R.string.fivetrue_app_key));
    }

    private BaseApiResponse<T> baseApiResponse = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<T>() {
        @Override
        public void onResponse(BaseApiResponse<T> response) {
            if(mOnResponseListener != null){
                mOnResponseListener.onResponse(response);
            }
        }

        @Override
        public void onError(VolleyError error) {
            if(mOnResponseListener != null){
                mOnResponseListener.onError(error);
            }
        }
    }, getClassType());


    protected abstract Type getClassType();

    public void setObject(T object){
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
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
