package com.fivetrue.gimpo.ac05.ui.fragment.detail;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.analytics.Event;
import com.fivetrue.gimpo.ac05.analytics.GoogleAnalytics;
import com.fivetrue.gimpo.ac05.vo.rss.FeedMessage;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;

/**
 * Created by kwonojin on 16. 6. 16..
 */
public class PageDataDetailFragment extends BaseFragment {

    private static final String TAG = "PageDataDetailFragment";

    private TextView mPageDataTitle = null;
    private WebView mWebView = null;
    private ContentLoadingProgressBar mProgress = null;
    private FloatingActionButton mDetailButton = null;

    private FeedMessage mData = null;

    private int mTextColor = 0;
    private int mTextBgColor = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        GoogleAnalytics.getInstance().sendLogEventProperties(Event.EnterPageDataDetailFragment);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater);
    }

    private void initData(){
        mData = getArguments().getParcelable(FeedMessage.class.getName());
        mTextColor = getArguments().getInt("textColor", getResources().getColor(R.color.colorAccent));
        mTextBgColor = getArguments().getInt("bgColor", getResources().getColor(R.color.colorPrimary));
    }

    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_page_data_detail, null);
        mPageDataTitle = (TextView) view.findViewById(R.id.tv_fragment_page_data_detail);
        mProgress = (ContentLoadingProgressBar) view.findViewById(R.id.pb_fragment_page_data_detail);
        mWebView = (WebView) view.findViewById(R.id.webview_fragment_page_data_detail);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mProgress.setMax(100);
        mDetailButton = (FloatingActionButton) view.findViewById(R.id.fab_fragment_page_data_detail);
        mDetailButton.setRippleColor(mTextBgColor);
        mPageDataTitle.setTextColor(mTextColor);
        mPageDataTitle.setBackgroundColor(mTextBgColor);
        mDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(mData != null && getActivity() != null){
//                    Uri uri = Uri.parse(mData.getLink());
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);
//                }
                mWebView.loadUrl(mData.getLink());
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    Animator anim = ViewAnimationUtils.createCircularReveal(mDetailButton
                            , mDetailButton.getWidth() / 2, mDetailButton.getHeight() / 2
                            , mDetailButton.getWidth(), 0);
                    anim.setDuration(500L);
                    anim.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mDetailButton.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    anim.start();
                }else{
                    AlphaAnimation animation = new AlphaAnimation(1f, 0);
                    animation.setDuration(500L);
                    mDetailButton.setAnimation(animation);
                    mDetailButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.loadDataWithBaseURL("", mData.getDescription(), "text/html", "UTF-8", "");
        mPageDataTitle.setText(mData.getTitle());
    }

    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgress.setVisibility(View.VISIBLE);
        }
    };

    private WebChromeClient webChromeClient = new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgress.setProgress(newProgress);
        }
    };

    public static Bundle makeArgument(FeedMessage data, @Nullable int textColor, @Nullable int bgColor){
        Bundle b = new Bundle();
        b.putParcelable(FeedMessage.class.getName(), data);
        b.putInt("textColor", textColor);
        b.putInt("bgColor", bgColor);
        return b;
    }
}
