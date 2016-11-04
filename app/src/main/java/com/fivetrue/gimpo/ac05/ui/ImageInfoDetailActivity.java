package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.firebase.model.ImageInfo;
import com.fivetrue.gimpo.ac05.ui.adapter.NetworkImageListAdapter;


/**
 * Created by kwonojin on 16. 6. 7..
 */
public class ImageInfoDetailActivity extends BaseActivity {

    private static final String TAG = "ImageInfoDetailActivity";

    private ListView mListView;
    private NetworkImageListAdapter mAdapter;


    private ImageInfo mImageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_info_detail);
        initData();
        initView();
        loadData();
    }

    private void initData(){
        mImageInfo = getIntent().getParcelableExtra(ImageInfo.class.getName());
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.lv_image_info_detail);

        getSupportActionBar().setTitle(mImageInfo.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mAdapter != null && mAdapter.getCount() > position){
                    Intent intent = new Intent(ImageInfoDetailActivity.this, ImageDetailActivity.class);
                    intent.putExtra("url", mAdapter.getItem(position));
                    startActivity(intent);
                }
            }
        });
    }

    private void loadData(){
        if(mImageInfo != null && mImageInfo.images != null && mImageInfo.images.size() > 0){
            if(mAdapter == null){
                mAdapter = new NetworkImageListAdapter(this, mImageInfo.images);
                mListView.setAdapter(mAdapter);
            }else{
                mAdapter.setData(mImageInfo.images);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean transitionModeWhenFinish() {
        return true;
    }

}
