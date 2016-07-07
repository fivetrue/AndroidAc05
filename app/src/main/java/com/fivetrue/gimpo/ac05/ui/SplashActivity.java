package com.fivetrue.gimpo.ac05.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.analytics.Event;
import com.fivetrue.gimpo.ac05.analytics.GoogleAnalytics;
import com.fivetrue.gimpo.ac05.manager.NaverApiManager;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.ConfigRequest;
import com.fivetrue.gimpo.ac05.net.request.DistrictDataReqeust;
import com.fivetrue.gimpo.ac05.net.request.RegisterUserRequest;
import com.fivetrue.gimpo.ac05.parser.NaverUserInfoParser;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.service.notification.NotificationHelper;
import com.fivetrue.gimpo.ac05.utils.AppUtils;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.view.CircleImageView;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;
import com.fivetrue.gimpo.ac05.vo.config.Token;
import com.fivetrue.gimpo.ac05.vo.user.District;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

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

    private static final int REQUEST_LOGIN_CODE = 0x33;

    private static final int RETRY_COUNT = 3;

    private TextView mLoadingMessage = null;

    private ProgressBar mProgress = null;
    private LinearLayout mUserLayout = null;

    private CircleImageView mUserImage = null;
    private TextView mUserNickname = null;

    private ConfigRequest mConfigReqeust = null;
    private RegisterUserRequest mRegisterUserRequest = null;
    private ConfigPreferenceManager mConfigPref = null;
    private DistrictDataReqeust mDistrictDataRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initData();
        initView();
        getApplicationConfig();
    }

    private void initView(){
        setActionbarVisible(false);
        mLoadingMessage = (TextView) findViewById(R.id.tv_splash_loading);

        mUserLayout = (LinearLayout) findViewById(R.id.layout_splash_user_info);
        mUserNickname = (TextView) findViewById(R.id.tv_splash_user);
        mUserImage = (CircleImageView) findViewById(R.id.iv_splash_user);

        mProgress = (ProgressBar) findViewById(R.id.pb_splash);
        mProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent)
                , android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mConfigReqeust = new ConfigRequest(this, onConfigResponseListener);
        mRegisterUserRequest = new RegisterUserRequest(this, onRegisterUserInfoResponse);
        mDistrictDataRequest = new DistrictDataReqeust(this, districtApiResponse);
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
        mLoadingMessage.setText(R.string.config_data_register);
        if(appConfig != null){
            Log.i(TAG, "appConfig = " + appConfig.toString());
            Log.i(TAG, "registerDevice: init NaverApiManager start");
            NaverApiManager.init(SplashActivity.this, appConfig.getNaverClientId(), appConfig.getNaverClientSecret(),
                    getString(R.string.app_name));

            String regId = mConfigPref.getGcmDeviceId();
            if(regId != null){
                loginNaver();
            }else{
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        String regId = null;
                        if(params != null && params.length > 0){
                            String senderId = params[0];
                            GoogleCloudMessaging gcm =  GoogleCloudMessaging.getInstance(getApplicationContext());
                            try {
                                regId = gcm.register(senderId);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return regId;
                    }

                    @Override
                    protected void onPostExecute(String value) {
                        super.onPostExecute(value);
                        if(value != null){
                            mConfigPref.setGcmDeviceId(value);
                            loginNaver();
                        }else{
                            Log.e(TAG, "registerDevice Gcm register error");
                        }

                    }
                }.execute(appConfig.getSenderId());
            }
        }else{
            //TODO : AppCOnfig error
            Log.e(TAG, "registerDevice AppConfig is null");
        }
    }

    /**
     * 3. login to Naver
     */
    private void loginNaver(){
        Log.i(TAG, "loginNaver: start");
        mLoadingMessage.setText(R.string.config_data_auto_login);
        startActivityForResult(new Intent(this, NaverLoginActivity.class), REQUEST_LOGIN_CODE);
    }

    private BaseApiResponse<AppConfig> onConfigResponseListener = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<AppConfig>() {

        private int mRetryCount = 0;

        @Override
        public void onResponse(BaseApiResponse<AppConfig> response) {
            if(response != null){
                Log.i(TAG, "onConfigResponse onResponse : " + response.toString());
                mLoadingMessage.setText(R.string.config_check_app_version);
                if(response.getData() != null){
                    getApp().setAppConfig(response.getData());
                    final AppConfig config = response.getData();
                    int lastestVersion = config.getAppVersionCode();
                    int appVersion = AppUtils.getApplicationVersionCode(SplashActivity.this);
                    if(lastestVersion > appVersion && config.getForceUpdate() > 0){
                        new AlertDialog.Builder(SplashActivity.this)
                                .setTitle(R.string.notice)
                                .setMessage(R.string.config_force_update)
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        AppUtils.goAppStore(SplashActivity.this, config);
                                    }
                                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                AppUtils.goAppStore(SplashActivity.this, config);

                            }
                        }).create().show();
                    }else{
                        NetworkManager.getInstance().request(mDistrictDataRequest);
                    }
                }
            }else{
                if(RETRY_COUNT > mRetryCount){
                    NetworkManager.getInstance().request(mConfigReqeust);
                    mRetryCount++;
                }else{
                   finishError();
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            if(RETRY_COUNT > mRetryCount){
                NetworkManager.getInstance().request(mConfigReqeust);
                mRetryCount++;
            }else{
                finishError();
            }
        }
    }, new TypeToken<AppConfig>(){}.getType());

    private void finishError(){
        Toast.makeText(SplashActivity.this, R.string.config_data_can_not_read, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500L);
    }

    private void getUserProfile(Token token){
        Log.i(TAG, "getUserProfile: token = " + token.toString());
        /**
         * 유저 정보 등록
         */
        mLoadingMessage.setText(R.string.config_user_info_register);
        NaverApiManager.getInstance().reqeustUserProfile(new NaverApiManager.OnRequestResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "Userinfo  onRequest response = " + response);
                UserInfo naverUserInfo = NaverUserInfoParser.parse(response);
                Log.i(TAG, "Userinfo  onRequest userInfo = " + naverUserInfo.toString());
                naverUserInfo.setGcmId(mConfigPref.getGcmDeviceId());
                naverUserInfo.setDevice(Build.MODEL);
                mRegisterUserRequest.setObject(naverUserInfo);
                NetworkManager.getInstance().request(mRegisterUserRequest);
            }
        });
    }


    private void startApplication(final UserInfo naverUserInfo){
        Log.i(TAG, "startApplication: start");
        mLoadingMessage.setVisibility(View.GONE);
        mProgress.setVisibility(View.GONE);
        if(naverUserInfo != null){
            Log.i(TAG, "startApplication: naverUserInfo = " + naverUserInfo.toString());
            mUserNickname.setText(naverUserInfo.getName());
            mUserImage.setImageUrl(naverUserInfo.getProfileImage());

            AlphaAnimation anim = new AlphaAnimation(0f, 1f);
            anim.setDuration(1000L);

            mUserLayout.setAnimation(anim);
            mUserLayout.setVisibility(View.VISIBLE);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    Log.i(TAG, "startApplication: animation start");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.i(TAG, "startApplication: animation end");
                    mUserLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startMainActivity(naverUserInfo);
                        }
                    }, 1500L);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    private void startMainActivity(UserInfo info){
        Log.i(TAG, "startMainActivity: start");
        Intent intent = null;
        if(getIntent() != null
                && getIntent().getAction() != null
                && getIntent().getAction().equals(NotificationHelper.ACTION_NOTIFICATION)){
            GoogleAnalytics.getInstance().sendLogEventProperties(Event.EnterSplashActivity_Notification.addParams("data", getIntent().getDataString()));
            intent = new Intent(this, MainActivity.class);
            intent.setData(getIntent().getData());
            intent.putExtra(NotificationHelper.KEY_NOTIFICATION_PARCELABLE, getIntent().getParcelableExtra(NotificationHelper.KEY_NOTIFICATION_PARCELABLE));

        }else{
            intent = new Intent(this, MainActivity.class);
        }
        GoogleAnalytics.getInstance().setUserId(info.getEmail());
        intent.putExtra(UserInfo.class.getName(), info);
        startActivity(intent);
        finish();
    }

    @Override
    protected void checkAppConfig() {
//        super.checkAppConfig();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void failedLogin(){
        Toast.makeText(this, R.string.fail_auth_user_info, Toast.LENGTH_SHORT).show();
        GoogleAnalytics.getInstance().sendLogEventProperties(Event.EnterSplashActivity_LoginFailed);
        getBaseLayoutContainer().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000L);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_LOGIN_CODE){
                if(data != null){
                    Token token = data.getParcelableExtra(Token.class.getName());
                    getUserProfile(token);
                }
            }
        }else{
            failedLogin();
        }
    }

    private BaseApiResponse<UserInfo> onRegisterUserInfoResponse = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<UserInfo>() {

        private int mRetryCount = 0;

        @Override
        public void onResponse(BaseApiResponse<UserInfo> response) {
            Log.i(TAG, "onRegisterUserInfoResponse onResponse: " + response.toString());
            if(response != null && response.getData() != null){
                final UserInfo userInfo = response.getData();
                mConfigPref.setUserInfo(userInfo);
                startApplication(userInfo);
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
            }else{
                Log.i(TAG, "onRegisterUserInfoResponse error : " + error.toString());
                startApplication(mConfigPref.getUserInfo());
            }
        }
    }, new TypeToken<UserInfo>(){}.getType());

    private BaseApiResponse<ArrayList<District>> districtApiResponse = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<ArrayList<District>>() {
        private int mRetry = 0;
        @Override
        public void onResponse(BaseApiResponse<ArrayList<District>> response) {
            if(response != null){
                getApp().setDistricts(response.getData());
                registerDevice(getApp().getAppConfig());
            }else{
                if(BaseApiResponse.RETRY_COUNT > mRetry){
                    NetworkManager.getInstance().request(mDistrictDataRequest);
                    mRetry++;
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            if(BaseApiResponse.RETRY_COUNT > mRetry){
                NetworkManager.getInstance().request(mDistrictDataRequest);
                mRetry++;
            }
        }
    }, new TypeToken<ArrayList<District>>(){}.getType());
}
