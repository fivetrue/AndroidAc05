package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.fivetrueandroid.BuildConfig;
import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.ErrorCode;
import com.fivetrue.fivetrueandroid.net.NetworkManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.fivetrueandroid.ui.fragment.BaseFragmentImp;
import com.fivetrue.fivetrueandroid.utils.AppUtils;
import com.fivetrue.fivetrueandroid.view.CircleImageView;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.database.ChatMessageDatabase;
import com.fivetrue.gimpo.ac05.net.request.MainPageDataRequest;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.rss.RSSFeedParser;
import com.fivetrue.gimpo.ac05.service.FirebaseService;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;
import com.fivetrue.gimpo.ac05.vo.IBaseItem;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfoEntry;
import com.fivetrue.gimpo.ac05.vo.data.MainDataEntry;
import com.fivetrue.gimpo.ac05.vo.data.PageData;
import com.fivetrue.gimpo.ac05.vo.data.TownDataEntry;
import com.fivetrue.gimpo.ac05.vo.rss.Feed;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout = null;

    private NestedScrollView mScrollView = null;
    private LinearLayout mLayoutMainContainer = null;

    private View mNavUserNoti;
    private View mNavNewIcon;
    private CircleImageView mNavImage = null;
    private TextView mNavAccount = null;
    private TextView mNavName = null;
    private TextView mNavDistrict = null;

    private ProgressBar mProgressBar = null;

    private MainPageDataRequest mMainDataRequest = null;

    private boolean mDoubleClickBack = false;

    private ConfigPreferenceManager mConfigPref = null;

    private InterstitialAd mInterstitialAd;
    private AdRequest mAdRequest;

    private ChatMessageDatabase mChatMessageDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initAds();
        if (getIntent() != null && getIntent().getData() != null) {
            DeepLinkManager.goLink(this, getIntent());
        }
        /**
         * request main datas.
         */
        mProgressBar.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().request(mMainDataRequest);
        if (!AppUtils.isServiceRunning(this, FirebaseService.class)) {
            startService(new Intent(getApplicationContext(), FirebaseService.class));
        }
        DefaultPreferenceManager.getInstance(this).setOpenFirst(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUserInfo();
    }

    private void initData() {
        mConfigPref = new ConfigPreferenceManager(this);
        mMainDataRequest = new MainPageDataRequest(this, baseMainDataEntryOnResponseListener);
        mMainDataRequest.setPage(0);
        mMainDataRequest.setCount(6);
        mChatMessageDatabase = new ChatMessageDatabase(this);
    }

    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        mNavUserNoti = headerView.findViewById(R.id.layout_nav_noti);
        mNavNewIcon = headerView.findViewById(R.id.iv_nav_noti_new);
        mNavImage = (CircleImageView) headerView.findViewById(R.id.iv_nav_header);
        mNavAccount = (TextView) headerView.findViewById(R.id.tv_nav_header_name_account);
        mNavName = (TextView) headerView.findViewById(R.id.tv_nav_header_name);
        mNavDistrict = (TextView) headerView.findViewById(R.id.tv_nav_header_district);

        mNavNewIcon.setVisibility(mChatMessageDatabase.hasNewChatMessage(Constants.PERSON_MESSAGE_NOTIFICATION_ID)
                ? View.VISIBLE : View.GONE);

        mScrollView = (NestedScrollView) findViewById(R.id.sv_main);
        mLayoutMainContainer = (LinearLayout) findViewById(R.id.layout_main_container);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_main);

        mNavUserNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initAds(){

        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }
        });
        mInterstitialAd.setAdUnitId(getString(R.string.ad_id));
        mAdRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(mAdRequest);
    }

    private void updateUserInfo(){
        if(mNavImage != null && mNavAccount != null && mNavName != null && mNavDistrict != null){
            if(mConfigPref.getUserInfo() != null){
                mNavImage.setImageUrl(mConfigPref.getUserInfo().profileImage);
                mNavAccount.setText(mConfigPref.getUserInfo().email);
                mNavName.setText(mConfigPref.getUserInfo().getDisplayName());
                if(mConfigPref.getUserInfo().district > 0){
                    mNavDistrict.setText(mConfigPref.getUserInfo().district + " 동");
                }
            }else{
                Log.d(TAG, "updateUserInfo() called" + "UserData null");
            }
        }
    }

    protected boolean closeDrawer() {
        boolean b = false;
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            b = true;
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return b;
    }

    @Override
    public void onBackPressed() {
        if (closeDrawer()) {
            return;
        } else {
            if (!mDoubleClickBack) {
                mDoubleClickBack = true;
                Toast.makeText(MainActivity.this, R.string.exit_click_back_twice, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDoubleClickBack = false;
                    }
                }, 2000L);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected int getFragmentAnchorLayoutID() {
        return R.id.layout_main_fragment_anchor;
    }

    private void addRecyclerContainer(ArrayList<? extends IBaseItem> list, String title, int leftDrawable, View.OnClickListener onClickCategory) {
        if (list != null && list.size() > 0 && title != null) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_recycler_container, null);
            View titleContainer = view.findViewById(R.id.layout_recycler_container_title);
            TextView tv = (TextView) view.findViewById(R.id.tv_recycler_container_title);
            ImageView icon = (ImageView) view.findViewById(R.id.iv_recycler_container_title_icon);
            RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv_recycler_conatainer_list);
            tv.setText(title);
            icon.setImageResource(leftDrawable);
            titleContainer.setOnClickListener(onClickCategory);
            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rv.setItemAnimator(new SlideInRightAnimator());
            BaseItemListAdapter adapter = new BaseItemListAdapter<>(list, R.layout.item_base_list_item_grid);

            adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<IBaseItem, BaseItemListAdapter.BaseItemViewHolder>() {
                @Override
                public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, IBaseItem data) {
                    if (data != null) {
                        if(data instanceof ImageInfoEntry){
                            if(((ImageInfoEntry) data).getImageInfos().size() > 1){
                                Intent intent = new Intent(MainActivity.this, ImageInfoListActivity.class);
                                intent.putExtra("title", data.getTitle());
                                intent.putExtra("type", ((ImageInfoEntry) data).getImageInfos().get(0).getImageType());
                                startActivityWithClipRevealAnimation(intent, holder.layout);
                            }else{
                                Intent intent = new Intent(MainActivity.this, ImageDetailActivity.class);
                                intent.putExtra("url", data.getImageUrl());
                                startActivity(intent);
                            }
                        }else{
                            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                            intent.putExtra("url", data.getUrl());
                            intent.putExtra("title", data.getContent());
                            intent.putExtra("image", data.getImageUrl());
                            startActivityWithClipRevealAnimation(intent, holder.image);
                        }
                    }
                }
            });

            rv.setAdapter(adapter);
            new LinearSnapHelper().attachToRecyclerView(rv);
            mLayoutMainContainer.addView(view);
        }
    }

    @Override
    public Fragment addFragment(Class<? extends BaseFragmentImp> cls, Bundle arguments, int anchorLayout, int enterAnim, int exitAnim, boolean addBackstack) {
        Fragment f = super.addFragment(cls, arguments, anchorLayout, R.anim.enter_smooth, R.anim.exit_smooth, addBackstack);
        if (f instanceof BaseFragmentImp) {
            getSupportActionBar().setTitle(((BaseFragmentImp) f).getFragmentTitle());
        }
        return f;
    }

    @Override
    protected boolean popFragment(FragmentManager fm) {
        boolean b = super.popFragment(fm);
        if (fm.getBackStackEntryCount() <= 0) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        return b;
    }

    private void setMainData(final MainDataEntry entry) {
        if (entry != null) {
            addRecyclerContainer(entry.getNotices(), getString(R.string.public_notice), R.drawable.ic_public_notification_20dp, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, NotificationDataListActivity.class);
                    intent.putExtra("title", getString(R.string.public_notice));
                    intent.putExtra("type", "1");
                    startActivityWithClipRevealAnimation(intent, v);

                }
            });

            addRecyclerContainer(entry.getImageInfos(), getString(R.string.image_infomation), R.drawable.ic_multi_camera_20dp, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ImageInfoEntryListActivity.class);
                    intent.putExtra("title", getString(R.string.image_infomation));
                    startActivityWithClipRevealAnimation(intent, v);
                }
            });

            addRecyclerContainer(entry.getNotification(), getString(R.string.notice), R.drawable.ic_notification_20dp, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, NotificationDataListActivity.class);
                    intent.putExtra("title", getString(R.string.notice));
                    intent.putExtra("type", "0");
                    startActivityWithClipRevealAnimation(intent, v);
                }
            });

            addRecyclerContainer(entry.getTown().getList(), entry.getTown().getTitle(), R.drawable.ic_info_20dp, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, TownDataListActivity.class);
                    intent.putExtra("title", entry.getTown().getTitle());
                    intent.putExtra(TownDataEntry.class.getName(), entry.getTown());
                    startActivityWithClipRevealAnimation(intent, v);
                }
            });

            for (final PageData data : entry.getPages()) {
                new RSSFeedParser(data.getPageUrl(), new RSSFeedParser.OnLoadFeedListener() {
                    @Override
                    public void onLoad(final Feed feed) {
                        data.setFeed(feed);
                        addRecyclerContainer(feed.getMessages(), feed.getTitle(), R.drawable.ic_document_20dp, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, PageDataListActivity.class);
                                intent.putExtra("title", feed.getTitle());
                                intent.putExtra(PageData.class.getName(), data);
                                startActivityWithClipRevealAnimation(intent, v);
                            }
                        });
                    }
                }).readFeed();
            }
        }
    }

    private BaseApiResponse.OnResponseListener<MainDataEntry> baseMainDataEntryOnResponseListener = new BaseApiResponse.OnResponseListener<MainDataEntry>() {
        @Override
        public void onResponse(BaseApiResponse<MainDataEntry> response) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
            mProgressBar.setVisibility(View.GONE);
            if (response != null) {
                if (response.getErrorCode() == ErrorCode.OK) {
                    if (response.getData() != null) {
                        setMainData(response.getData());
                    }
                } else {
                    Toast.makeText(MainActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            if (BuildConfig.DEBUG) Log.d(TAG, "onError() called with: error = [" + error + "]");
            mProgressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_person :
                Intent intent = new Intent(this, PersonalActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getOptionsMenuResource() {
        return R.menu.menu_main;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        closeDrawer();
        if (item != null) {
            switch (item.getItemId()) {
                case R.id.nav_gram:
                {
                    Intent intent = new Intent(this, GalleryActivity.class);
                    startActivity(intent);
                }
                    return true;

                case R.id.nav_talk:{
                    Intent intent = new Intent(this, ChattingActivity.class);
                    intent.putExtra("type", ChattingActivity.TYPE_CHATTING_PUBLIC);
                    startActivity(intent);
                }
                    return true;

                case R.id.nav_district_talk :{
                    if(mConfigPref.getUserInfo() != null && mConfigPref.getUserInfo().district > 0){
                        Intent intent = new Intent(this, ChattingActivity.class);
                        intent.putExtra("type", ChattingActivity.TYPE_CHATTING_DISTRICT);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(this, UserInfoInputActivity.class);
                        startActivity(intent);
                    }
                }

                    return true;

                case R.id.nav_cafe:{
                    Intent intent = new Intent(this, CafeActivity.class);
                    startActivity(intent);
                }
                    return true;

                case R.id.nav_setting:{
                    Intent intent = new Intent(this, SettingActivity.class);
                    startActivity(intent);
                }
                    return true;
            }
        }
        return false;
    }
}
