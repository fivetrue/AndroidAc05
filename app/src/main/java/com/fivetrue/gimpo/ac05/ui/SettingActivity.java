package com.fivetrue.gimpo.ac05.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.fivetrueandroid.google.GoogleLoginUtil;
import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.NetworkManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.utils.AppUtils;
import com.fivetrue.fivetrueandroid.view.CircleImageView;
import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.net.request.TokenRequest;
import com.fivetrue.gimpo.ac05.net.response.TokenApiResponse;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.fragment.SettingFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.WebViewFragment;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;
import com.fivetrue.gimpo.ac05.vo.config.Token;
import com.fivetrue.gimpo.ac05.vo.user.District;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class SettingActivity extends BaseActivity{

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

    private int mHiddenCount = 0;

    private GoogleLoginUtil mGoogleLoginUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initData();
        initView();
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

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mAppConfig = mConfigPref.getAppConfig();
        mUserInfo = mConfigPref.getUserInfo();
        mDistricts = mConfigPref.getDistricts();
        mGoogleLoginUtil = new GoogleLoginUtil(this, getString(R.string.firebase_auth_client_id));
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            }
        });

        mUserCafeMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("url", mAppConfig.getClubMyInfo() + mUserInfo.getEmail());
                addFragment(WebViewFragment.class, b, true);
            }
        });

        mUserLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingActivity.this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert)
                        .setTitle(R.string.notice)
                        .setTitle(R.string.setting_logout_message)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mGoogleLoginUtil.logout();
                        mConfigPref.setUserInfo(null);
                        finishAffinity();
                    }
                }).show();
            }
        });

        /**
         * 버전정보
         */
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(getFragmentAnchorLayoutID());
        if(f != null && f instanceof WebViewFragment){
            if(((WebViewFragment) f).canGoback()){
                ((WebViewFragment) f).goBack();
                return;
            }
        }
        super.onBackPressed();
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mHiddenCount = 0;
        }
    };

    protected int getFragmentAnchorLayoutID() {
        return R.id.layout_setting_container;
    }
}
