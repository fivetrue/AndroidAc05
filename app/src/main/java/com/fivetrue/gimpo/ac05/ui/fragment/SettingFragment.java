package com.fivetrue.gimpo.ac05.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.fivetrue.fivetrueandroid.ui.fragment.BaseFragmentImp;
import com.fivetrue.gimpo.ac05.R;

/**
 * Created by kwonojin on 2016. 10. 10..
 */

public class SettingFragment extends PreferenceFragmentCompat implements BaseFragmentImp , SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = "SettingFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.config_prefrences, rootKey);
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public String getFragmentTitle() {
        return null;
    }

    @Override
    public String getFragmentBackStackName() {
        return null;
    }

    @Override
    public int getChildFragmentAnchorId() {
        return 0;
    }

    @Override
    public int getFragmentNameResource() {
        return 0;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
