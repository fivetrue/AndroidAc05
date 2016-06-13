package com.fivetrue.gimpo.ac05.manager;

import android.content.Context;
import android.os.AsyncTask;

import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.nhn.android.naverlogin.OAuthLoginDefine;
import com.nhn.android.naverlogin.connection.CommonConnection;
import com.nhn.android.naverlogin.connection.ResponseData;

/**
 * Created by kwonojin on 16. 6. 2..
 */
public class NaverApiManager {

    private static final String TAG = "NaverApiManager";

    private static final String API_USER_PROFILE = "https://openapi.naver.com/v1/nid/getUserProfile.xml";

    public interface OnErrorCodeListener{
        void onError(NaverApiManager apiManager, String errorCode, String errorDescription);
    }

    public interface OnOAuthLoginListener extends OnErrorCodeListener{
        void onSuccess(NaverApiManager apiManager, String accessToken, String refreshToken,
                       long expiresAt, String tokenType, String state);
    }

    public interface OnRequestResponseListener{
        void onResponse(String response);
    }

    private static NaverApiManager sInstance = null;

    private Context mContext = null;

    private String mClientId = null;
    private String mClientSecret = null;
    private String mApplicationName = null;

    private ConfigPreferenceManager mConfigPref = null;

    private OnRequestResponseListener mOnRequestResponseListener = null;

    public static NaverApiManager getInstance(){
        return sInstance;
    }

    public static void init(Context context, String clientId, String clientSecret,
                            String applicationName){
        sInstance = new NaverApiManager(context, clientId, clientSecret, applicationName);
    }

    private NaverApiManager(Context context, String clientId, String clientSecret,
                            String applicationName){

        if(clientId == null || clientSecret == null || applicationName == null){
            throw new IllegalArgumentException("Naver parameters must be not null");
        }

        this.mContext = context;
        mClientId = clientId;
        mClientSecret = clientSecret;
        mApplicationName = applicationName;

        mConfigPref = new ConfigPreferenceManager(mContext);
    }

    public void logout(){
        mConfigPref.setToken(null);
        mConfigPref.setUserInfo(null);
    }

    public void reqeustUserProfile(OnRequestResponseListener ll){
        mOnRequestResponseListener = ll;
        new RequestAPITask().execute(API_USER_PROFILE);
    }

    private OnRequestResponseListener onRequestResponseListener = new OnRequestResponseListener() {
        @Override
        public void onResponse(String response) {
            Log.d(TAG, "onResponse : " + response);
            if(mOnRequestResponseListener != null){
                mOnRequestResponseListener.onResponse(response);
                mOnRequestResponseListener = null;
            }
        }
    };

    private class RequestAPITask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            if(params != null && params.length > 0){
                Log.i(TAG, "RequestApiTask start ");
                String api = params[0];
                String at = mConfigPref.getToken().getAccess_token();
                result = requestNaverApi(mContext, at, api);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            onRequestResponseListener.onResponse(s);
        }
    }

    private String requestNaverApi(Context context, String accessToken, String url) {
        String authHeader = "bearer " + accessToken;
        if(OAuthLoginDefine.DEVELOPER_VERSION) {
            Log.d("NaverLoginOAuth|OAuthLogin", "at:" + accessToken + ", url:" + url);
            Log.d("NaverLoginOAuth|OAuthLogin", "header:" + authHeader);
        }

        ResponseData res = CommonConnection.request(context, url, (String) null, (String) null, authHeader);
        if(OAuthLoginDefine.DEVELOPER_VERSION) {
            Log.d("NaverLoginOAuth|OAuthLogin", "res.statuscode" + res.mStatusCode);
            Log.d("NaverLoginOAuth|OAuthLogin", "res.content" + res.mContent);
        }

        return res == null?null:res.mContent;
    }
}
