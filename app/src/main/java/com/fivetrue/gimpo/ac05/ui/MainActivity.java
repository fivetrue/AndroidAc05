package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.MainPageDataRequest;
import com.fivetrue.gimpo.ac05.rss.FeedMessage;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.data.PageDataListFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.UserInfoInputFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.data.PageDataDetailFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.data.TownDataFragment;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.data.MainDataEntry;
import com.fivetrue.gimpo.ac05.vo.data.PageData;
import com.fivetrue.gimpo.ac05.vo.data.TownDataEntry;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class MainActivity extends DrawerActivity implements PageDataListFragment.OnPageDataClickListener {

    private static final String TAG = "MainActivity";

    private SwipeRefreshLayout mRefreshLayout = null;
    private ScrollView mScrollView = null;
    private View mBackground = null;
    private LinearLayout mMainContainer = null;

    private ViewGroup mTopHoverContainer = null;
    private TextView mTopHoverText = null;


    private ProgressBar mProgressBar = null;

    private UserInfo mUserInfo = null;
    private MainPageDataRequest mPageDataReqeust = null;

    private boolean mDoubleClickBack = false;

    private int [] layouts = {
            R.id.layout_main_page1,
            R.id.layout_main_page2,
            R.id.layout_main_page3,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        checkUserInfo();
        mProgressBar.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().request(mPageDataReqeust);
    }

    private void initData(){
        mUserInfo = getIntent().getParcelableExtra(UserInfo.class.getName());
        mPageDataReqeust = new MainPageDataRequest(this, basePageDataApiResponse);
    }

    private void initView(){
        mBackground = findViewById(R.id.view_main_background);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_main_refresh);
        mScrollView = (ScrollView) findViewById(R.id.layout_main_scroll);
        mMainContainer = (LinearLayout) findViewById(R.id.layout_main_container);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_main);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageDataReqeust.setCache(false);
                NetworkManager.getInstance().request(mPageDataReqeust);
            }
        });

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (mScrollView != null) {
                    scrollY(mScrollView.getScrollY() / (float) mScrollView.getHeight());
                }
            }
        });
    }

    private void scrollY(float scrollOffset){
        int backgroundHeight = mBackground.getHeight();
        backgroundHeight = (int) (backgroundHeight * 0.1);
        mBackground.scrollTo(0, (int) -(backgroundHeight * scrollOffset));
    }

    private void checkUserInfo(){
        if(mUserInfo != null){
            if(TextUtils.isEmpty(mUserInfo.getApartDong())){
                Bundle arg = new Bundle();
                arg.putParcelable(UserInfo.class.getName(), mUserInfo);
                addFragment(UserInfoInputFragment.class, arg,
                        getBaseLayoutContainer().getId(), android.R.anim.fade_in, android.R.anim.fade_out,true);
            }
        }
    }

    private void setData(final MainDataEntry data){
        if(data != null){
            TownDataEntry townDataEntry = data.getTown();
            if(townDataEntry != null){
                Fragment f = getCurrentFragmentManager().findFragmentById(layouts[0]);
                if(f != null && f instanceof TownDataFragment){
                    ((TownDataFragment) f).setData(townDataEntry);
                }else{
                    Bundle argument = new Bundle();
                    argument.putParcelable(TownDataEntry.class.getName(), townDataEntry);
                    TownDataFragment fragment = (TownDataFragment) addFragment(TownDataFragment.class, argument, layouts[0], R.anim.enter_smooth, 0, false);
                }

            }

            final ArrayList<PageData> pageData = data.getPages();
            if(pageData != null){
                for(int i = 0 ; i < pageData.size() ; i ++){
                    if(i < pageData.size()){
                        final int idx = i;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Fragment f = getCurrentFragmentManager().findFragmentById(layouts[(idx + 1)]);
                                if(f != null && f instanceof PageDataListFragment){
                                    ((PageDataListFragment) f).setData(pageData.get(idx));
                                }else{
                                    Bundle argument = new Bundle();
                                    argument.putParcelable(PageData.class.getName(), pageData.get(idx));
                                    PageDataListFragment fragment = (PageDataListFragment) addFragment(PageDataListFragment.class, argument, layouts[idx + 1], R.anim.enter_smooth, 0, false);
                                    fragment.setNestedScrollingEnabled(false);
                                }

                            }
                        }, 500L);
                    }
                }
            }
        }
    }

    private BaseApiResponse<MainDataEntry> basePageDataApiResponse = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<MainDataEntry>() {
        @Override
        public void onResponse(BaseApiResponse<MainDataEntry> response) {
            if(mRefreshLayout != null && mRefreshLayout.isRefreshing()){
                mRefreshLayout.setRefreshing(false);
            }
            mPageDataReqeust.setCache(true);
            setData(response.getData());
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onError(VolleyError error) {
            if(mRefreshLayout != null && mRefreshLayout.isRefreshing()){
                mRefreshLayout.setRefreshing(false);
            }
            mProgressBar.setVisibility(View.GONE);
        }
    }, new TypeToken<MainDataEntry>(){}.getType());

    @Override
    public void onClickPageData(String title, FeedMessage data, Integer textColor, Integer bgColor) {
        BaseFragment f = addFragment(PageDataDetailFragment.class, PageDataDetailFragment.makeArgument(data, textColor, bgColor)
                , getFragmentAnchorLayoutID(), R.anim.enter_translate_up, R.anim.exit_translate_down, true);
        if(f != null){
            getFtActionBar().setTitle(title);
        }
        Log.i(TAG, "onClickPageData: " + data.toString());
    }

    @Override
    public BaseFragment addFragment(Class<? extends BaseFragment> cls, Bundle arguments, int anchorLayout, int enterAnim, int exitAnim, boolean addBackstack) {
        BaseFragment f = super.addFragment(cls, arguments, anchorLayout, enterAnim, exitAnim, addBackstack);
        if(addBackstack){
            getFtActionBar().setHomeAsUp(true, true);
        }
        return f;
    }

    @Override
    protected boolean popFragment(FragmentManager fm) {
        boolean b = super.popFragment(fm);
        if(getCurrentFragmentManager().getBackStackEntryCount() <= 0){
            getFtActionBar().setHomeAsUp(false, true);
            getFtActionBar().setTitle(R.string.app_name);
        }
        return b;
    }

    @Override
    public void onBackPressed() {
        if(getCurrentFragmentManager().getBackStackEntryCount() > 0 || isOpenMenu()){
            super.onBackPressed();
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
                super.onBackPressed();
            }
        }
    }
}
