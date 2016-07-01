package com.fivetrue.gimpo.ac05.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.adapter.InfomationImageRecyclerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.ImageDetailFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.WebViewFragment;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class InfomationImageActivity extends DrawerActivity{

    private static final String TAG = "InfomationImageActivity";

    private RecyclerView mRecyclerView = null;

    private InfomationImageRecyclerAdapter mAdapter = null;

    private AppConfig mAppConfig = null;
    private ArrayList<String> mInfomationUrls = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);
        initData();
        initView();
    }

    private void initView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_infomations);

    }

    private void initData(){
        mAppConfig = ((ApplicationEX)getApplicationContext()).getAppConfig();
        if(mAppConfig != null){
            mInfomationUrls = mAppConfig.getInfomationImageUrlList();
        }
    }

    public void setData(ArrayList<String> datas){
        if(datas != null){
            if(mAdapter == null){
                mAdapter = new InfomationImageRecyclerAdapter(datas, onInfomationImageItemClickListener);
                mRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.setData(datas);
            }
        }else{
            //TODO : Update data is empty
        }
    }

    @Override
    public void onBackPressed() {
        Fragment f = getCurrentFragmentManager().findFragmentById(getFragmentAnchorLayoutID());
        if(f != null && f instanceof WebViewFragment){
            if(((WebViewFragment) f).canGoback()){
                ((WebViewFragment) f).goBack();
                return;
            }
        }
        super.onBackPressed();
    }

    private InfomationImageRecyclerAdapter.OnInfomationImageItemClickListener onInfomationImageItemClickListener = new InfomationImageRecyclerAdapter.OnInfomationImageItemClickListener() {
        @Override
        public void onClick(View view, Bitmap bitmap) {
            if(bitmap != null && !bitmap.isRecycled()){
                Bundle b = new Bundle();
                b.putParcelable(Bitmap.class.getName(), bitmap);
                addFragment(ImageDetailFragment.class, b, getBaseLayoutContainer().getId(), R.anim.enter_transform, R.anim.exit_transform, true);
            }
        }
    };
}
