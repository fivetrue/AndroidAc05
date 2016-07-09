package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.analytics.Event;
import com.fivetrue.gimpo.ac05.analytics.GoogleAnalytics;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.TokenRequest;
import com.fivetrue.gimpo.ac05.net.response.TokenApiResponse;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.fragment.WebViewFragment;
import com.fivetrue.gimpo.ac05.utils.AppUtils;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.view.CircleImageView;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;
import com.fivetrue.gimpo.ac05.vo.config.Token;
import com.fivetrue.gimpo.ac05.vo.user.District;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class SettingActivity extends DrawerActivity{

    private static final String TAG = "SettingActivity";

    private static final int MESSGE_INIT_CLICK_TIME = 0x44;

    private CircleImageView mUserImage = null;
    private Button mUserCafeMyInfo = null;
    private Button mUserLogout = null;

    private TextView mVersionInfo = null;
    private TextView mDistrictInfo = null;

    private SwitchCompat mSwitchPush = null;

    private UserInfo mUserInfo = null;
    private ArrayList<District> mDistricts = null;

    private ConfigPreferenceManager mConfigPref = null;
    private AppConfig mAppConfig = null;

    private TokenRequest mDeleteTokenRequest = null;

    private int mHiddenCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initData();
        initView();
        GoogleAnalytics.getInstance().sendLogEventProperties(Event.EnterSettingActivity);
    }

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mAppConfig = mConfigPref.getAppConfig();
        mUserInfo = mConfigPref.getUserInfo();
        mDistricts = mConfigPref.getDistricts();
        mDeleteTokenRequest = new TokenRequest(this, deleteTokenApiResponse);
    }

    private void initView(){
        mUserImage = (CircleImageView) findViewById(R.id.iv_setting_user_image);
//        mUserMyInfo = (Button) findViewById(R.id.btn_setting_user_info);
        mUserCafeMyInfo = (Button) findViewById(R.id.btn_setting_cafe_user_info);
        mUserLogout = (Button) findViewById(R.id.btn_setting_user_logout);

        mSwitchPush = (SwitchCompat) findViewById(R.id.sw_setting_push);

        mVersionInfo = (TextView) findViewById(R.id.tv_setting_version_info_value);
        mDistrictInfo = (TextView) findViewById(R.id.tv_setting_district_info_value);

        mUserImage.setImageUrl(mUserInfo.getProfileImage());

        mVersionInfo.setText(AppUtils.getApplicationVersionName(this));

        District d = new District();
        d.setDistrictNumber(mUserInfo.getDistrict());
        if(mDistricts.contains(d)){
            District district = mDistricts.get(mDistricts.indexOf(d));
            mDistrictInfo.setText(district.getDistrictName());
        }else{
            mDistrictInfo.setText("-");
        }

        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("url", mAppConfig.getMyInfoUrl());
                addFragment(WebViewFragment.class, b, true);
                GoogleAnalytics.getInstance().sendLogEventProperties(Event.ClickSettingMenu_MyInfo);
            }
        });

        mUserCafeMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("url", mAppConfig.getClubMyInfo() + mUserInfo.getEmail());
                addFragment(WebViewFragment.class, b, true);
                GoogleAnalytics.getInstance().sendLogEventProperties(Event.ClickSettingMenu_MyCafeInfo);
            }
        });

        mUserLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, R.string.setting_logout_message, Toast.LENGTH_SHORT).show();
                Token token = mConfigPref.getToken();
                mDeleteTokenRequest.requestDeleteToken(mAppConfig, token);
                NetworkManager.getInstance().request(mDeleteTokenRequest);
                GoogleAnalytics.getInstance().sendLogEventProperties(Event.ClickSettingMenu_Logout);
            }
        });

        mSwitchPush.setChecked(mConfigPref.isSettingPush());

        mSwitchPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mConfigPref.setSettingPush(isChecked);
                GoogleAnalytics.getInstance().sendLogEventProperties(isChecked ? Event.ClickSettingMenu_NotificationOn : Event.ClickSettingMenu_NotificationOff);
            }
        });

        //버전정보
        findViewById(R.id.layout_setting_version_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHiddenCount > 12){
                    String url = mConfigPref.getAppConfig().getAdminUrl();
                    Bundle b = new Bundle();
                    b.putString("url", String.format("%s?email=%s&id=%s", url, mUserInfo.getEmail(), mUserInfo.getId()));
                    addFragment(WebViewFragment.class, b, true);
                    mHiddenCount = 0;
                }
                mHiddenCount ++;
                mHandler.removeMessages(MESSGE_INIT_CLICK_TIME);
                mHandler.sendEmptyMessageDelayed(MESSGE_INIT_CLICK_TIME, 1000L);
            }
        });

        findViewById(R.id.layout_setting_district).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, UserInfoInputActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Fragment f = getCurrentFragmentManager().findFragmentById(getFragmentAnchorLayoutID());
        if(f != null && f instanceof WebViewFragment){
            if(((WebViewFragment) f).canGoback()){
                ((WebViewFragment) f).goBack();
                return;
            }
        }
        super.onBackPressed();
    }

    private TokenApiResponse deleteTokenApiResponse = new TokenApiResponse(new BaseApiResponse.OnResponseListener<Token>() {

        private int mRetry = 0;

        @Override
        public void onResponse(BaseApiResponse<Token> response) {
            Log.i(TAG, "onResponse: " + response);
            if(response != null && response.getData() != null){
                Token token = response.getData();
                if(token.getResult().equalsIgnoreCase("success")){
                    mConfigPref.setToken(null);
                    mConfigPref.setUserInfo(null);
                    finishAffinity();
                }
            }else{
                if(mRetry < BaseApiResponse.RETRY_COUNT){
                    mRetry++;
                    NetworkManager.getInstance().request(mDeleteTokenRequest);
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            Log.i(TAG, "onError: " + error.toString());
            if(mRetry < BaseApiResponse.RETRY_COUNT){
                mRetry++;
                NetworkManager.getInstance().request(mDeleteTokenRequest);
            }
        }
    });

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mHiddenCount = 0;
        }
    };
}
