package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.utils.CustomWebViewClient;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class CafeActivity extends BaseActivity{

    private static final String TAG = "CafeActivity";

    private ContentLoadingProgressBar mProgress = null;
    private WebView mWebView = null;


    private ConfigPreferenceManager mConfigPref = null;

    private CustomWebViewClient mCustomWebViewClient = null;


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
        mProgress = (ContentLoadingProgressBar) findViewById(R.id.pb_cafe);
        mWebView = (WebView) findViewById(R.id.webview_cafe);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        mProgress.setMax(100);

        mCustomWebViewClient = new CustomWebViewClient(this, mWebView, mProgress);

        mWebView.loadUrl(mConfigPref.getAppConfig().clubUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCustomWebViewClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

}
