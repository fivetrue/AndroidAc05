package com.fivetrue.gimpo.ac05.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.utils.SimpleViewUtils;
import com.fivetrue.gimpo.ac05.R;

/**
 * Created by kwonojin on 16. 6. 10..
 */
public class WebViewActivity extends BaseActivity {

    private static final String TAG = "WebViewActivity";

    private static final int REQUEST_CODE_GET_FILE = 0x77;
    private static final int REQUEST_CODE_GET_FILE_OVER_L = 0x66;

    private CollapsingToolbarLayout mCollapsingToolbar;
    private ImageView mMainImage = null;

    private ContentLoadingProgressBar mProgress = null;
    private WebView mWebView = null;

    private String mUrl = null;
    private String mTitle = null;
    private String mSubTitle = null;
    private String mImageUrl = null;

    private ValueCallback<Uri> mFileCallback;
    private ValueCallback<Uri[]> mFilePathCallbacks = null;

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
        mSubTitle = getIntent().getStringExtra("subtitle");
        mImageUrl = getIntent().getStringExtra("image");
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        mMainImage = (ImageView) findViewById(R.id.iv_webview_image);

        mProgress = (ContentLoadingProgressBar) findViewById(R.id.pb_webview);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        mProgress.setMax(100);

//        mWebView.addJavascriptInterface(this, "Android");
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
        if(mUrl != null){
            mWebView.loadUrl(mUrl);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mSubTitle);

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


    protected boolean onShouldOverrideUrlLoading(WebView view, String url){
        boolean b = false;
        return b;
    }

    protected void onWebPageFinished(WebView view, String url){
        mProgress.setVisibility(View.GONE);
        mUrl = url;
    }

    protected void onWebPageCommitVisible(WebView view, String url){

    }

    protected void onPageProgressChanged(WebView view, int newProgress) {
        mProgress.setProgress(newProgress);
    }

    protected void onWebPageStarted(WebView view, String url, Bitmap favicon){
        mProgress.setVisibility(View.VISIBLE);
    }

    protected void onPagePermissionRequest(PermissionRequest request){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            request.grant(request.getResources());
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected boolean onShowFileChooserFromWeb(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams){
        mFilePathCallbacks = filePathCallback;
        try {
            Intent intent = fileChooserParams.createIntent();
            startActivityForResult(intent, REQUEST_CODE_GET_FILE_OVER_L);
        } catch (Exception e) {
            // TODO: when open file chooser failed
        }
        return true;
    }

    protected void onOpenFileChooserFromWeb(ValueCallback<Uri> uploadMsg, String acceptType, String caputre){
        mFileCallback = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        if(acceptType != null){
            i.setType(acceptType);
        }else{
            i.setType("*/*");
        }
        startActivityForResult(Intent.createChooser(i, "Select File"), REQUEST_CODE_GET_FILE);
    }

    protected void onGetFile(int resultCode, Intent intent){
        if(intent != null && mFileCallback != null){
            if(resultCode == Activity.RESULT_OK){
                mFileCallback.onReceiveValue(intent.getData());
            }else{
                mFileCallback.onReceiveValue(null);
            }
            mFileCallback = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onGetFileOverL(int resultCode, Intent intent){
        if(intent != null && mFilePathCallbacks != null){
            mFilePathCallbacks.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            mFilePathCallbacks = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode){
            case REQUEST_CODE_GET_FILE:
                onGetFile(resultCode, intent);
                break;
            case REQUEST_CODE_GET_FILE_OVER_L:
                onGetFileOverL(resultCode, intent);
                break;
        }
    }

    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return onShouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            onWebPageFinished(view, url);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            onWebPageCommitVisible(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            onWebPageStarted(view, url, favicon);
        }
    };

    private WebChromeClient webChromeClient = new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            onPageProgressChanged(view, newProgress);
        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {
            super.onPermissionRequest(request);
            onPagePermissionRequest(request);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            return onShowFileChooserFromWeb(webView, filePathCallback, fileChooserParams);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            onOpenFileChooserFromWeb(uploadMsg, null, null);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            onOpenFileChooserFromWeb(uploadMsg, acceptType, null);
        }

        //For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            onOpenFileChooserFromWeb(uploadMsg, acceptType, capture);
        }
    };

    public boolean canGoback(){
        return mWebView.canGoBack();
    }

    public void goBack(){
        mWebView.goBack();
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
                onBackPressed();
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
}
