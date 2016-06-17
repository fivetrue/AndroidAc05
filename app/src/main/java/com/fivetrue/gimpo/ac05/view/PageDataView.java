package com.fivetrue.gimpo.ac05.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.data.PageData;

/**
 * Created by kwonojin on 16. 2. 17..
 */
public class PageDataView extends RelativeLayout {

    private static final String TAG = "PageDataView";

    private static final int INVALID_VALUE = -1;
    private RelativeLayout mPageDataView = null;
    private NetworkImageView mImageView = null;
    private TextView mTitleText = null;

    private float mWidth = INVALID_VALUE;
    private float mHeight = INVALID_VALUE;
    private float mMargin = INVALID_VALUE;
    private int mColor = INVALID_VALUE;

    public PageDataView(Context context) {
        super(context);
        initView(context);
    }

    public PageDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributeset(context, attrs);
        initView(context);
    }

    private void initAttributeset(Context context, AttributeSet attrs){
        Resources res = context.getResources();
        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomView);
            mWidth = a.getDimension(R.styleable.CustomView_width, res.getDimension(R.dimen.small_item_view_default_width));
            mMargin = a.getDimension(R.styleable.CustomView_margin, res.getDimension(R.dimen.small_item_view_default_margin));
            mColor = a.getColor(R.styleable.CustomView_viewColor, Color.TRANSPARENT);
            a.recycle();
        }
    }



    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.page_data_view, this, true);
        mPageDataView = (RelativeLayout) findViewById(R.id.page_data_view);
        mImageView = (NetworkImageView) findViewById(R.id.iv_page_data_image);
        mTitleText = (TextView) findViewById(R.id.tv_page_data_view_title);

        if(mColor > INVALID_VALUE){
            mPageDataView.setBackgroundColor(mColor);
        }

        if(mWidth > INVALID_VALUE){
            mPageDataView.getLayoutParams().width = (int) mWidth;
        }
        if(mHeight > INVALID_VALUE){
            mPageDataView.getLayoutParams().height = (int) mHeight;
        }
    }

    public void setImageUrl(String url){
        if(mImageView != null){
            mImageView.setImageUrl(url, ImageLoadManager.getImageLoader());
        }
    }

    public void setPageData(PageData data){
        if(data != null){
            setTitle(data.getPageContent());
            if(data.getPageContent() != null){
                String token = "src=\"";
                if(data.getPageContent().contains(token)){
                    int startTokenIndex = data.getPageContent().indexOf(token);
                    String imgUrl = data.getPageContent().substring(startTokenIndex + token.length());
                    imgUrl = imgUrl.substring(0, imgUrl.indexOf("\""));
                    Log.i(TAG, "setPageData: "  + imgUrl);
                    setImageUrl(imgUrl);
                }
            }
        }
    }

    public void setTitle(String title){
        if(title != null && mTitleText != null){
            mTitleText.setVisibility(View.VISIBLE);
            mTitleText.setText(title);
        }
    }


    public NetworkImageView getImageView(){
        return mImageView;
    }

    public TextView getTitleText(){
        return mTitleText;
    }

    @Override
    public void setBackgroundColor(int color) {
        mPageDataView.setBackgroundColor(color);
    }

}
