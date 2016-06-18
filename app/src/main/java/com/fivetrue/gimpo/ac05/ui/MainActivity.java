package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.PageDataRequest;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.data.BasePageDataFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.data.HorizontalListPageDataFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.UserInfoInputFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.data.PageDataDetailFragment;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.data.PageData;
import com.fivetrue.gimpo.ac05.vo.data.PageDataEntry;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class MainActivity extends DrawerActivity implements BasePageDataFragment.OnPageDataClickListener {

    private static final String TAG = "MainActivity";

    private SwipeRefreshLayout mRefreshLayout = null;
    private ScrollView mScrollView = null;
    private View mBackground = null;
    private LinearLayout mMainContainer = null;

    private ProgressBar mProgressBar = null;

    private UserInfo mUserInfo = null;
    private PageDataRequest mPageDataReqeust = null;

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
        mPageDataReqeust = new PageDataRequest(this, basePageDataApiResponse);
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
                if(mScrollView != null){
                    scrollY(mScrollView.getScrollY() / (float)mScrollView.getHeight());
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

    private void setData(final ArrayList<PageDataEntry> data){
        if(data != null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0 ; i < data.size() ; i++){
                        if(i < data.size()){
                            Fragment f = getCurrentFragmentManager().findFragmentById(layouts[i % layouts.length]);
                            if(f != null && f instanceof BasePageDataFragment){
                                ((BasePageDataFragment) f).setData(data.get(i));
                            }else{
                                Bundle argument = new Bundle();
                                argument.putParcelable(PageDataEntry.class.getName(), data.get(i));
                                addFragment(HorizontalListPageDataFragment.class, argument, layouts[ i % layouts.length], R.anim.enter_smooth, 0, false);
                            }
                        }
                    }
                }
            }, 500L);
        }
    }

    private BaseApiResponse<ArrayList<PageDataEntry>> basePageDataApiResponse = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<ArrayList<PageDataEntry>>() {
        @Override
        public void onResponse(BaseApiResponse<ArrayList<PageDataEntry>> response) {
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
    }, new TypeToken<ArrayList<PageDataEntry>>(){}.getType());

    @Override
    public void onClickPageData(PageDataEntry entry, PageData pageData, Integer textColor, Integer bgColor) {
        BaseFragment f = addFragment(PageDataDetailFragment.class, PageDataDetailFragment.makeArgument(pageData, textColor, bgColor)
                , getFragmentAnchorLayoutID(), R.anim.enter_translate_up, R.anim.exit_translate_down, true);
        if(f != null){
            getFtActionBar().setTitle(entry.getDataTitle());
        }
        Log.i(TAG, "onClickPageData: " + pageData.toString());
    }

    @Override
    public void onClickPageDetail(PageDataEntry entry) {
        Log.i(TAG, "onClickPageDetail: "  + entry.toString());
        Intent intent = new Intent(this, PageDataListActivity.class);
        intent.putExtra(PageDataEntry.class.getName(), entry);
        startActivity(intent);
    }

    @Override
    public BaseFragment addFragment(Class<? extends BaseFragment> cls, Bundle arguments, int anchorLayout, int enterAnim, int exitAnim, boolean addBackstack) {
        BaseFragment f = super.addFragment(cls, arguments, anchorLayout, enterAnim, exitAnim, addBackstack);
        if(addBackstack){
            getFtActionBar().setHomeAsUp(true);
        }
        return f;
    }

    @Override
    protected boolean popFragment(FragmentManager fm) {
        boolean b = super.popFragment(fm);
        if(getCurrentFragmentManager().getBackStackEntryCount() <= 0){
            getFtActionBar().setHomeAsUp(false);
            getFtActionBar().setTitle(R.string.app_name);
        }
        return b;
    }

    @Override
    public void onBackPressed() {
        if(getCurrentFragmentManager().getBackStackEntryCount() > 0){
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
