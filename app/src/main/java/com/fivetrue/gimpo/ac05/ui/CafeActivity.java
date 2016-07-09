package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.fragment.WebViewFragment;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class CafeActivity extends DrawerActivity implements WebViewFragment.OnShouldOverrideUrlLoadingListener{

    private static final String TAG = "CafeActivity";

    private ConfigPreferenceManager mConfigPref = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe);
        initData();
        initView();
    }

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
    }

    private void initView(){
        Bundle b = new Bundle();
        b.putString("url", mConfigPref.getAppConfig().getClubUrl());
        addFragment(WebViewFragment.class, b, false);
    }

    @Override
    public boolean onOverride(String url) {
        return false;
    }

    @Override
    public void onCallback(String response) {

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

    @Override
    protected int getActionbarRightImageRes() {
        return R.drawable.ic_home_30dp;
    }

    @Override
    protected void onClickActionBarRightButton(View view) {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finishAffinity();
        startActivity(i);
    }
}
