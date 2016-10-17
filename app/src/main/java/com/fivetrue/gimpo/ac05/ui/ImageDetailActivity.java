package com.fivetrue.gimpo.ac05.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.fivetrueandroid.view.DetailPhotoView;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class ImageDetailActivity extends BaseActivity {

    private static final String TAG = "ImageDetailActivity";

    private DetailPhotoView mImageView = null;
    private ProgressBar mProgress = null;

    private String mImageUrl = null;
    private Bitmap mBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        initData();
        initView();
    }

    private void initView(){
        mImageView = (DetailPhotoView) findViewById(R.id.iv_detail_image);
        mProgress = (ProgressBar) findViewById(R.id.pb_detail_image);

        if(mBitmap == null){
            ImageLoadManager.getInstance().loadImageUrl(mImageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    mProgress.setVisibility(View.GONE);
                    if(response != null && response.getBitmap() != null){
                        mBitmap = response.getBitmap();
                        setBitmap(mBitmap);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgress.setVisibility(View.GONE);
                }
            });
        }else{
            mProgress.setVisibility(View.GONE);
            setBitmap(mBitmap);
        }
    }

    private void setBitmap(Bitmap bitmap){
        if(bitmap != null && !bitmap.isRecycled()){
            mImageView.setImageBitmap(bitmap);
        }
    }

    private void initData(){
        mBitmap = getIntent().getParcelableExtra(Bitmap.class.getName());
        if(mBitmap == null){
            mImageUrl = getIntent().getStringExtra("url");
        }
    }

}
