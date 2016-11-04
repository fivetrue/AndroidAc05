package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.firebase.database.ImageInfoDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.ImageInfo;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * Created by kwonojin on 16. 6. 7..
 */
public class ImageInfoListActivity extends BaseListDataActivity<ImageInfo>{

    private static final String TAG = "ImageInfoEntryListActiv";

    private ImageInfoDatabase mImageInfoDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        loadData();
    }

    private void initData(){
        mImageInfoDatabase = new ImageInfoDatabase();
    }

    private void loadData(){
        showProgressBar();
        mImageInfoDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null && dataSnapshot.getValue() != null){
                    if(dataSnapshot.getChildrenCount() > 0){
                        for(DataSnapshot s : dataSnapshot.getChildren()){
                            if(getAdapter() == null){
                                ArrayList<ImageInfo> list = new ArrayList<ImageInfo>();
                                list.add(s.getValue(ImageInfo.class));
                                setData(list);

                            }else{
                                getAdapter().getData().add(s.getValue(ImageInfo.class));
                                getAdapter().notifyItemInserted(getAdapter().getCount() - 1);
                            }
                        }
                    }
                }
                goneProgressBar();
                onRefreshFinish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                goneProgressBar();
                onRefreshFinish();
            }
        });
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        loadData();
    }

    @Override
    public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, ImageInfo data) {
        super.onClickItem(holder, data);
        if(data != null) {
            Intent intent = new Intent(this, ImageInfoDetailActivity.class);
            intent.putExtra(ImageInfo.class.getName(), data);
            startActivityWithClipRevealAnimation(intent, holder.layout);
        }
    }

    @Override
    protected boolean transitionModeWhenFinish() {
        return true;
    }

    @Override
    protected int getAdapterItemLayout() {
        return R.layout.item_image_list_item_vertical;
    }
}
