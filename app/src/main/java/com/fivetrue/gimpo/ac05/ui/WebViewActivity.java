package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.utils.CustomWebViewClient;
import com.fivetrue.fivetrueandroid.utils.SimpleViewUtils;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;

/**
 * Created by kwonojin on 16. 6. 10..
 */
public class WebViewActivity extends BaseActivity implements CustomWebViewClient.JSInterface{

    private static final String TAG = "WebViewActivity";

    private ImageView mMainImage = null;

    private WebView mWebView = null;

    private String mUrl = null;
    private String mTitle = null;
    private String mImageUrl = null;

    private CustomWebViewClient mCustomWebViewClient = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initData();
        initView();
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

    protected void initData(){
        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");
        mImageUrl = getIntent().getStringExtra("image");
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMainImage = (ImageView) findViewById(R.id.iv_webview_image);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        mCustomWebViewClient  = new CustomWebViewClient(this, mWebView, this);

        if(mUrl != null){
            mWebView.loadUrl(mUrl);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mTitle);

        if(mImageUrl != null){
            ImageLoadManager.getInstance().loadImageUrl(mImageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                    if(response != null && response.getBitmap() != null && !response.getBitmap().isRecycled()){
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                mMainImage.setImageBitmap(response.getBitmap());
                                SimpleViewUtils.showView(mMainImage, View.VISIBLE);
                            }
                        });
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w(TAG, "onErrorResponse: ", error);
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mCustomWebViewClient.onActivityResult(requestCode, resultCode, intent);
    }

    public String getUrl(){
        return mUrl;
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
                i.putExtra(Intent.EXTRA_TEXT, mUrl);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        return new DefaultJSInterface();
    }

    @Override
    public void onLoadedPage(WebView webView, String url) {
//        webView.loadUrl("document.getElementsByTagName('head')[0].innerHtml += <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />;");
//        webView.loadUrl("javascript:window."+ Constants.JS_INTERFACE_NAME + ".onLoadHtml(document.getElementsByTagName('html')[0].innerHtml);");
    }

    @Override
    public void onStartPage(WebView webView, String url) {

    }

    private class DefaultJSInterface{

        @android.webkit.JavascriptInterface
        public void onLoadHtml(String html){
            Log.d(TAG, "onLoadHtml() called with: html = [" + html + "]");
        }
    }
}
