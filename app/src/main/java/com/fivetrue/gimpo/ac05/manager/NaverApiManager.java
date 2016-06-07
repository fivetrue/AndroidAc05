package com.fivetrue.gimpo.ac05.manager;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.fivetrue.gimpo.ac05.BuildConfig;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginDefine;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.data.OAuthErrorCode;

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

    private OAuthLogin mOAuthLogin = null;

    private String mClientId = null;
    private String mClientSecret = null;
    private String mApplicationName = null;

    private OnErrorCodeListener mOnErrorCodeListener = null;
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

        OAuthLoginDefine.DEVELOPER_VERSION = BuildConfig.DEBUG;

        this.mContext = context;
        mOAuthLogin = OAuthLogin.getInstance();

        mClientId = clientId;
        mClientSecret = clientSecret;
        mApplicationName = applicationName;

        mOAuthLogin.init(mContext, mClientId, mClientSecret, mApplicationName);
    }

    public OAuthLogin getOAuthLogin(){
        return mOAuthLogin;
    }

    public void startOauthLoginActivity(Activity activity, OnOAuthLoginListener ll){
        mOnErrorCodeListener = ll;
        mOAuthLogin.startOauthLoginActivity(activity, mOAuthLoginHandler);
    }

    public void logout(){
        mOAuthLogin.logout(mContext);
    }

    public void reqeustUserProfile(OnRequestResponseListener ll){
        mOnRequestResponseListener = ll;
        new RequestAPITask().execute(API_USER_PROFILE);
    }

    public void requestRefreshAccessToken(OnOAuthLoginListener ll){
        mOnErrorCodeListener = ll;
        new RefreshTokenTask().execute();
    }

    public void requestDeleteAccessToken(OnErrorCodeListener ll){
        mOnErrorCodeListener = ll;
        new DeleteTokenTask().execute();
    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {

        @Override
        public void run(boolean b) {
            if (b) {
                String accessToken = mOAuthLogin.getAccessToken(mContext);
                String refreshToken = mOAuthLogin.getRefreshToken(mContext);
                long expiresAt = mOAuthLogin.getExpiresAt(mContext);
                String tokenType = mOAuthLogin.getTokenType(mContext);
                String state = mOAuthLogin.getState(mContext).toString();
                Log.i(TAG, "accessToken : " + accessToken);
                Log.i(TAG, "refreshToken : " + refreshToken);
                Log.i(TAG, "expiresAt : " + expiresAt);
                Log.i(TAG, "tokenType : " + tokenType);
                Log.i(TAG, "state : " + state);
                if(mOnErrorCodeListener != null && mOnErrorCodeListener instanceof OnOAuthLoginListener){
                    ((OnOAuthLoginListener)mOnErrorCodeListener).onSuccess(NaverApiManager.this, accessToken, refreshToken, expiresAt,
                            tokenType, state);
                }
            } else {
                String errorCode = mOAuthLogin.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLogin.getLastErrorDesc(mContext);
                Log.i(TAG, "errorCode:" + errorCode + ", errorDesc:" + errorDesc);
                if(mOnErrorCodeListener != null){
                    mOnErrorCodeListener.onError(NaverApiManager.this, errorCode, errorDesc);
                }
            }
            mOnErrorCodeListener = null;
        }
    };

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
                String at = mOAuthLogin.getAccessToken(mContext);
                result = mOAuthLogin.requestApi(mContext, at, api);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            onRequestResponseListener.onResponse(s);
        }
    }

    private class RefreshTokenTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            String result = mOAuthLogin.refreshAccessToken(mContext);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null){
                String refreshToken = mOAuthLogin.getRefreshToken(mContext);
                long expiresAt = mOAuthLogin.getExpiresAt(mContext);
                String tokenType = mOAuthLogin.getTokenType(mContext);
                String state = mOAuthLogin.getState(mContext).toString();
                Log.i(TAG, "refresh accessToken : " + s);
                Log.i(TAG, "refresh refreshToken : " + refreshToken);
                Log.i(TAG, "refresh expiresAt : " + expiresAt);
                Log.i(TAG, "refresh tokenType : " + tokenType);
                Log.i(TAG, "refresh state : " + state);
                if(mOnErrorCodeListener != null && mOnErrorCodeListener instanceof OnOAuthLoginListener){
                    ((OnOAuthLoginListener)mOnErrorCodeListener).onSuccess(NaverApiManager.this, refreshToken, refreshToken, expiresAt,
                            tokenType, state);
                }
            }
            mOnErrorCodeListener = null;
        }
    }

    private class DeleteTokenTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return mOAuthLogin.logoutAndDeleteToken(mContext);
        }

        @Override
        protected void onPostExecute(Boolean v) {
            if(v != null){
                OAuthErrorCode errorCode = mOAuthLogin.getLastErrorCode(mContext);
                String errorDesc = mOAuthLogin.getLastErrorDesc(mContext);
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorDesc:" + errorDesc);
                Log.i(TAG, "successDeleteToken = " + v);
                if(mOnErrorCodeListener != null){
                    mOnErrorCodeListener.onError(NaverApiManager.this, errorCode.getCode()
                            , errorDesc);
                }
            }
        }
    }
}
