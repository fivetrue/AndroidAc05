package com.fivetrue.gimpo.ac05.ui;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.fivetrueandroid.google.GoogleLoginUtil;
import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.NetworkManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.utils.AppUtils;
import com.fivetrue.fivetrueandroid.utils.SimpleViewUtils;
import com.fivetrue.fivetrueandroid.view.CircleImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.chatting.FirebaseChattingService;
import com.fivetrue.gimpo.ac05.net.request.ConfigRequest;
import com.fivetrue.gimpo.ac05.net.request.DistrictDataReqeust;
import com.fivetrue.gimpo.ac05.net.request.RegisterUserRequest;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.service.notification.NotificationHelper;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;
import com.fivetrue.gimpo.ac05.vo.user.District;
import com.fivetrue.gimpo.ac05.vo.user.FirebaseUserInfo;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.auth.FirebaseUser;

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
public class SplashActivity extends BaseActivity implements GoogleLoginUtil.OnAccountManagerListener {

    private static final String TAG = "SplashActivity";

    private static final int REQUEST_LOGIN_CODE = 0x33;

    private static final int RETRY_COUNT = 3;

    private TextView mMainMessage = null;
    private TextView mLoadingMessage = null;

    private ProgressBar mProgress = null;
    private LinearLayout mUserLayout = null;

    private CircleImageView mUserImage = null;
    private TextView mUserNickname = null;

    private Button mLoginGoogle = null;

    private ConfigRequest mConfigReqeust = null;
    private RegisterUserRequest mRegisterUserRequest = null;
    private ConfigPreferenceManager mConfigPref = null;
    private DistrictDataReqeust mDistrictDataRequest = null;

    private GoogleLoginUtil mGoogleLoginUtil = null;

    private Typeface mTyeFace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initData();
        initView();
        getApplicationConfig();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleLoginUtil.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mGoogleLoginUtil.onStop();
    }

    private void initView(){

        mMainMessage = (TextView) findViewById(R.id.tv_splash_main);
        mLoadingMessage = (TextView) findViewById(R.id.tv_splash_loading);

        mUserLayout = (LinearLayout) findViewById(R.id.layout_splash_user_info);
        mUserNickname = (TextView) findViewById(R.id.tv_splash_user);
        mUserImage = (CircleImageView) findViewById(R.id.iv_splash_user);

        mProgress = (ProgressBar) findViewById(R.id.pb_splash);
        mProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent)
                , android.graphics.PorterDuff.Mode.MULTIPLY);

        mLoginGoogle = (Button) findViewById(R.id.btn_splash_login_google);

        mLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleLoginUtil.loginGoogleAccount();
                showProgress();
            }
        });

        mMainMessage.setTypeface(mTyeFace);
    }

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mConfigReqeust = new ConfigRequest(this, onConfigResponseListener);
        mRegisterUserRequest = new RegisterUserRequest(this, onRegisterUserInfoResponse);
        mDistrictDataRequest = new DistrictDataReqeust(this, districtApiResponse);

        mGoogleLoginUtil = new GoogleLoginUtil(this, getString(R.string.firebase_auth_client_id));
        mTyeFace = Typeface.createFromAsset(getAssets(), "Typo_PapyrusM.ttf");
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
    private void registerDevice(final AppConfig appConfig) {
        Log.i(TAG, "registerDevice: start");
        mLoadingMessage.setText(R.string.config_data_register);
        if (appConfig != null) {
            Log.i(TAG, "appConfig = " + appConfig.toString());
            Log.i(TAG, "registerDevice: init NaverApiManager start");

            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    String regId = null;
                    if (params != null && params.length > 0) {
                        String senderId = params[0];
                        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
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
                    if (value != null) {
                        mConfigPref.setGcmDeviceId(value);
                    } else {
                        Log.e(TAG, "registerDevice Gcm register error");
                    }
                    checkLoginStatus(mGoogleLoginUtil.getUser());

                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, appConfig.getSenderId());
        }
    }

    private void checkLoginStatus(FirebaseUser user){
        if(user != null){
            FirebaseUserInfo oldUserInfo = mConfigPref.getUserInfo();
            if(getIntent().getAction().equals(NotificationHelper.ACTION_NOTIFICATION)){
                startMainActivity(oldUserInfo);
            }else{
                mLoadingMessage.setVisibility(View.VISIBLE);
                showProgress();
                mLoadingMessage.setText(R.string.config_user_info_register);
                FirebaseUserInfo userInfo = new FirebaseUserInfo(user
                        , mConfigPref.getGcmDeviceId()
                        ,Build.MODEL, oldUserInfo != null ? oldUserInfo.getDistrict() : 0);
                mConfigPref.setUserInfo(userInfo);
                mRegisterUserRequest.setObject(userInfo);
                NetworkManager.getInstance().request(mRegisterUserRequest);
            }
        }else{
            mLoadingMessage.setVisibility(View.GONE);
            showLoginButton();
        }
    }

    /**
     * 3. login to Naver
     */
