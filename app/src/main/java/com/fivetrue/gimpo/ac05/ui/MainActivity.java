package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.fivetrueandroid.ui.fragment.BaseFragmentImp;
import com.fivetrue.fivetrueandroid.utils.AppUtils;
import com.fivetrue.fivetrueandroid.view.CircleImageView;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.database.ChatLocalDB;
import com.fivetrue.gimpo.ac05.firebase.database.ImageInfoDatabase;
import com.fivetrue.gimpo.ac05.firebase.database.RssMessageDatabase;
import com.fivetrue.gimpo.ac05.firebase.database.ScrapContentDatabase;
import com.fivetrue.gimpo.ac05.firebase.database.TownNewsDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.ImageInfo;
import com.fivetrue.gimpo.ac05.firebase.model.RssMessage;
import com.fivetrue.gimpo.ac05.firebase.model.ScrapContent;
import com.fivetrue.gimpo.ac05.firebase.model.TownNews;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.rss.RSSFeedParser;
import com.fivetrue.gimpo.ac05.service.FirebaseService;
import com.fivetrue.gimpo.ac05.service.GcmMessage;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;
import com.fivetrue.gimpo.ac05.ui.adapter.MainItemListAdapter;
import com.fivetrue.gimpo.ac05.vo.data.MainItem;
import com.fivetrue.gimpo.ac05.vo.rss.Feed;
import com.fivetrue.gimpo.ac05.vo.rss.FeedMessage;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class MainActivity extends FirebaseBaseAcitivty implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout = null;

    private RecyclerView mRecyclerView;

    private View mNavUserNoti;
    private View mNavNewIcon;
    private CircleImageView mNavImage = null;
    private TextView mNavAccount = null;
    private TextView mNavName = null;
    private TextView mNavDistrict = null;

    private boolean mDoubleClickBack = false;

    private ConfigPreferenceManager mConfigPref = null;

    private InterstitialAd mInterstitialAd;
    private AdRequest mAdRequest;

    private MainItemListAdapter mAdapter;
    private BaseItemListAdapter<ScrapContent> mScrapContentAdapter;
    private BaseItemListAdapter<TownNews> mTownNewsAdapter;

    private TownNewsDatabase mTownNewsDatabase;
    private ScrapContentDatabase mScrapContentDatabase;
    private RssMessageDatabase mRssDatabase;
    private ImageInfoDatabase mImageInfoDatabase;

    private ChatLocalDB mChatLocalDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initAds();
        /**
         * Check Serivce
         */
        if (!AppUtils.isServiceRunning(this, FirebaseService.class)) {
            startService(new Intent(getApplicationContext(), FirebaseService.class));
        }

        DefaultPreferenceManager.getInstance(this).setOpenFirst(false);

        /**
         * Check intent
         */
        if(getIntent() != null && getIntent().getAction() != null){
            if(getIntent().getAction().equals(Constants.ACTION_NOTIFICATION)){
                GcmMessage gcmMessage = new GcmMessage(getIntent());
                if(!TextUtils.isEmpty(gcmMessage.url)){
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("title", gcmMessage.title);
                    intent.putExtra("url", gcmMessage.url);
                    startActivity(intent);
                }
            }
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_UPDATE_CAPTURED_PAGE);

        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUserInfo();
    }

    private void initData() {
        mConfigPref = new ConfigPreferenceManager(this);

        /**
         * Firebase Database
         */
        mTownNewsDatabase = new TownNewsDatabase();
        mScrapContentDatabase = new ScrapContentDatabase();
        mRssDatabase = new RssMessageDatabase();
        mImageInfoDatabase = new ImageInfoDatabase();

        /**
         * Local Database
         */
        mChatLocalDB = new ChatLocalDB(this);

        mAdapter = new MainItemListAdapter(new ArrayList<MainItem>());
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<MainItem, MainItemListAdapter.BaseItemViewHolder>() {
            @Override
            public void onClickItem(MainItemListAdapter.BaseItemViewHolder holder, MainItem data) {
                Intent intent = new Intent(MainActivity.this, data.targetClass);
                intent.putExtra("title", data.title);
                startActivity(intent);
            }
        });

        mScrapContentAdapter = new BaseItemListAdapter<ScrapContent>(new ArrayList<ScrapContent>(), R.layout.item_base_list_item_grid);
        mScrapContentAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<ScrapContent, BaseItemListAdapter.BaseItemViewHolder>() {
            @Override
            public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, ScrapContent data) {
                Intent intent = new Intent(MainActivity.this, ScrapContentActivity.class);
                intent.putExtra(ScrapContent.class.getName(), data);
                startActivity(intent);
            }
        });

        mTownNewsAdapter = new BaseItemListAdapter<TownNews>(new ArrayList<TownNews>(), R.layout.item_base_list_item_grid);
        mTownNewsAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<TownNews, BaseItemListAdapter.BaseItemViewHolder>() {
            @Override
            public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, TownNews data) {
                if(data != null){
                    Intent intent = new Intent(MainActivity.this, TownWebViewActivity.class);
                    intent.putExtra(TownNews.class.getName(), data);
                    startActivity(intent);
                }
            }
        });

        mAdapter.getData().add(new MainItem(getString(R.string.scraped_cafe_content)
                , null , ScrapContentListActivity.class, mScrapContentAdapter));

        mAdapter.getData().add(new MainItem(getString(R.string.town_news)
                    , null , TownDataListActivity.class, mTownNewsAdapter));
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

        mNavNewIcon.setVisibility(mChatLocalDB.hasNewChatMessage(Constants.PERSON_MESSAGE_ID)
                ? View.VISIBLE : View.GONE);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new FadeInAnimator());
        mRecyclerView.setAdapter(mAdapter);

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

    private void loadData(){
        /**
         * load ScrapContent
         */
        mScrapContentDatabase.getReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ScrapContent content = dataSnapshot.getValue(ScrapContent.class);
                content.key = dataSnapshot.getKey();
                mScrapContentAdapter.getData().add(0, content);
                mScrapContentAdapter.notifyItemInserted(0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    ScrapContent content = dataSnapshot.getValue(ScrapContent.class);
                    if(mScrapContentAdapter.getData().contains(content)){
                        int index = mScrapContentAdapter.getData().indexOf(content);
                        if(index > -1){
                            mScrapContentAdapter.getData().remove(index);
                            mScrapContentAdapter.notifyItemRemoved(index);
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mTownNewsDatabase.getReference().orderByChild("date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TownNews content = dataSnapshot.getValue(TownNews.class);
                content.key = dataSnapshot.getKey();
                mTownNewsAdapter.getData().add(0, content);
                mTownNewsAdapter.notifyItemInserted(0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    TownNews content = dataSnapshot.getValue(TownNews.class);
                    if(mTownNewsAdapter.getData().contains(content)){
                        int index = mTownNewsAdapter.getData().indexOf(content);
                        if(index > -1){
                            mTownNewsAdapter.getData().remove(index);
                            mTownNewsAdapter.notifyItemRemoved(index);
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRssDatabase.getReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                onReceivedRssMessage(dataSnapshot.getValue(RssMessage.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mImageInfoDatabase.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null && dataSnapshot.getValue() != null){
                    if(dataSnapshot.getChildrenCount() > 0){
                        ArrayList<ImageInfo> imageInfos = new ArrayList<ImageInfo>();
                        for(DataSnapshot s : dataSnapshot.getChildren()){
                            imageInfos.add(s.getValue(ImageInfo.class));
                        }
                        BaseItemListAdapter<ImageInfo> adapter = new BaseItemListAdapter<>(imageInfos, R.layout.item_base_list_item_grid);
                        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<ImageInfo, BaseItemListAdapter.BaseItemViewHolder>() {
                            @Override
                            public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, ImageInfo data) {
                                Intent intent = new Intent(MainActivity.this, ImageInfoDetailActivity.class);
                                intent.putExtra(ImageInfo.class.getName(), data);
                                startActivityWithClipRevealAnimation(intent, holder.layout);
                            }
                        });
                        mAdapter.getData().add(new MainItem(getString(R.string.camera_photo)
                                , null , ImageInfoListActivity.class, adapter));
                        mAdapter.notifyItemInserted(mAdapter.getCount() - 1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        List<TownNews> townNewsList = mTownNewsLocalDB.getTownNews(true);
//        if(townNewsList != null && townNewsList.size() > 0){
//            BaseItemListAdapter<TownNews> adapter = new BaseItemListAdapter<>(townNewsList, R.layout.item_base_list_item_grid);
//            adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<TownNews, BaseItemListAdapter.BaseItemViewHolder>() {
//                @Override
//                public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, TownNews data) {
//                    if(data != null){
//                        Intent intent = new Intent(MainActivity.this, TownWebViewActivity.class);
//                        intent.putExtra(TownNews.class.getName(), data);
//                        startActivity(intent);
//                    }
//                }
//            });
//            mAdapter.getData().add(1, new MainItem(getString(R.string.town_news)
//                    , null , TownDataListActivity.class, adapter));
//            mAdapter.notifyItemInserted(1);
//        }
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

    private void onReceivedRssMessage(final RssMessage message){
        if(message != null){
            new RSSFeedParser(message.rssPath, new RSSFeedParser.OnLoadFeedListener() {
                @Override
                public void onLoad(Feed feed) {
                    if(feed != null && feed.getMessages() != null && feed.getMessages().size() > 0){
                        BaseItemListAdapter<FeedMessage> adapter = new BaseItemListAdapter<>(feed.getMessages(), R.layout.item_base_list_item_grid);
                        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<FeedMessage, BaseItemListAdapter.BaseItemViewHolder>() {
                            @Override
                            public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, FeedMessage data) {
                                if(data != null){
                                    Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                                    intent.putExtra("title", data.getTitle());
                                    intent.putExtra("image", data.getImageUrl());
                                    intent.putExtra("url", data.getUrl());
                                    startActivity(intent);
                                }
                            }
                        });
                        mAdapter.getData().add(new MainItem(message.name
                                , null , FeedListActivity.class, adapter));
                        mAdapter.notifyItemInserted(mAdapter.getCount() - 1);
                    }
                }
            }).readFeed();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cafe : {
                Intent intent = new Intent(this, CafeActivity.class);
                startActivity(intent);
            }
            return true;
            case R.id.action_person : {
                Intent intent = new Intent(this, PersonalActivity.class);
                startActivity(intent);
            }
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

                case R.id.nav_gallery :{
                    Intent intent = new Intent(this, ImageInfoListActivity.class);
                    intent.putExtra("title", getString(R.string.camera_photo));
                    startActivity(intent);
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

    @Override
    protected void onReceivedScrapContent(ScrapContent scrapContent) {
        super.onReceivedScrapContent(scrapContent);
        if(mScrapContentAdapter != null && scrapContent != null){
            mScrapContentAdapter.getData().add(0, scrapContent);
            mScrapContentAdapter.notifyItemInserted(0);
        }
    }

    @Override
    protected void onRefreshData() {
        super.onRefreshData();
//        if(mScrapContentAdapter != null && mScrapLocalDB != null){
//            mScrapContentAdapter.setData(mScrapLocalDB.getScrapContent(true));
//            mScrapContentAdapter.notifyDataSetChanged();
//        }
    }
}
