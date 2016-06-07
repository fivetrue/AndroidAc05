package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.manager.NaverApiManager;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.ConfigRequest;
import com.fivetrue.gimpo.ac05.net.request.RegisterUserRequest;
import com.fivetrue.gimpo.ac05.parser.NaverUserInfoParser;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;
import com.fivetrue.gimpo.ac05.vo.naver.NaverUserInfo;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * 1. get Application config from server
 * 2. Register GCM
 * 3. Login to Naver
 * 4. getNaver Userinfo
 * 5. register UserInfo to Server
 * 6. start application
 */
public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    private static final int RETRY_COUNT = 3;

    private ConfigRequest mConfigReqeust = null;
    private RegisterUserRequest mRegisterUserRequest = null;
    private ConfigPreferenceManager mConfigPref = null;

    private TextView mTvLoading = null;
    private ProgressBar mProgress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initModels();
        getApplicationConfig();
    }

    private void initView(){
        mTvLoading = (TextView) findViewById(R.id.tv_splash_loading);
    }

    private void initModels(){
        mConfigPref = new ConfigPreferenceManager(this);
        mConfigReqeust = new ConfigRequest(this, onConfigResponseListener);
        mRegisterUserRequest = new RegisterUserRequest(this, onRegisterUserRequest);
    }

    /**
     * 1. get application config from server
     */
    private void getApplicationConfig(){
        Log.i(TAG, "getApplicationConfig : start");
        NetworkManager.getInstance().request(mConfigReqeust);
    }

    /**
     * 2. do register device after getting appconfig
     * @param appConfig
     */
    private void registerDevice(final AppConfig appConfig){
        Log.i(TAG, "registerDevice: start");
        mTvLoading.setText(R.string.config_data_register);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String regId = mConfigPref.getGcmDeviceId();
                if(appConfig != null){
                    Log.i(TAG, "appConfig = " + appConfig.toString());
                    ((ApplicationEX)getApplicationContext()).setAppConfig(appConfig);
                    if(regId == null && appConfig.getSenderId() != null){
                        GoogleCloudMessaging gcm =  GoogleCloudMessaging.getInstance(getApplicationContext());
                        try {
                            regId = gcm.register(appConfig.getSenderId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return regId;
            }

            @Override
            protected void onPostExecute(String value) {
                super.onPostExecute(value);
                if(value != null){
                    mConfigPref.setGcmDeviceId(value);
                    NaverApiManager.init(SplashActivity.this, appConfig.getNaverClientId(), appConfig.getNaverClientSecret(),
                            getString(R.string.app_name));
                    loginNaver();
                }else{
                    //TODO : gcm error
                }
            }
        }.execute();
    }

    /**
     * 3. login to Naver
     */
    private void loginNaver(){
        Log.i(TAG, "loginNaver: start");
        mTvLoading.setText(R.string.config_data_auto_login);
        NaverApiManager.getInstance().startOauthLoginActivity(SplashActivity.this, onOAuthLoginListener);
    }

    private BaseApiResponse.OnResponseListener<AppConfig> onConfigResponseListener = new BaseApiResponse.OnResponseListener<AppConfig>() {

        private int mRetryCount = 0;

        @Override
        public void onResponse(BaseApiResponse<AppConfig> response) {
            if(response != null){
                Log.i(TAG, "onConfigResponse onResponse : " + response.toString());
                registerDevice(response.getData());
            }else{
                if(RETRY_COUNT > mRetryCount){
                    NetworkManager.getInstance().request(mConfigReqeust);
                    mRetryCount++;
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            if(RETRY_COUNT > mRetryCount){
                NetworkManager.getInstance().request(mConfigReqeust);
                mRetryCount++;
            }
        }
    };

    private NaverApiManager.OnOAuthLoginListener onOAuthLoginListener = new NaverApiManager.OnOAuthLoginListener() {
        @Override
        public void onSuccess(NaverApiManager apiManager, String accessToken, String refreshToken, long expiresAt, String tokenType, String state) {
            Log.i(TAG, "Login onSuccess accessToken = " + accessToken + " / refreshToken = " + refreshToken
                    + " / expiresAt = " + expiresAt
                    + " / state = " + state
                    + " / tokenType = " + tokenType);

            apiManager.reqeustUserProfile(new NaverApiManager.OnRequestResponseListener() {
                @Override
                public void onResponse(String response) {
                    mTvLoading.setText(R.string.config_user_info_register);
                    Log.i(TAG, "Userinfo  onRequest response = " + response);
                    NaverUserInfo userInfo = NaverUserInfoParser.parse(response);
                    Log.i(TAG, "Userinfo  onRequest userInfo = " + userInfo.toString());
                    userInfo.setGcmId(mConfigPref.getGcmDeviceId());
                    userInfo.setDevice(Build.MODEL);
                    mRegisterUserRequest.setNaverUserInfo(userInfo);
                    NetworkManager.getInstance().request(mRegisterUserRequest);
                }
            });
        }

        @Override
        public void onError(NaverApiManager apiManager, String errorCode, String errorDescription) {
            Log.i(TAG, "errorCode = " + errorCode + " / errorDesc = " + errorDescription);
        }
    };

    private BaseApiResponse.OnResponseListener<NaverUserInfo> onRegisterUserRequest = new BaseApiResponse.OnResponseListener<NaverUserInfo>() {

        private int mRetryCount = 0;

        @Override
        public void onResponse(BaseApiResponse<NaverUserInfo> response) {
            Log.i(TAG, "onRegisterUserRequest onResponse: " + response.toString());
            if(response != null && response.getData() != null){
                mTvLoading.setText(response.getData().getNickname() + " 로그인 하였습니다 ");
                mTvLoading.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startApplication();
                    }
                }, 300L);
            }else{
                if(RETRY_COUNT > mRetryCount){
                    NetworkManager.getInstance().request(mRegisterUserRequest);
                    mRetryCount++;
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            if(RETRY_COUNT > mRetryCount){
                NetworkManager.getInstance().request(mRegisterUserRequest);
                mRetryCount++;
            }
        }
    };

    private void startApplication(){
        mTvLoading.setText(R.string.config_data_auto_login);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
