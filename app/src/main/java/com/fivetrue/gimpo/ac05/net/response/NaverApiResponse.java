package com.fivetrue.gimpo.ac05.net.response;


import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

/**
 * Created by kwonojin on 16. 3. 15..
 */
public class NaverApiResponse extends BaseApiResponse<NaverApiResponse.ErrorCode> {

    public NaverApiResponse(BaseApiResponse.OnResponseListener<NaverApiResponse.ErrorCode> listener) {
        super(listener, new TypeToken<ErrorCode>(){}.getType());
    }

    protected static final String KEY_MESSAGE = "message";
    protected static final String KEY_ERROR_CODE = "error_code";
    protected static final String KEY_STATUS = "status";

    private int status = 0;
    private int naverErrorCode = 0;

    @Override
    public void setResponse(JSONObject response){
        if(response != null){
            JSONObject jMessage = response.optJSONObject(KEY_MESSAGE);
            naverErrorCode = response.optInt(KEY_ERROR_CODE);
            if(jMessage != null){
                status = jMessage.optInt(KEY_MESSAGE);
            }
            super.setResponse(response);
        }
    }

    @Override
    protected String getDataRootKey() {
        return "error";
    }

    public int getStatus() {
        return status;
    }

    public int getNaverErrorCode(){
        return naverErrorCode;
    }

    public static final class ErrorCode{
        public String code = null;
        public String msg = null;
    }

}
