package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;

import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.gimpo.ac05.utils.DeeplinkUtil;

/**
 * Created by kwonojin on 2016. 10. 18..
 */

public class ByPassAcitivty extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeeplinkUtil.goPage(this, getIntent());
        finish();
    }
}
