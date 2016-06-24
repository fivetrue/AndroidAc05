package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.RegisterUserRequest;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.adapter.SimpleSpinnerAdapter;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 9..
 */
public class UserInfoInputActivity extends BaseActivity {

    private TextView mWellcomeText = null;
    private TextView mSelectedText = null;

    private AppCompatSpinner mSpinner = null;
    private Button mButton = null;

    private AppConfig mAppConfig = null;
    private UserInfo mUserInfo = null;
    private ConfigPreferenceManager mPref = null;

    private SimpleSpinnerAdapter mAdapter = null;

    private RegisterUserRequest mRequest = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_input);
        initData();
        initView();
    }

    private void initData(){
        mAppConfig = ((ApplicationEX) getApplicationContext()).getAppConfig();
        mPref = new ConfigPreferenceManager(this);
        mUserInfo = mPref.getUserInfo();
        mRequest = new RegisterUserRequest(this, userInfoBaseApiResponse);
        ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.input_user_dong));
        list.addAll(mAppConfig.getDistrictList());
        mAdapter = new SimpleSpinnerAdapter(this, list);
    }

    private void initView(){
        mWellcomeText = (TextView) findViewById(R.id.tv_userinfo_input_wellcome);
        mSelectedText = (TextView) findViewById(R.id.tv_userinfo_input_selected);
        mSpinner = (AppCompatSpinner) findViewById(R.id.sp_user_info_input);
        mButton = (Button) findViewById(R.id.btn_usernfo_input);
        mWellcomeText.setText(String.format(getString(R.string.userinfo_input_wellcome), mUserInfo.getNickname()));

        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    mUserInfo.setApartDong(mAdapter.getItem(position));
                    mSelectedText.setText(mAdapter.getItem(position) + " 동");
                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    mSelectedText.setAnimation(animation);
                    mSelectedText.setVisibility(View.VISIBLE);
                } else {
                    mUserInfo.setApartDong("");
                    AlphaAnimation animation = new AlphaAnimation(1, 0);
                    mSelectedText.setAnimation(animation);
                    mSelectedText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSpinner.setSelection(0);
                mUserInfo.setApartDong("");
                AlphaAnimation animation = new AlphaAnimation(1, 0);
                mSelectedText.setAnimation(animation);
                mSelectedText.setVisibility(View.INVISIBLE);
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserInfo != null && !TextUtils.isEmpty(mUserInfo.getApartDong())) {
                    mRequest.setObject(mUserInfo);
                    NetworkManager.getInstance().request(mRequest);
                } else {
                    Toast.makeText(UserInfoInputActivity.this, "값이 설정되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private BaseApiResponse<UserInfo> userInfoBaseApiResponse = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<UserInfo>() {
        @Override
        public void onResponse(BaseApiResponse<UserInfo> response) {
            finish();
        }

        @Override
        public void onError(VolleyError error) {
            finish();
        }
    }, new TypeToken<UserInfo>(){}.getType());

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, 0);
    }
}