//    private void loginNaver(){
//        Log.i(TAG, "loginNaver: start");
//        mLoadingMessage.setText(R.string.config_data_auto_login);
//        startActivityForResult(new Intent(this, NaverLoginActivity.class), REQUEST_LOGIN_CODE);
//    }

    private BaseApiResponse.OnResponseListener<AppConfig> onConfigResponseListener = new BaseApiResponse.OnResponseListener<AppConfig>() {

        private int mRetryCount = 0;

        @Override
        public void onResponse(BaseApiResponse<AppConfig> response) {
            if(response != null){
                Log.i(TAG, "onConfigResponse onResponse : " + response.toString());
                mLoadingMessage.setText(R.string.config_check_app_version);
                if(response.getData() != null){
                    mConfigPref.setAppConfig(response.getData());
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
                                        AppUtils.goAppStore(SplashActivity.this, config.getAppMarketUrl());
                                    }
                                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                AppUtils.goAppStore(SplashActivity.this, config.getAppMarketUrl());

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
    };

    private void finishError(){
        Toast.makeText(SplashActivity.this, R.string.config_data_can_not_read, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500L);
    }

    private void startApplication(final FirebaseUserInfo userInfo){
        Log.i(TAG, "startApplication: start");
        SimpleViewUtils.hideView(mLoadingMessage, View.GONE);
        SimpleViewUtils.hideView(mProgress, View.GONE);
        if(userInfo != null){
            Log.i(TAG, "startApplication: naverUserInfo = " + userInfo.toString());
            mUserNickname.setText(userInfo.getDisplayName());
            mUserImage.setImageUrl(userInfo.getPhotoUrl());

            SimpleViewUtils.showView(mUserLayout, View.VISIBLE, new SimpleViewUtils.SimpleAnimationStatusListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd() {
                    mUserLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startMainActivity(userInfo);
                        }
                    }, 700L);
                }
            });
            mUserLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showProgress() {
        SimpleViewUtils.hideView(mLoginGoogle, View.GONE, new SimpleViewUtils.SimpleAnimationStatusListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd() {
                SimpleViewUtils.showView(mProgress, View.VISIBLE);
            }
        });
    }

    private void showLoginButton(){
        SimpleViewUtils.hideView(mProgress, View.GONE, new SimpleViewUtils.SimpleAnimationStatusListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd() {
                SimpleViewUtils.showView(mLoginGoogle, View.VISIBLE);
            }
        });
    }

    private void startMainActivity(FirebaseUserInfo info){
        Log.i(TAG, "startMainActivity: start");

        if(!AppUtils.isServiceRunning(this, FirebaseChattingService.class)){
            startService(new Intent(getApplicationContext(), FirebaseChattingService.class));
        }

        Intent intent = null;
        if(getIntent() != null
                && getIntent().getAction() != null
                && getIntent().getAction().equals(NotificationHelper.ACTION_NOTIFICATION)){
            intent = new Intent(this, MainActivity.class);
            intent.setData(getIntent().getData());
            intent.putExtra(NotificationHelper.KEY_NOTIFICATION_PARCELABLE, getIntent().getParcelableExtra(NotificationHelper.KEY_NOTIFICATION_PARCELABLE));

        }else{
            intent = new Intent(this, MainActivity.class);
        }
        startActivityWithClipRevealAnimation(intent, mMainMessage);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

//    private void failedLogin(){
//        Toast.makeText(this, R.string.fail_auth_user_info, Toast.LENGTH_SHORT).show();
//        GoogleAnalytics.getInstance().sendLogEventProperties(Event.EnterSplashActivity_LoginFailed);
//        getBaseLayoutContainer().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        }, 1000L);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoogleLoginUtil.onActivityResult(requestCode, resultCode, data);
    }

    private BaseApiResponse.OnResponseListener<FirebaseUserInfo> onRegisterUserInfoResponse = new BaseApiResponse.OnResponseListener<FirebaseUserInfo>() {

        private int mRetryCount = 0;

        @Override
        public void onResponse(BaseApiResponse<FirebaseUserInfo> response) {
            Log.i(TAG, "onRegisterUserInfoResponse onResponse: " + response.toString());
            if(response != null && response.getData() != null){
                final FirebaseUserInfo userInfo = response.getData();
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
    };

    private BaseApiResponse.OnResponseListener<ArrayList<District>> districtApiResponse = new BaseApiResponse.OnResponseListener<ArrayList<District>>() {
        private int mRetry = 0;
        @Override
        public void onResponse(BaseApiResponse<ArrayList<District>> response) {
            if(response != null){
                mConfigPref.setDistricts(response.getData());
                registerDevice(mConfigPref.getAppConfig());
            }else{
                if(BaseApiResponse.RETRY_COUNT > mRetry){
                    NetworkManager.getInstance().request(mDistrictDataRequest);
                    mRetry++;
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            Log.w(TAG, "onError: ", error);
        }
    };

    @Override
    public void onUserAddSuccess(FirebaseUser user) {
        checkLoginStatus(user);
    }

    @Override
    public void onUserAddError(Exception message) {
        showLoginButton();
    }

    @Override
    public void onUpdateUserInfo(FirebaseUser user) {

    }
}
