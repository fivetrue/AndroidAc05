package com.fivetrue.gimpo.ac05.ui.fragment.detail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.fivetrueandroid.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.fivetrueandroid.view.DetailPhotoView;


/**
 * Created by HanyLuv on 2016-05-09.
 */
public class ImageDetailFragment extends BaseFragment {


    private DetailPhotoView mDetailImageView;
    private View mView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_detail_image, null);
        mDetailImageView = (DetailPhotoView) mView.findViewById(R.id.iv_fragment_detail_image);
        mView.setOnClickListener(null);
        return mView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bitmap bitmap = getArguments().getParcelable(Bitmap.class.getName());
        if (bitmap != null && !bitmap.isRecycled()) {
            mDetailImageView.setImageBitmap(bitmap);
        }
    }

}
