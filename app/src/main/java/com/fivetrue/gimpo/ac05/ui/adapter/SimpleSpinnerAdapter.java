package com.fivetrue.gimpo.ac05.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;

import java.util.List;

/**
 * Created by kwonojin on 16. 6. 25..
 */
public class SimpleSpinnerAdapter extends BaseListAdapter <String, SimpleSpinnerAdapter.Holder>{


    public SimpleSpinnerAdapter(Context context, List<String> data) {
        super(context, data, R.layout.item_simple_spinner);
    }

    @Override
    protected Holder makeHolder(View view) {
        Holder holder = new Holder();
        holder.text = (TextView) view.findViewById(R.id.tv_item_simple_spinner);
        return holder;
    }

    @Override
    protected void initView(Holder holder, int position, View convertView, ViewGroup parent) {
        String data = getItem(position);
        if(holder != null){
            holder.text.setText(data + (position > 0 ? " 동" : ""));
        }
    }

    protected static final class Holder{
        TextView text = null;
    }
}
