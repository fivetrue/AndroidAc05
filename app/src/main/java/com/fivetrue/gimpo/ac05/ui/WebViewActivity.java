package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.ContentLoadingProgressBar;
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
import com.fivetrue.gimpo.ac05.R;

/**
 * Created by kwonojin on 16. 6. 10..
 */
public class WebViewActivity extends BaseActivity {

    private static final String TAG = "WebViewActivity";

    private ImageView mMainImage = null;

    private ContentLoadingProgressBar mProgress = null;
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

    protected void initData(){
        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");
        mImageUrl = getIntent().getStringExtra("image");
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMainImage = (ImageView) findViewById(R.id.iv_webview_image);

        mProgress = (ContentLoadingProgressBar) findViewById(R.id.pb_webview);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        mProgress.setMax(100);

        mCustomWebViewClient  = new CustomWebViewClient(this, mWebView, mProgress);

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
    protected boolean transitionModeWhenFinish() {
        return true;
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
