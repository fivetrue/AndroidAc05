package com.fivetrue.gimpo.ac05.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.fivetrue.gimpo.ac05.view.SmallItemView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.vo.LeftMenu;



import java.util.List;

/**
 * Created by So on 2016-01-31.
 */
public class LeftMenuListAdapter extends BaseListAdapter<LeftMenu, LeftMenuListAdapter.ViewHolder> {


    public LeftMenuListAdapter(Context context, List<LeftMenu> data) {
        super(context, data, R.layout.item_row_base_grid);
    }

    @Override
    protected ViewHolder makeHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.item = new SmallItemView(getContext());
        ((ViewGroup) view.findViewById(R.id.layout_row_item_base_grid)).addView(holder.item);
        return holder;
    }

    @Override
    protected void initView(ViewHolder holder, int position, View convertView, ViewGroup parent) {
        LeftMenu item = getItem(position);
        if(item != null) {
            holder.item.setLargeText(item.getName());
        }
    }


    protected static class ViewHolder{
        private SmallItemView item = null;
    }
}