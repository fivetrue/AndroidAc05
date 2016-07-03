package com.fivetrue.gimpo.ac05.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
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
        void onClick(View view, String imageUrl, Bitmap bitmap);
    }


    private OnInfomationImageItemClickListener mOnClickInfomationClickListener = null;

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
            ImageLoadManager.getInstance().loadImageUrl(data, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.progressBar.setVisibility(View.GONE);
                    if(response != null && response.getBitmap() != null && !response.getBitmap().isRecycled()){
                        holder.imageView.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.imageView.setImageBitmap(null);
                }
            });
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickInfomationClickListener != null) {
                        Bitmap bm = ((BitmapDrawable)holder.imageView.getDrawable()).getBitmap();
                        mOnClickInfomationClickListener.onClick(v, data, bm);
                    }
                }
            });
        }
    }

    public static class InfomationDataHolder extends RecyclerView.ViewHolder{

        protected View container = null;
        protected ImageView imageView = null;
//        protected NetworkImageView imageView = null;
        protected ProgressBar progressBar = null;

        public InfomationDataHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.infomation_data_view);
            imageView = (ImageView) itemView.findViewById(R.id.iv_item_infomation_image_list_image);
//            imageView = (NetworkImageView) itemView.findViewById(R.id.iv_item_infomation_image_list_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pb_item_infomation_image_list);
        }
    }
}
