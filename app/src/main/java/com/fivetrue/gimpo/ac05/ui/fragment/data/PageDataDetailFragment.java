package com.fivetrue.gimpo.ac05.ui.fragment.data;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.vo.data.PageData;

/**
 * Created by kwonojin on 16. 6. 16..
 */
public class PageDataDetailFragment extends BaseFragment {

    private static final String TAG = "PageDataDetailFragment";

    private TextView mPageDataTitle = null;
    private WebView mWebView = null;
    private FloatingActionButton mDetailButton = null;

    private PageData mPageData = null;

    private int mTextColor = 0;
    private int mTextBgColor = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater);
    }

    private void initData(){
        mPageData = getArguments().getParcelable(PageData.class.getName());
        mTextColor = getArguments().getInt("textColor", getResources().getColor(R.color.colorAccent));
        mTextBgColor = getArguments().getInt("bgColor", getResources().getColor(R.color.colorPrimary));
    }

    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_page_data_detail, null);
        mPageDataTitle = (TextView) view.findViewById(R.id.tv_fragment_page_data_detail);
        mWebView = (WebView) view.findViewById(R.id.webview_fragment_page_data_detail);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mDetailButton = (FloatingActionButton) view.findViewById(R.id.fab_fragment_page_data_detail);
        mDetailButton.setRippleColor(mTextBgColor);
        mPageDataTitle.setTextColor(mTextColor);
        mPageDataTitle.setBackgroundColor(mTextBgColor);
        mDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPageData != null && getActivity() != null){
                    Uri uri = Uri.parse(mPageData.getPageUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView.setWebViewClient(webViewClient);
        mWebView.loadDataWithBaseURL("", mPageData.getPageContent(), "text/html", "UTF-8", "");
        mPageDataTitle.setText(mPageData.getPageTitle());
    }

    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
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

    public static Bundle makeArgument(PageData pageData, @Nullable int textColor, @Nullable int bgColor){
        Bundle b = new Bundle();
        b.putParcelable(PageData.class.getName(), pageData);
        b.putInt("textColor", textColor);
        b.putInt("bgColor", bgColor);
        return b;
    }
}