package com.fivetrue.gimpo.ac05.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fivetrue.fivetrueandroid.net.BaseApiRequest;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.gimpo.ac05.R;

import java.util.List;

/**
 * Created by kwonojin on 2016. 10. 13..
 */

public class AdminListAdapter extends BaseRecyclerAdapter<BaseApiRequest, AdminListAdapter.AdminViewHolder> {


    public AdminListAdapter(List<BaseApiRequest> data) {
        super(data, R.layout.item_admin_list_item);
    }

    @Override
    protected AdminViewHolder makeHolder(View view) {
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdminViewHolder holder, final int position) {
        holder.text.setText(getItem(position).getUrl());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem(holder, getItem(position));
            }
        });
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder{

        public ViewGroup layout;
        public TextView text;

        public AdminViewHolder(View itemView) {
            super(itemView);

            layout = (ViewGroup) itemView.findViewById(R.id.layout_item_admin_content);
            text = (TextView) itemView.findViewById(R.id.tv_item_admin_text);
        }
    }
}
