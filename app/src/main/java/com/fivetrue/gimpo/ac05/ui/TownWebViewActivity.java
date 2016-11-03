package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.utils.CustomWebViewClient;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.firebase.model.TownNews;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class TownWebViewActivity extends BaseActivity implements CustomWebViewClient.JSInterface{

    private static final String TAG = "TownWebViewActivity";

    private WebView mWebView = null;

    private ConfigPreferenceManager mConfigPref = null;

    private CustomWebViewClient mCustomWebViewClient = null;
    private TownJSInterface mTownInterface;

    private Pattern mPattern = Pattern.compile("src\\s*=\\s*([\"'])?([^ \"']*)");

    private String mHtmlTemplete;
    private TownNews mTownNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_town_webview);
        initData();
        initView();
    }

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mTownInterface = new TownJSInterface();
        mTownNews = getIntent().getParcelableExtra(TownNews.class.getName());

        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(getAssets().open("templete/town_templete.html")));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
            mHtmlTemplete = total.toString();
        } catch (IOException e) {
            Log.w(TAG, "initData: html templete open error", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCustomWebViewClient.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCustomWebViewClient.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomWebViewClient.onDestroy();
        mWebView = null;
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(mTownNews.title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setVisibility(View.INVISIBLE);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mCustomWebViewClient = new CustomWebViewClient(this, mWebView, this);
        mWebView.loadUrl(mTownNews.url);
    }

    @Override
    protected int getOptionsMenuResource() {
        return R.menu.menu_webview;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                return true;
            case R.id.action_share :
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/html");
                i.putExtra(Intent.EXTRA_TEXT, mTownNews.url);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public String getInterfaceName() {
        return Constants.JS_INTERFACE_NAME;
    }

    @Override
    public Object getInterface() {
        return mTownInterface;
    }

    @Override
    public void onLoadedPage(WebView webView, String url) {
        webView.loadUrl("javascript:window."+ Constants.JS_INTERFACE_NAME + ".onLoadTableContent(document.getElementsByTagName('tbody')[0].innerHTML);");
    }

    @Override
    public void onStartPage(WebView webView, String url) {

    }

    private class TownJSInterface {

        boolean loaded;
        @android.webkit.JavascriptInterface
        void onLoadTableContent(String content){

                final String html = String.format(mHtmlTemplete , mTownNews.title, mTownNews.url, mTownNews.title
                        , content);
                if(!loaded){
                    loaded = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.loadDataWithBaseURL(mTownNews.url, html, "text/html", "UTF-8", null);
                            mWebView.setVisibility(View.VISIBLE);
                        }
                    });
                }
        }

        @android.webkit.JavascriptInterface
        void onDownloadFile(String downloadPath){
            String downloadUrl = mConfigPref.getAppConfig().townHostUrl + downloadPath;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(downloadUrl));
            startActivity(intent);
        }
    }
}
