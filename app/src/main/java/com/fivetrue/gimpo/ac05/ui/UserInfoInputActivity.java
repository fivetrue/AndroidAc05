package com.fivetrue.gimpo.ac05.ui;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.ui.diaglog.LoadingDialog;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.firebase.database.UserInfoDatabase;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.adapter.DistrictSpinnerAdapter;
import com.fivetrue.gimpo.ac05.firebase.model.District;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by kwonojin on 16. 6. 9..
 */
public class UserInfoInputActivity extends BaseActivity {

    private static final String TAG = "UserInfoInputActivity";

    private TextView mWellcomeText = null;
    private TextInputLayout mInputLayout;
    private TextInputEditText mInputNickname;
    private AppCompatSpinner mSpinner = null;
    private Button mButton = null;

    private LinearLayout mLayoutUserSelected = null;
    private TextView mSelectedText = null;

    private User mUserInfo = null;
    private ConfigPreferenceManager mConfigPref = null;

    private DistrictSpinnerAdapter mAdapter = null;

    private InputMethodManager mImm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_input);
        initData();
        initView();
    }

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mUserInfo = mConfigPref.getUserInfo();
        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mAdapter = new DistrictSpinnerAdapter(this, mConfigPref.getAppConfig().districts);
    }

    private void initView(){
        mWellcomeText = (TextView) findViewById(R.id.tv_userinfo_input_wellcome);
        mInputLayout = (TextInputLayout) findViewById(R.id.layout_userinfo_input);
        mInputNickname = (TextInputEditText) findViewById(R.id.et_userinfo_input_nickname);
        mSpinner = (AppCompatSpinner) findViewById(R.id.sp_user_info_input);
        mLayoutUserSelected = (LinearLayout) findViewById(R.id.layout_userinfo_input_selected);
        mSelectedText = (TextView) findViewById(R.id.tv_userinfo_input_selected);
        mButton = (Button) findViewById(R.id.btn_usernfo_input);
        mWellcomeText.setText(String.format(getString(R.string.userinfo_input_wellcome), mUserInfo.name));

        if(!TextUtils.isEmpty(mUserInfo.nickName) && mUserInfo.district > 0){
            mWellcomeText.setVisibility(View.GONE);
        }

        mInputNickname.setText(mUserInfo.nickName);

        int position = 0;
        for(District district : mAdapter.getData()){
            if(district != null){
                if(district.districtNumber == mUserInfo.district){
                    position = mAdapter.getData().indexOf(district);
                }
            }
        }

        mSpinner.setAdapter(mAdapter);
        mSpinner.setSelection(position);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                District district = mAdapter.getItem(position);
                if(district != null){
                    mUserInfo.district = district.districtNumber;
                    StringBuilder sb = new StringBuilder();
                    sb.append(district.districtName)
                            .append("\n\n")
                            .append(district.districtType + "형")
                            .append("\n\n")
                            .append(district.count + "호")
                            .append("\n\n")
                            .append(district.districtDesc);
                    mSelectedText.setText(sb.toString());
                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    mLayoutUserSelected.setAnimation(animation);
                    mLayoutUserSelected.setVisibility(View.VISIBLE);
                }else{
                    mUserInfo.district = 0;
                    mSelectedText.setText("");
                    AlphaAnimation animation = new AlphaAnimation(1, 0);
                    mLayoutUserSelected.setAnimation(animation);
                    mLayoutUserSelected.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSpinner.setSelection(0);
                mUserInfo.district = 0;
                AlphaAnimation animation = new AlphaAnimation(1, 0);
                mSelectedText.setAnimation(animation);
                mSelectedText.setVisibility(View.INVISIBLE);
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if (mUserInfo != null) {
                    final String nickName = mInputNickname.getText().toString();
                    final int district = mUserInfo.district;

                    if(TextUtils.isEmpty(nickName)){
                        mInputLayout.setError(getString(R.string.does_not_setting_message));
                        return;
                    }
                    mInputLayout.setErrorEnabled(false);
                    if(district <= 0){
                        Toast.makeText(UserInfoInputActivity.this, R.string.does_not_setting_message, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final LoadingDialog dialog = new LoadingDialog(UserInfoInputActivity.this);
                    dialog.show();
                    new UserInfoDatabase(mUserInfo.uid).validNickname(nickName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null && TextUtils.isEmpty(mUserInfo.nickName)){
                                mInputLayout.setError(getString(R.string.duplication_nickname));
                                dialog.dismiss();
                            }else{
                                dataSnapshot.child(mUserInfo.uid)
                                        .child("nickName")
                                        .getRef().setValue(nickName.trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dataSnapshot.child(mUserInfo.uid)
                                                .child("district")
                                                .getRef().setValue(district).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mUserInfo.district = district;
                                                mUserInfo.nickName = nickName;
                                                mConfigPref.setUserInfo(mUserInfo);
                                                dialog.dismiss();
                                                Intent intent = new Intent(UserInfoInputActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });

                                    }
                                });
                            }
                            Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot + "]");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(UserInfoInputActivity.this, R.string.does_not_setting_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, 0);
    }

    @Override
    public void onBackPressed() {
        if(TextUtils.isEmpty(mUserInfo.nickName)){
            new AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert)
                    .setTitle(R.string.notice)
                    .setMessage(R.string.cancel_input_user_info_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            UserInfoInputActivity.super.onBackPressed();
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }else{
            super.onBackPressed();
        }
    }
}
