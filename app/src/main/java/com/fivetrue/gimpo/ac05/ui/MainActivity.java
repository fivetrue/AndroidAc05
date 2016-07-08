package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.analytics.Event;
import com.fivetrue.gimpo.ac05.analytics.GoogleAnalytics;
import com.fivetrue.gimpo.ac05.analytics.UserProperty;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.MainPageDataRequest;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.vo.IPageData;
import com.fivetrue.gimpo.ac05.vo.rss.FeedMessage;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;
import com.fivetrue.gimpo.ac05.service.notification.NotificationHelper;
import com.fivetrue.gimpo.ac05.ui.adapter.pager.MainFragmentViewPagerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseDataListFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.WebViewFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.detail.PageDataDetailFragment;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.data.MainDataEntry;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;
import com.fivetrue.gimpo.ac05.widget.PagerSlidingTabStrip;
import com.google.gson.reflect.TypeToken;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class MainActivity extends DrawerActivity implements BaseDataListFragment.IBaseDataListListener {

    private static final String TAG = "MainActivity";

    private PagerSlidingTabStrip mTabPager = null;
    private ViewPager mViewPager = null;

    private View mBackground = null;

    private ProgressBar mProgressBar = null;

    private MainFragmentViewPagerAdapter mAdapter = null;

    private UserInfo mUserInfo = null;
    private MainPageDataRequest mPageDataReqeust = null;

    private boolean mDoubleClickBack = false;

    private ConfigPreferenceManager mConfigPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        checkUserInfo();
        mProgressBar.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().request(mPageDataReqeust);
        if(getIntent() != null && getIntent().getDataString() != null){
            String data = getIntent().getData().toString();
            if(data != null && (data.startsWith("http") || data.startsWith("https"))){
                NotificationData notificationData = getIntent().getParcelableExtra(NotificationHelper.KEY_NOTIFICATION_PARCELABLE);
                if(notificationData != null){
                    data = String.format(Constants.API_CHECK_REDIRECT, notificationData.getUri()
                            , notificationData.getMulticast_id()
                            , mUserInfo.getEmail());
                }
                showWebviewFragment(getString(R.string.notice), data);
            }
        }

        GoogleAnalytics.getInstance().sendLogEventProperties(Event.EnterMainActivity);
    }

    private void initData(){
        mUserInfo = getIntent().getParcelableExtra(UserInfo.class.getName());
        mConfigPref = new ConfigPreferenceManager(this);
        mPageDataReqeust = new MainPageDataRequest(this, basePageDataApiResponse);
    }

    private void initView(){
        mBackground = findViewById(R.id.view_main_background);
        mTabPager = (PagerSlidingTabStrip) findViewById(R.id.tab_main_pager_strip);
        mViewPager = (ViewPager) findViewById(R.id.vp_main_pager);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_main);
    }

    private void checkUserInfo(){
        if(mUserInfo != null){
            if(mUserInfo.getDistrict() <= 0){
                Intent intent = new Intent(this, UserInfoInputActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }else{
                GoogleAnalytics.getInstance().setUserProperties(UserProperty.makeProperty(UserProperty.DISTRICT, mUserInfo.getDistrict() + ""));
            }
        }
    }

    private void setData(MainDataEntry data){
        if(mAdapter == null){
            mAdapter = new MainFragmentViewPagerAdapter(getSupportFragmentManager(), data);
            mViewPager.setAdapter(mAdapter);
            mTabPager.setViewPager(mViewPager);
        }
    }

    private void showPageDetailFragment(String title, FeedMessage message, Integer textColor, Integer bgColor){
        int enterAnim = R.anim.enter_translate_up;
        int exitAnim = R.anim.exit_translate_down;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            enterAnim = R.anim.enter_smooth;
            exitAnim = R.anim.exit_smooth;
        }

        BaseFragment f = addFragment(PageDataDetailFragment.class, PageDataDetailFragment.makeArgument(message, textColor, bgColor)
                , getFragmentAnchorLayoutID(), enterAnim, exitAnim, true);
        if(f != null){
            getFtActionBar().setTitle(title);
        }
    }

    private void showWebviewFragment(String title, String url){

        int enterAnim = R.anim.enter_translate_up;
        int exitAnim = R.anim.exit_translate_down;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            enterAnim = R.anim.enter_smooth;
            exitAnim = R.anim.exit_smooth;
        }

        Bundle argument = new Bundle();
        argument.putString("url", url);
        BaseFragment f = addFragment(WebViewFragment.class, argument
                , getFragmentAnchorLayoutID(), enterAnim, exitAnim, true);
        if(f != null){
            getFtActionBar().setTitle(title);
        }
    }

    private BaseApiResponse<MainDataEntry> basePageDataApiResponse = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<MainDataEntry>() {
        @Override
        public void onResponse(BaseApiResponse<MainDataEntry> response) {
            mPageDataReqeust.setCache(true);
            setData(response.getData());
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onError(VolleyError error) {
            mProgressBar.setVisibility(View.GONE);
        }
    }, new TypeToken<MainDataEntry>(){}.getType());

    @Override
    public void onClickPageData(String title, IPageData data, Integer textColor, Integer bgColor) {
        if(mUserInfo == null){
            mUserInfo = mConfigPref.getUserInfo();
        }
        if(data != null){
            if(data instanceof FeedMessage){
                showPageDetailFragment(title, (FeedMessage) data, textColor, bgColor);
            }else{
                String url = null;
                if(data instanceof NotificationData && mUserInfo != null){
                    url = String.format(Constants.API_CHECK_REDIRECT, ((NotificationData)data).getUri()
                            , ((NotificationData)data).getMulticast_id()
                            , mUserInfo.getEmail());
                }else{
                    url = data.getUrl();
                }
                showWebviewFragment(title, url);

            }
            Log.i(TAG, "onClickPageData: " + data.toString());
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//        int backgroundHeight = mBackground.getHeight();
//        backgroundHeight = (int) (backgroundHeight * 0.1);
//        mBackground.scrollTo(0, (int) -(backgroundHeight * scrollOffset));
        mBackground.scrollTo(0, dy);
    }

    @Override
    public void onBackPressed() {
        if(isOpenMenu()){
            super.onBackPressed();
        }else if(getCurrentFragmentManager().getBackStackEntryCount() > 0){
            Fragment f = getCurrentFragmentManager().findFragmentById(getFragmentAnchorLayoutID());
            if(f != null && f instanceof WebViewFragment && ((WebViewFragment) f).canGoback()){
                ((WebViewFragment) f).goBack();
            }else{
                super.onBackPressed();
            }
        }else{
            if(!mDoubleClickBack){
                mDoubleClickBack = true;
                Toast.makeText(MainActivity.this, R.string.exit_click_back_twice, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDoubleClickBack = false;
                    }
                }, 2000L);
            }else{
                GoogleAnalytics.getInstance().sendLogEventProperties(Event.ExitAppByBackPress);
                super.onBackPressed();
            }
        }
    }
}
