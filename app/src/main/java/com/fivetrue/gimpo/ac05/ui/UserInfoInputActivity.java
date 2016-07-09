package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.analytics.Event;
import com.fivetrue.gimpo.ac05.analytics.GoogleAnalytics;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.UpdateUserDistrictRequest;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.adapter.DistrictSpinnerAdapter;
import com.fivetrue.gimpo.ac05.vo.user.District;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 9..
 */
public class UserInfoInputActivity extends BaseActivity {

    private TextView mWellcomeText = null;
    private AppCompatSpinner mSpinner = null;
    private Button mButton = null;

    private LinearLayout mLayoutUserSelected = null;
    private TextView mSelectedText = null;

    private UserInfo mUserInfo = null;
    private ArrayList<District> mDistricts = new ArrayList<>();
    private ConfigPreferenceManager mConfigPref = null;

    private DistrictSpinnerAdapter mAdapter = null;

    private UpdateUserDistrictRequest mUpdateUserDistrict = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_input);
        initData();
        initView();
        GoogleAnalytics.getInstance().sendLogEventProperties(Event.EnterUserInfoInputActivity);
    }

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mUserInfo = mConfigPref.getUserInfo();
        mDistricts.add(null);
        mDistricts.addAll(mConfigPref.getDistricts() );
        mUpdateUserDistrict = new UpdateUserDistrictRequest(this, updateUserDistrictResponse);
    }

    private void initView(){
        mWellcomeText = (TextView) findViewById(R.id.tv_userinfo_input_wellcome);
        mSpinner = (AppCompatSpinner) findViewById(R.id.sp_user_info_input);
        mLayoutUserSelected = (LinearLayout) findViewById(R.id.layout_userinfo_input_selected);
        mSelectedText = (TextView) findViewById(R.id.tv_userinfo_input_selected);
        mButton = (Button) findViewById(R.id.btn_usernfo_input);
        mWellcomeText.setText(String.format(getString(R.string.userinfo_input_wellcome), mUserInfo.getName()));

        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                District district = mAdapter.getItem(position);
                if(district != null){
                    mUserInfo.setDistrict(district.getDistrictNumber());
                    StringBuilder sb = new StringBuilder();
                    sb.append(district.getDistrictName())
                            .append("\n\n")
                            .append(district.getDistrictType() + "형")
                            .append("\n\n")
                            .append(district.getCount() + "호")
                            .append("\n\n")
                            .append(district.getDistrictDesc());
                    mSelectedText.setText(sb.toString());
                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    mLayoutUserSelected.setAnimation(animation);
                    mLayoutUserSelected.setVisibility(View.VISIBLE);
                    GoogleAnalytics.getInstance().sendLogEventProperties(Event.ClickUserInfoInput_SelectedDistrict.addParams("District",
                            mAdapter.getItem(position).toString()));
                }else{
                    mUserInfo.setDistrict(0);
                    mSelectedText.setText("");
                    AlphaAnimation animation = new AlphaAnimation(1, 0);
                    mLayoutUserSelected.setAnimation(animation);
                    mLayoutUserSelected.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSpinner.setSelection(0);
                mUserInfo.setDistrict(0);
                AlphaAnimation animation = new AlphaAnimation(1, 0);
                mSelectedText.setAnimation(animation);
                mSelectedText.setVisibility(View.INVISIBLE);
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserInfo != null && mUserInfo.getDistrict() > 0) {
                    GoogleAnalytics.getInstance().sendLogEventProperties(Event.ClickUserInfoInput_FinishUserInput.addParams("District", "mUserInfo.getApartDong"));
                    mUpdateUserDistrict.setObject(mUserInfo);
                    NetworkManager.getInstance().request(mUpdateUserDistrict);
                } else {
                    Toast.makeText(UserInfoInputActivity.this, "값이 설정되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setData(mDistricts);
    }

    private void setData(ArrayList<District> districts){
        if(districts != null && districts.size() > 0){
            if(mAdapter == null){
                mAdapter = new DistrictSpinnerAdapter(this, districts);
                mSpinner.setAdapter(mAdapter);
            }else{
                mAdapter.setData(districts);
            }
        }
    }

    private BaseApiResponse<UserInfo> updateUserDistrictResponse = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<UserInfo>() {
        @Override
        public void onResponse(BaseApiResponse<UserInfo> response) {
            if(response != null &&  response.getData() != null){
                mConfigPref.setUserInfo(response.getData());
            }
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
