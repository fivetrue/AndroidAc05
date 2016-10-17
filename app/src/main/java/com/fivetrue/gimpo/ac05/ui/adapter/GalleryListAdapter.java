package com.fivetrue.gimpo.ac05.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.fivetrueandroid.view.CircleImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.chatting.GalleryMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kwonojin on 2016. 9. 28..
 */

public class GalleryListAdapter extends BaseRecyclerAdapter<GalleryMessage, GalleryListAdapter.GalleryItemView> {

    private static final String TAG = "GalleryListAdapter";

    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");

    public GalleryListAdapter(List<GalleryMessage> data) {
        super(data, R.layout.item_gallery_list);
    }

    @Override
    protected GalleryItemView makeHolder(View view) {
        GalleryItemView holder = new GalleryItemView(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GalleryItemView holder, int position) {
        if(holder != null){
            final GalleryMessage item = getItem(position);

            holder.image.setImageUrl(item.image, ImageLoadManager.getImageLoader());
            holder.userImage.setImageUrl(item.userImage);
            holder.userName.setText(item.getUser());
            holder.message.setText(item.message);
            holder.date.setText(mSdf.format(new Date(item.createTime)));
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(holder, item);
                }
            });
        }
    }

    public static final class GalleryItemView extends RecyclerView.ViewHolder{

        public final View layout;
        public final NetworkImageView image;
        public final CircleImageView userImage;
        public final TextView userName;
        public final TextView message;
        public final TextView date;

        public GalleryItemView(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_gallery_container);
            image = (NetworkImageView) itemView.findViewById(R.id.iv_item_gallery_image);
            userImage = (CircleImageView) itemView.findViewById(R.id.iv_item_gallery_user_image);
            message = (TextView) itemView.findViewById(R.id.iv_item_gallery_user_message);
            userName = (TextView) itemView.findViewById(R.id.iv_item_gallery_user_id);
            date = (TextView) itemView.findViewById(R.id.iv_item_gallery_user_date);
        }
    }
}
