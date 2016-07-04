package com.fivetrue.gimpo.ac05.ui.fragment.detail;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.analytics.Event;
import com.fivetrue.gimpo.ac05.analytics.GoogleAnalytics;
import com.fivetrue.gimpo.ac05.ui.fragment.WebViewFragment;
import com.fivetrue.gimpo.ac05.vo.rss.FeedMessage;

/**
 * Created by kwonojin on 16. 6. 16..
 */
public class PageDataDetailFragment extends WebViewFragment{

    private static final String TAG = "PageDataDetailFragment";

    private TextView mPageDataTitle = null;
    private FrameLayout mFrameLayout = null;
    private FloatingActionButton mDetailButton = null;

    private FeedMessage mData = null;

    private int mTextColor = 0;
    private int mTextBgColor = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = super.onCreateView(inflater, container, savedInstanceState);
        return initView(inflater, parentView);
    }

    @Override
    protected void initData(){
        mData = getArguments().getParcelable(FeedMessage.class.getName());
        mTextColor = getArguments().getInt("textColor", getResources().getColor(R.color.colorAccent));
        mTextBgColor = getArguments().getInt("bgColor", getResources().getColor(R.color.colorPrimary));
        GoogleAnalytics.getInstance().sendLogEventProperties(Event.EnterPageDataDetailFragment);
    }

    private View initView(LayoutInflater inflater, View parentView){
        View view = inflater.inflate(R.layout.fragment_page_data_detail, null);
        getShareButton().setVisibility(View.GONE);
        mPageDataTitle = (TextView) view.findViewById(R.id.tv_fragment_page_data_detail);
        mFrameLayout = (FrameLayout) view.findViewById(R.id.layout_fragment_page_data_detail);
        mFrameLayout.addView(parentView);
        mDetailButton = (FloatingActionButton) view.findViewById(R.id.fab_fragment_page_data_detail);
        mDetailButton.setRippleColor(mTextBgColor);
        mPageDataTitle.setTextColor(mTextColor);
        mPageDataTitle.setBackgroundColor(mTextBgColor);
        mDetailButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
        mDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(mData != null && getActivity() != null){
//                    Uri uri = Uri.parse(mData.getLink());
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);
//                }
                getWebView().loadUrl(mData.getLink());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Animator anim = ViewAnimationUtils.createCircularReveal(mDetailButton
                            , mDetailButton.getWidth() / 2, mDetailButton.getHeight() / 2
                            , mDetailButton.getWidth(), 0);
                    anim.setDuration(500L);
                    anim.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Animator anim = ViewAnimationUtils.createCircularReveal(getShareButton()
                                    , mDetailButton.getWidth() / 2, mDetailButton.getHeight() / 2
                                    , 0, mDetailButton.getWidth());
                            anim.setDuration(500L);
                            mDetailButton.setVisibility(View.GONE);
                            getShareButton().setVisibility(View.VISIBLE);
                            anim.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    anim.start();
                } else {
                    AlphaAnimation animation = new AlphaAnimation(1f, 0);
                    animation.setDuration(500L);
                    mDetailButton.setAnimation(animation);
                    mDetailButton.setVisibility(View.GONE);
                    AlphaAnimation animationShare = new AlphaAnimation(0, 1f);
                    animationShare.setDuration(500L);
                    getShareButton().setAnimation(animationShare);
                    getShareButton().setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getWebView().loadDataWithBaseURL("", mData.getDescription(), "text/html", "UTF-8", "");
        mPageDataTitle.setText(mData.getTitle());
    }

    public static Bundle makeArgument(FeedMessage data, @Nullable int textColor, @Nullable int bgColor){
        Bundle b = new Bundle();
        b.putParcelable(FeedMessage.class.getName(), data);
        b.putInt("textColor", textColor);
        b.putInt("bgColor", bgColor);
        return b;
    }
}
