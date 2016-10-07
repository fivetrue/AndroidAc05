package com.fivetrue.gimpo.ac05.net.response;


import android.text.TextUtils;

import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.vo.config.Token;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

/**
 * Created by kwonojin on 16. 3. 15..
 */
public class TokenApiResponse extends BaseApiResponse<Token> {

    public TokenApiResponse(OnResponseListener<Token> listener) {
        super(listener, new TypeToken<Token>(){}.getType());
    }

    @Override
    public void setResponse(JSONObject response){
        if(response != null){
            if(!TextUtils.isEmpty(response.toString()) && getType() != null){
                Token data = getGson().fromJson(response.toString(), getType());
                setData(data);
            }
            super.setResponse(null);
        }
    }

    @Override
    protected String getDataRootKey() {
        return "error";
    }
}
