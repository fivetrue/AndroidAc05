package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.fragment.WebViewFragment;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class CafeActivity extends DrawerActivity implements WebViewFragment.OnShouldOverrideUrlLoadingListener{

    private static final String TAG = "CafeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe);
        Bundle b = new Bundle();
        b.putString("url", ((ApplicationEX)getApplicationContext()).getAppConfig().getClubUrl());
        addFragment(WebViewFragment.class, b, false);
    }

    @Override
    public boolean onOverride(String url) {
        return false;
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
}
