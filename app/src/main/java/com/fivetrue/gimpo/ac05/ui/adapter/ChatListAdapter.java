package com.fivetrue.gimpo.ac05.ui.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.fivetrueandroid.view.CircleImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.firebase.model.ChatMessage;
import com.fivetrue.gimpo.ac05.ui.ImageDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kwonojin on 2016. 9. 28..
 */

public class ChatListAdapter extends BaseRecyclerAdapter<ChatMessage, ChatListAdapter.ChatItemViewHolder> {

    private static final String TAG = "BaseItemListAdapter";

    private String mUserId;
    private SimpleDateFormat mSdf = new SimpleDateFormat("MM월 dd일 HH시 mm분");

    public ChatListAdapter(List<ChatMessage> data, String userId) {
        super(data, 0);
        mUserId = userId;
    }

    @Override
    protected ChatItemViewHolder makeHolder(View view) {
        ChatItemViewHolder holder = new ChatItemViewHolder(view);
        return holder;
    }

    @Override
    public ChatItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_chat_list, null);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_chat_list, null);
        }
        return makeHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).user.uid.equals(mUserId) ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(final ChatItemViewHolder holder, int position) {
        if(holder != null){
            final ChatMessage item = getItem(position);

            holder.userImage.setImageUrl(item.user.profileImage);
            holder.date.setText(mSdf.format(new Date(item.updateTime)));
            holder.message.setText(item.message);
            if(item.imageMessage != null){
                holder.messageImage.setImageUrl(item.imageMessage, ImageLoadManager.getImageLoader());
                holder.messageImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ImageDetailActivity.class);
                        intent.putExtra("url", item.imageMessage);
                        v.getContext().startActivity(intent);
                    }
                });
            }
            holder.name.setText(item.user.getDisplayName());
            holder.message.requestLayout();

            holder.userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(holder, item);
                }
            });
        }

    }

    public static final class ChatItemViewHolder extends RecyclerView.ViewHolder{

        public final View chatLayout;
        public final View userInfoLayout;
        public final CircleImageView userImage;
        public final NetworkImageView messageImage;
        public final TextView message;
        public final TextView name;
        public final TextView date;

        public ChatItemViewHolder(View itemView) {
            super(itemView);
            chatLayout = itemView.findViewById(R.id.layout_item_chat_container);
            userInfoLayout = itemView.findViewById(R.id.layout_item_chat_user_info);
            userImage = (CircleImageView) itemView.findViewById(R.id.iv_item_chat_user);
            messageImage = (NetworkImageView) itemView.findViewById(R.id.iv_item_chat_image);
            message = (TextView) itemView.findViewById(R.id.tv_item_chat_message);
            name = (TextView) itemView.findViewById(R.id.tv_item_chat_name);
            date = (TextView) itemView.findViewById(R.id.tv_item_chat_date);
        }
    }
}
