package com.fivetrue.gimpo.ac05.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.fivetrueandroid.google.GoogleLoginUtil;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.utils.AppUtils;
import com.fivetrue.fivetrueandroid.view.CircleImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.firebase.model.AppConfig;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.firebase.model.District;
import com.fivetrue.gimpo.ac05.firebase.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class SettingActivity extends BaseActivity{

    private static final String TAG = "SettingActivity";

    private static final int MESSGE_INIT_CLICK_TIME = 0x44;

    private NetworkImageView mUserImage = null;

    private TextView mUserEmail = null;
    private TextView mUserName = null;
    private TextView mUserNickname = null;
    private TextView mUserDistrict = null;
    private TextView mUserRecently = null;

    private Button mEditUserInfo = null;
    private Button mUserCafeMyInfo = null;
    private Button mUserLogout = null;

    private TextView mVersionInfo = null;
    private SwitchCompat mSwitchPush = null;

    private User mUserInfo = null;

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
        mGoogleLoginUtil = new GoogleLoginUtil(this, getString(R.string.firebase_auth_client_id));
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserImage = (NetworkImageView) findViewById(R.id.iv_setting_user_image);
        mUserEmail = (TextView) findViewById(R.id.tv_setting_user_email);
        mUserName = (TextView) findViewById(R.id.tv_setting_user_name);
        mUserNickname = (TextView) findViewById(R.id.tv_setting_user_nickname);
        mUserDistrict = (TextView) findViewById(R.id.tv_setting_user_district);
        mUserRecently = (TextView) findViewById(R.id.tv_setting_user_recently);

        mEditUserInfo = (Button) findViewById(R.id.btn_setting_user_edit);
        mUserCafeMyInfo = (Button) findViewById(R.id.btn_setting_cafe_user_info);
        mUserLogout = (Button) findViewById(R.id.btn_setting_user_logout);

        mSwitchPush = (SwitchCompat) findViewById(R.id.sw_setting_push);

        mVersionInfo = (TextView) findViewById(R.id.tv_setting_version_info_value);

        mUserEmail.setText(getString(R.string.email) + " : " + mUserInfo.email);
        mUserName.setText(getString(R.string.name) + " : " + mUserInfo.name);
        mUserNickname.setText(getString(R.string.nickname) + " : " + mUserInfo.nickName);
        mUserRecently.setText(getString(R.string.recently_active_date) + " : " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(mUserInfo.updateTime)));
        mUserImage.setImageUrl(mUserInfo.profileImage, ImageLoadManager.getImageLoader());

        mVersionInfo.setText(AppUtils.getApplicationVersionName(this));
        mSwitchPush.setChecked(DefaultPreferenceManager.getInstance(this).isPushService());


        District d = new District();
        d.districtNumber = mUserInfo.district;
        if(mAppConfig.districts.contains(d)){
            District district = mAppConfig.districts.get(mAppConfig.districts.indexOf(d));
            mUserDistrict.setText(getString(R.string.setting_district) + " : " + district.districtName);
        }else{
            mUserDistrict.setVisibility(View.GONE);
        }


        mEditUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, UserInfoInputActivity.class);
                startActivityWithClipRevealAnimation(intent, v);
            }
        });

        mUserCafeMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, WebViewActivity.class);
                intent.putExtra("url", mAppConfig.clubMyInfo);
                intent.putExtra("title", getString(R.string.my_cafe_info));
                startActivityWithClipRevealAnimation(intent, mUserCafeMyInfo);
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
                    Intent intent = new Intent(SettingActivity.this, AdminActivity.class);
                    startActivityWithClipRevealAnimation(intent, findViewById(R.id.layout_setting_version_info));
                    mHiddenCount = 0;
                }
                mHiddenCount ++;
                mHandler.removeMessages(MESSGE_INIT_CLICK_TIME);
                mHandler.sendEmptyMessageDelayed(MESSGE_INIT_CLICK_TIME, 1000L);
            }
        });

        findViewById(R.id.layout_setting_dev_qna).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.goAppStore(SettingActivity.this);
            }
        });

        mSwitchPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwitchPush.setChecked(mSwitchPush.isChecked());
                DefaultPreferenceManager.getInstance(SettingActivity.this).setPushService(mSwitchPush.isChecked());
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
