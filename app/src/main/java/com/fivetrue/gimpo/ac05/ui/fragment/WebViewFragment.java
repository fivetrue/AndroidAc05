package com.fivetrue.gimpo.ac05.ui.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.analytics.Event;
import com.fivetrue.gimpo.ac05.analytics.GoogleAnalytics;
import com.fivetrue.gimpo.ac05.utils.Log;

/**
 * Created by kwonojin on 16. 6. 10..
 */
public class WebViewFragment extends BaseFragment{

    private static final String TAG = "WebViewFragment";

    private static final int REQUEST_CODE_GET_FILE = 0x77;
    private static final int REQUEST_CODE_GET_FILE_OVER_L = 0x66;

    public interface OnShouldOverrideUrlLoadingListener {
        boolean onOverride(String url);
        void onCallback(String response);
    }

    private ContentLoadingProgressBar mProgress = null;
    private WebView mWebView = null;
    private FloatingActionButton mFabShare = null;

    private OnShouldOverrideUrlLoadingListener mOnShouldOverrideUrlLoadingListener = null;

    private String mUrl = null;
    private ValueCallback<Uri> mFileCallback;
    private ValueCallback<Uri[]> mFilePathCallbacks = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof OnShouldOverrideUrlLoadingListener){
            mOnShouldOverrideUrlLoadingListener = (OnShouldOverrideUrlLoadingListener) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater);
    }

    protected void setUrl(String url){
        mUrl = url;
    }

    protected void initData(){
        setUrl(getArguments().getString("url"));
        GoogleAnalytics.getInstance().sendLogEventProperties(Event.EnterWebviewFragment.addParams("url", mUrl));
    }

    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_webview, null);
        mProgress = (ContentLoadingProgressBar) view.findViewById(R.id.pb_fragment_webview);
        mWebView = (WebView) view.findViewById(R.id.webview_fragment_webview);
        mFabShare = (FloatingActionButton) view.findViewById(R.id.fab_fragment_webview);
        mFabShare.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        mFabShare.setVisibility(getArguments().getBoolean("hide", false) ? View.GONE : View.VISIBLE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        mProgress.setMax(100);

        mFabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/html");
                i.putExtra(android.content.Intent.EXTRA_TEXT, mUrl);
                startActivity(i);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView.addJavascriptInterface(this, "Android");
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
        if(mUrl != null){
            mWebView.loadUrl(mUrl);
        }
    }

    @JavascriptInterface
    public void onCallback(final String response){
        Log.i(TAG, "onCallback = " + response);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mOnShouldOverrideUrlLoadingListener != null) {
                    mOnShouldOverrideUrlLoadingListener.onCallback(response);
                }
            }
        });
    }

    protected boolean onShouldOverrideUrlLoading(WebView view, String url){
        boolean b = false;
        if(mOnShouldOverrideUrlLoadingListener != null){
            b = mOnShouldOverrideUrlLoadingListener.onOverride(url);
        }
        return b;
    }

    protected void onWebPageFinished(WebView view, String url){
        mProgress.setVisibility(View.GONE);
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
        if(getActivity() != null){
            mFileCallback = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            if(acceptType != null){
                i.setType(acceptType);
            }else{
                i.setType("*/*");
            }
            getActivity().startActivityForResult(Intent.createChooser(i, "Select File"), REQUEST_CODE_GET_FILE);
        }
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

    public WebView getWebView(){
        return mWebView;
    }

    public FloatingActionButton getShareButton(){
        return mFabShare;
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
}
