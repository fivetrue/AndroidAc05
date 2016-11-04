package com.fivetrue.gimpo.ac05.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseListAdapter;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.AdminActivity;

import java.util.List;

/**
 * Created by kwonojin on 2016. 10. 13..
 */

public class NetworkImageListAdapter extends BaseListAdapter<String, NetworkImageListAdapter.NetworkImageHolder> {


    public NetworkImageListAdapter(Context context, List<String> data) {
        super(context, data, R.layout.item_network_image_list_item);
    }

    @Override
    protected NetworkImageHolder makeHolder(View view, int postion) {
        NetworkImageHolder holder = new NetworkImageHolder(view);
        return holder;
    }

    @Override
    protected void initView(NetworkImageHolder holder, int position, View convertView, ViewGroup parent) {
        holder.image.setDefaultImageResId(R.drawable.default_image);
        holder.image.setImageUrl(getItem(position), ImageLoadManager.getImageLoader());
    }

    public static class NetworkImageHolder{

        public ViewGroup layout;
        public NetworkImageView image;

        public NetworkImageHolder(View itemView) {
            layout = (ViewGroup) itemView.findViewById(R.id.layout_item_network_image_list);
            image = (NetworkImageView) itemView.findViewById(R.id.iv_item_network_image_list);
        }
    }
}
