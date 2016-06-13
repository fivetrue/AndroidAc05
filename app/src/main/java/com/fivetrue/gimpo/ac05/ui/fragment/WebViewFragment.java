package com.fivetrue.gimpo.ac05.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.utils.Log;

/**
 * Created by kwonojin on 16. 6. 10..
 */
public class WebViewFragment extends BaseFragment{

    private static final String TAG = "WebViewFragment";

    public interface OnShouldOverrideUrlLoadingListener {
        boolean onOverride(String url);
        void onCallback(String response);
    }

    private WebView mWebView = null;
    private OnShouldOverrideUrlLoadingListener mOnShouldOverrideUrlLoadingListener = null;

    private String mUrl = null;

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

    private void initData(){
        mUrl = getArguments().getString("url");
    }

    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_webview, null);
        mWebView = (WebView) view.findViewById(R.id.webview_fragment_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView.addJavascriptInterface(this, "Android");
        mWebView.setWebViewClient(webViewClient);
        mWebView.loadUrl(mUrl);
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

    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(mOnShouldOverrideUrlLoadingListener != null){
                return mOnShouldOverrideUrlLoadingListener.onOverride(url);
            }else{
                return super.shouldOverrideUrlLoading(view, url);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    };

    public boolean canGoback(){
        return mWebView.canGoBack();
    }

    public void goBack(){
        mWebView.goBack();
    }
}
