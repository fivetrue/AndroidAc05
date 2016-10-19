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
import com.fivetrue.gimpo.ac05.vo.user.FirebaseUserInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kwonojin on 2016. 9. 28..
 */

public class UserListAdapter extends BaseRecyclerAdapter<FirebaseUserInfo, UserListAdapter.UserItemView> {

    private static final String TAG = "GalleryListAdapter";

    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");

    public UserListAdapter(List<FirebaseUserInfo> data) {
        super(data, R.layout.item_user_list);
    }

    @Override
    protected UserItemView makeHolder(View view) {
        UserItemView holder = new UserItemView(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final UserItemView holder, int position) {
        if(holder != null){
            final FirebaseUserInfo item = getItem(position);

            holder.image.setImageUrl(item.getPhotoUrl(), ImageLoadManager.getImageLoader());
            holder.userName.setText(item.getName());
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(holder, item);
                }
            });
        }
    }

    public static final class UserItemView extends RecyclerView.ViewHolder{

        public final View layout;
        public final NetworkImageView image;
        public final TextView userName;

        public UserItemView(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_user_container);
            image = (NetworkImageView) itemView.findViewById(R.id.iv_item_user_image);
            userName = (TextView) itemView.findViewById(R.id.tv_item_user_name);
        }
    }
}
