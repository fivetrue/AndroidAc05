package com.fivetrue.gimpo.ac05.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.image.ImageLoadManager;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class InfomationImageRecyclerAdapter extends BaseRecyclerAdapter<String, InfomationImageRecyclerAdapter.InfomationDataHolder> {

    private static final String TAG = "InfomationImageRecyclerAdapter";

    public interface OnInfomationImageItemClickListener{
        void onClick(View view, Bitmap bitmap);
    }


    private OnInfomationImageItemClickListener mOnClickInfomationClickListener = null;

    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public InfomationImageRecyclerAdapter(List<String> data, OnInfomationImageItemClickListener ll) {
        super(data, R.layout.item_infomation_image_list);
        mOnClickInfomationClickListener = ll;
    }



    @Override
    protected InfomationDataHolder makeHolder(View view) {
        InfomationDataHolder holder = new InfomationDataHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final InfomationDataHolder holder, int position) {
        final String data = getItem(position);
        if (data != null) {
                holder.imageView.setImageUrl(data, ImageLoadManager.getImageLoader());
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickInfomationClickListener != null) {
                        Bitmap bm = ((BitmapDrawable)holder.imageView.getDrawable()).getBitmap();
                        mOnClickInfomationClickListener.onClick(v, bm);
                    }
                }
            });
        }
    }

    public static class InfomationDataHolder extends RecyclerView.ViewHolder{

        protected View container = null;
        protected NetworkImageView imageView = null;

        public InfomationDataHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.infomation_data_view);
            imageView = (NetworkImageView) itemView.findViewById(R.id.iv_item_infomation_image_list_image);
        }
    }
}
